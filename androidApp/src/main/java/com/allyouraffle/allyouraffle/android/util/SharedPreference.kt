package com.allyouraffle.allyouraffle.android.util

import android.content.Context
import com.allyouraffle.allyouraffle.exception.JwtNotFoundException

class SharedPreference(context : Context) {
    private val authPrefs = context.getSharedPreferences("AUTH",Context.MODE_PRIVATE)

    fun getJwt(): String {
        return authPrefs.getString("JWT",null)?:run { throw JwtNotFoundException() }
    }

}