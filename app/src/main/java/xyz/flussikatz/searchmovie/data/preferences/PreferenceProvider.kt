package xyz.flussikatz.searchmovie.data.preferences

import android.content.Context
import android.content.SharedPreferences
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
        preference.edit() { putBoolean(KEY_PLAY_SPLASH_SCREEN, state)}
    }

    fun getPlaySplashScreenState() = preference.getBoolean(KEY_PLAY_SPLASH_SCREEN, true)

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_PLAY_SPLASH_SCREEN = "splash_screen"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "popular"
        private const val KEY_LOAD_FROM_API_TIME_INTERVAL = "load_from_api_time_interval"
        private const val DEFAULT_LOAD_FROM_API_TIME_INTERVAL = 0L


    }
}