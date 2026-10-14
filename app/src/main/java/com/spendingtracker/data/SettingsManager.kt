package com.spendingtracker.data

import android.content.Context
import android.content.SharedPreferences

object SettingsManager {
    private const val PREF_NAME = "spendtrack_settings"
    private const val KEY_PERSONAL_TARGET = "personal_target"
    private const val KEY_TRAVEL_TARGET = "travel_target"
    private const val KEY_SALARY_DAY = "salary_day"
    private const val KEY_CURRENCY = "currency_symbol"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getPersonalTarget(context: Context): Double {
        return getPrefs(context).getFloat(KEY_PERSONAL_TARGET, 2500f).toDouble()
    }

    fun setPersonalTarget(context: Context, value: Double) {
        getPrefs(context).edit().putFloat(KEY_PERSONAL_TARGET, value.toFloat()).apply()
    }

    fun getTravelTarget(context: Context): Double {
        return getPrefs(context).getFloat(KEY_TRAVEL_TARGET, 1000f).toDouble()
    }

    fun setTravelTarget(context: Context, value: Double) {
        getPrefs(context).edit().putFloat(KEY_TRAVEL_TARGET, value.toFloat()).apply()
    }

    fun getSalaryDay(context: Context): Int {
        return getPrefs(context).getInt(KEY_SALARY_DAY, 10)
    }

    fun setSalaryDay(context: Context, day: Int) {
        getPrefs(context).edit().putInt(KEY_SALARY_DAY, day).apply()
    }

    fun getCurrency(context: Context): String {
        return getPrefs(context).getString(KEY_CURRENCY, "₹") ?: "₹"
    }

    fun setCurrency(context: Context, currency: String) {
        getPrefs(context).edit().putString(KEY_CURRENCY, currency).apply()
    }
}
