package com.gigaworks.tech.calculator.util

import android.content.Context
import android.content.SharedPreferences
import com.gigaworks.tech.calculator.BuildConfig

class AppPreference(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)

    fun setStringPreference(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getStringPreference(key: String, def: String = ""): String {
        return sharedPreferences.getString(key, def)!!
    }

    fun setIntPreference(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getIntPreference(key: String, def: Int = 0): Int {
        return sharedPreferences.getInt(key, def)
    }

    fun setBooleanPreference(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBooleanPreference(key: String, def: Boolean = true): Boolean {
        return sharedPreferences.getBoolean(key, def)
    }

    companion object {
        private const val SHARED_PREF = BuildConfig.APPLICATION_ID
        const val ANGLE_TYPE = "app_angle_type"
        const val NUMBER_SEPARATOR = "app_number_separator"
        const val SMART_CALCULATION = "app_smart_calculation"
        const val ANSWER_PRECISION = "app_answer_precision"
        const val MEMORY = "app_memory_store"
        const val EXPRESSION = "app_expression_string"
        const val APP_THEME = "app_theme"
        const val HISTORY_AUTO_DELETE = "app_history_auto_delete"
        const val ACCENT_THEME = "app_accent_theme"
    }
}