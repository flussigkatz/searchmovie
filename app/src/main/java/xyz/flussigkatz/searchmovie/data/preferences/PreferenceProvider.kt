package xyz.flussigkatz.searchmovie.data.preferences

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
    }

    fun getDefaultCategory(): String {
        return preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    }

    fun saveNightMode(mode: Int) {
        preference.edit() { putInt(KEY_DEFAULT_THEME, mode) }
    }

    fun getNightMode(): Int {
        return preference.getInt(KEY_DEFAULT_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun setPlaySplashScreenState(state: Boolean) {
        preference.edit() { putBoolean(KEY_PLAY_SPLASH_SCREEN, state) }
    }

    fun getPlaySplashScreenState() = preference.getBoolean(KEY_PLAY_SPLASH_SCREEN, true)

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_PLAY_SPLASH_SCREEN = "splash_screen"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val KEY_DEFAULT_THEME = "default_theme"
        private const val DEFAULT_CATEGORY = "popular"
    }
}