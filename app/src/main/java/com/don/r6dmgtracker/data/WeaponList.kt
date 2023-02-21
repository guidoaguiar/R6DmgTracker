package com.don.r6dmgtracker.data

import com.google.gson.annotations.SerializedName

data class WeaponList(
    @SerializedName("gun") val gun: String,
    @SerializedName("id") val id: Int,
    @SerializedName("newdmg") val newdmg: Int,
    @SerializedName("nstk1") val nstk1: Int,
    @SerializedName("nstk2") val nstk2: Int,
    @SerializedName("nstk3") val nstk3: Int,
    @SerializedName("nttk1") val nttk1: Int,
    @SerializedName("nttk2") val nttk2: Int,
    @SerializedName("nttk3") val nttk3: Int,
    @SerializedName("rof") val rof: Int,
    @SerializedName("tbs") val tbs: Int,
    @SerializedName("type") val type: String,
    @SerializedName("operators") val operators: String
)