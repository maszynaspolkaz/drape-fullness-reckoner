package com.drapefullness.reckoner.data.local

import android.content.Context
import android.content.SharedPreferences

class CurtainPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("drape_reckoner", Context.MODE_PRIVATE)

    fun text(key: String, fallback: String): String = prefs.getString(key, fallback) ?: fallback

    fun putText(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun number(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun putNumber(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun flag(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun putFlag(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun keys(key: String): Set<String> = prefs.getStringSet(key, emptySet()) ?: emptySet()

    fun putKeys(key: String, value: Set<String>) {
        prefs.edit().putStringSet(key, value).apply()
    }
}
