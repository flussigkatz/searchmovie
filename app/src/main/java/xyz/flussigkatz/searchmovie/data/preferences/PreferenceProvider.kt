package xyz.flussigkatz.searchmovie.data.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode
import androidx.core.content.edit
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DEFAULT_LIST_ID

class PreferenceProvider(appContext: Context) {
    private val preferences = appContext.getSharedPreferences(PREFERENCES_SETTINGS, MODE_PRIVATE)

    init {
        if (preferences.getBoolean(KEY_FIRST_LAUNCH, false)) {
            saveNightMode(getDefaultNightMode())
            preferences.edit() { putBoolean(KEY_FIRST_LAUNCH, false) }
        }
    }

    fun saveNightMode(mode: Int) {
        preferences.edit() { putInt(KEY_DEFAULT_THEME, mode) }
    }

    fun getNightMode() = preferences.getInt(KEY_DEFAULT_THEME, MODE_NIGHT_FOLLOW_SYSTEM)

    fun setPlaySplashScreenState(state: Boolean) {
        preferences.edit() { putBoolean(KEY_PLAY_SPLASH_SCREEN, state) }
    }

    fun getPlaySplashScreenState() = preferences.getBoolean(KEY_PLAY_SPLASH_SCREEN, true)

    fun getMarkedFilmListId() = preferences.getInt(KEY_MARKED_FILM_LIST_ID, DEFAULT_LIST_ID)

    fun setFavoriteFilmListId(id: Int) {
        preferences.edit() { putInt(KEY_MARKED_FILM_LIST_ID, id) }
    }

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_PLAY_SPLASH_SCREEN = "splash_screen"
        private const val KEY_MARKED_FILM_LIST_ID = "marked_film_list_id"
        private const val KEY_DEFAULT_THEME = "default_theme"
        private const val PREFERENCES_SETTINGS = "settings"
    }
}