package com.alcidev.onequestionjournal.manager
import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSettings(notificationsEnabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, notificationsEnabled)
        editor.apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }


    companion object {
        private const val PREFS_NAME = "NotificationSettings"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }
}