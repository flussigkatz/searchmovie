package xyz.flussikatz.searchmovie.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext
    private val preference: SharedPreferences = appContext.getSharedPreferences(
        "settings",
        Context.MODE_PRIVATE
    )

    init {
        if (preference.getBoolean(KEY_FIRST_LAUNCH, false)) {
            preference.edit() { putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) }
            preference.edit() { putInt(KEY_DEFAULT_THEME, AppCompatDelegate.getDefaultNightMode()) }
            preference.edit() { putBoolean(KEY_FIRST_LAUNCH, false) }
        }
    }

    fun saveDefaultCategory(category: String) {
        preference.edit() { putString(KEY_DEFAULT_CATEGORY, category) }
        preference.edit() {
            putLong(KEY_LOAD_FROM_API_TIME_INTERVAL, DEFAULT_LOAD_FROM_API_TIME_INTERVAL)
        }
    }

    fun getDefaultCategory(): String {
        return preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    }

    fun saveDefaultTheme(theme: Int) {
        preference.edit() { putInt(KEY_DEFAULT_THEME, theme) }
    }

    fun getDefaultTheme(): Int {
        return preference.getInt(KEY_DEFAULT_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun saveLoadFromApiTimeInterval(time: Long) {
        preference.edit() { putLong(KEY_LOAD_FROM_API_TIME_INTERVAL, time) }
    }

    fun getLoadFromApiTimeInterval(): Long {
        return preference.getLong(
            KEY_LOAD_FROM_API_TIME_INTERVAL,
            DEFAULT_LOAD_FROM_API_TIME_INTERVAL
        )
    }

    fun setPlaySplashScreenState(state: Boolean) {
        preference.edit() { putBoolean(KEY_PLAY_SPLASH_SCREEN, state) }
    }

    fun getPlaySplashScreenState() = preference.getBoolean(KEY_PLAY_SPLASH_SCREEN, true)

    fun setBoringKillerNotificationTimeInterval() {
        preference.edit() {
            putLong(
                KEY_BORING_KILLER_NOTIFICATION_TIME_INTERVAL,
                System.currentTimeMillis() + DEFAULT_BORING_KILLER_NOTIFICATION_TIME_INTERVAL
            )
        }
    }

    fun getBoringKillerNotificationTimeInterval(): Long {
        return preference.getLong(
            KEY_BORING_KILLER_NOTIFICATION_TIME_INTERVAL,
            System.currentTimeMillis()
        )
    }

    fun setBoringKillerNotificationState(state: Boolean) {
        preference.edit() { putBoolean(KEY_BORING_KILLER_NOTIFICATION_STATE, state) }
    }
    fun getBoringKillerNotificationState(): Boolean {
        return preference.getBoolean(KEY_BORING_KILLER_NOTIFICATION_STATE, true)
    }

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_PLAY_SPLASH_SCREEN = "splash_screen"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val KEY_DEFAULT_THEME = "default_theme"
        private const val DEFAULT_CATEGORY = "popular"
        private const val KEY_LOAD_FROM_API_TIME_INTERVAL = "load_from_api_time_interval"
        private const val DEFAULT_LOAD_FROM_API_TIME_INTERVAL = 0L
        private const val KEY_BORING_KILLER_NOTIFICATION_STATE = "boring_killer_notification_state"
        private const val KEY_BORING_KILLER_NOTIFICATION_TIME_INTERVAL =
            "boring_killer_time_interval"
        private const val DEFAULT_BORING_KILLER_NOTIFICATION_TIME_INTERVAL = 86400000L


    }
}