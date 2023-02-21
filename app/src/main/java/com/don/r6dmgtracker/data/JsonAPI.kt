package com.don.r6dmgtracker.data

import retrofit2.Response
import retrofit2.http.GET

interface JsonAPI {

    //https://guidoaguiar.github.io/json/app_weapon_list.json
    //https://guidoaguiar.github.io/json/app_weapon_list.json
    //https://raw.githubusercontent.com/guidoaguiar/guidoaguiar.github.io/main/json/app_weapon_list.json
    @GET("json/app_weapon_list.json")
    suspend fun retrieveWeaponList() : Response<List<WeaponList>>


}
