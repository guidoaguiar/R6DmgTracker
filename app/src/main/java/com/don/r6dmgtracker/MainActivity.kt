package com.don.r6dmgtracker


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.don.r6dmgtracker.data.JsonAPI
import com.don.r6dmgtracker.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.ceil


lateinit var mAdView : AdView

class MainActivity : AppCompatActivity() {

    fun stkcalc(dmg: Int, health: Int): Int {
        val calc = ceil(((health / dmg).toDouble()))
        val result = calc.toInt()
        println(result)
        return result
    }

    fun sendFeedback() {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:guidoaguiar@live.com")

        //put the Subject in the intent
        mIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback R6 DMG Tracker")
        //put the message in the intent
        mIntent.putExtra(

            Intent.EXTRA_TEXT, "Dear Guido,\n" +
                    "\n" +
                    "I recently used your app and wanted to share some feedback with you. I have been a user for [time period] and overall, I have had a [positive/negative] experience with the app.\n" +
                    "\n" +
                    "Here are a few things I would like to highlight:\n" +
                    "\n" +
                    "[Insert feedback or suggestion here]\n" +
                    "[Insert feedback or suggestion here]\n" +
                    "[Insert feedback or suggestion here]\n" +
                    "I believe these changes would greatly improve the user experience and make the app even more enjoyable to use.\n" +
                    "\n" +
                    "Thank you for taking the time to read my feedback. I look forward to your continued efforts to enhance the app.\n" +
                    "\n" +
                    "Best regards,\n" +
                    "[Your Name]"
        )


        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        } catch (e: Exception) {
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://guidoaguiar.github.io/")
            .addConverterFactory( GsonConverterFactory.create() )
            .build()
            .create( JsonAPI::class.java)
    }

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.mymenu, menu)
        return true
    }
    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {

        if (parent?.parent is FrameLayout) {
            (parent?.parent as View).setBackgroundColor(Color.parseColor("#262626"))
        }

        return super.onCreateView(parent, name, context!!, attrs)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.help -> {
                sendFeedback()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val spinner: Spinner = binding.spinner
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.weapons,
            androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }


        fun isNetworkConnected(): Boolean {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetwork
            val capabilities = cm.getNetworkCapabilities(activeNetwork)
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Network Connection")
        builder.setMessage("the app currently needs internet to retrieve the info from database. Please connect to internet and try again.")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Do nothing
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedWeapon = parent?.getItemAtPosition(position).toString()

                if (isNetworkConnected()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val replyFromJson = retrofit.retrieveWeaponList() //Response: List

                        if (replyFromJson.isSuccessful) {
                            //Deu certo
                            val list = replyFromJson.body()

                            if (list != null) {
                                for (each in list) {
                                    if (each.gun == selectedWeapon) {
                                        withContext(Dispatchers.Main) {
                                            binding.newdmgText.text = each.newdmg.toString()
                                            binding.rofText.text = each.rof.toString()
                                            var dmg = each.newdmg
                                            binding.stk1Text.text = stkcalc(dmg, 100).toString()
                                            binding.stk2Text.text = stkcalc(dmg, 110).toString()
                                            binding.stk3Text.text = stkcalc(dmg, 125).toString()
                                            var gunPng = "_" + each.gun
                                                .lowercase()
                                                .replace('-', '_', ignoreCase = true)
                                                .replace(' ', '_', ignoreCase = true)


                                            val res = getResources()
                                            val resID = res.getIdentifier(
                                                gunPng,
                                                "drawable",
                                                getPackageName()
                                            )
                                            binding.gunImg.setImageDrawable(
                                                ContextCompat.getDrawable(
                                                    this@MainActivity,
                                                    resID
                                                )
                                            )
                                        }
                                        break
                                    }
                                }
                            }
                        }
                    }
                }else {
                    builder.show()
                }

            }

        }

        MobileAds.initialize(this) {}
        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }


}