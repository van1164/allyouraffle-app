package com.allyouraffle.allyouraffle.android.util

import android.content.Context
import com.allyouraffle.allyouraffle.exception.JwtNotFoundException
import com.allyouraffle.allyouraffle.exception.RefreshTokenNotFoundException

class SharedPreference(context: Context) {
    private val authPrefs = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE)

    fun getJwt(): String {
        return authPrefs.getString("JWT", null) ?: run { throw JwtNotFoundException() }
    }

    fun getRefreshToken(): String {
        return authPrefs.getString("REFRESH", null) ?: run { throw RefreshTokenNotFoundException() }
    }

    fun setJwt(jwt: String) {
        authPrefs.edit().putString("JWT", jwt).apply()
    }

    fun setRefresh(refresh: String) {
        authPrefs.edit().putString("REFRESH", refresh).apply()
    }

    fun deleteAllToken() {
        authPrefs.edit().remove("JWT").remove("REFRESH").apply()
    }

}