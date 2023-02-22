package com.don.r6dmgtracker.data

import com.google.gson.annotations.SerializedName

data class WeaponList(
    @SerializedName("gun") val gun: String,
    @SerializedName("id") val id: Int,
    @SerializedName("dmg") val dmg: Int,
    @SerializedName("ebDmg") val ebDmg: Int,
    @SerializedName("rof") val rof: Int,
    @SerializedName("type") val type: String,
    @SerializedName("operators") val operators: String,
    @SerializedName("filename") val filename: String
)