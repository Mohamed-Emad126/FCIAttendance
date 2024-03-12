package com.memad.fciattendance.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesHelper @Inject constructor(
    private val gson: Gson,
    private val sharedPref: SharedPreferences
) {

    fun <T> save(key: String, value: T) {
        with(sharedPref.edit()) {
            putString(key, gson.toJson(value))
            apply()
        }
    }

    fun saveStringSet(key: String, value: Set<String>) {
        with(sharedPref.edit()) {
            putStringSet(key, value)
            apply()
        }
    }

    fun remove(key: String) {
        with(sharedPref.edit().remove(key)) {
            apply()
            commit()
        }
    }

    fun read(key: String, defaultValue: String): String? {
        return sharedPref.getString(key, defaultValue)
    }

    fun readBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun saveBoolean(key: String, value: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun readStringSet(key: String): MutableSet<String>? {
        return sharedPref.getStringSet(key, mutableSetOf())
    }
}