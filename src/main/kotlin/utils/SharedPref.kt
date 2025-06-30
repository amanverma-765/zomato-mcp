package com.ark.utils

import com.russhwolf.settings.Settings

class SharedPref(private val settings: Settings) {

    fun setString(key: String, value: String) {
        settings.putString(key, value)
    }

    fun  getString(key: String, default: String = ""): String {
        return settings.getString(key, default)
    }

    fun removeKey(key: String) {
        settings.remove(key)
    }

}