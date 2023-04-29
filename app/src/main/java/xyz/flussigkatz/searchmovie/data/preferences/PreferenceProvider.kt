package xyz.flussigkatz.searchmovie.data.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode
import androidx.core.content.edit
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DEFAULT_LIST_ID
import xyz.flussigkatz.searchmovie.di.AppScope
import javax.inject.Inject

@AppScope
class PreferenceProvider @Inject constructor(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_SETTINGS,
        MODE_PRIVATE
    )

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

    fun savePlaySplashScreenState(state: Boolean) {
        preferences.edit() { putBoolean(KEY_PLAY_SPLASH_SCREEN, state) }
    }

    fun getPlaySplashScreenState() = preferences.getBoolean(KEY_PLAY_SPLASH_SCREEN, true)

    fun getMarkedFilmListId() = preferences.getInt(KEY_MARKED_FILM_LIST_ID, DEFAULT_LIST_ID)

    fun saveFavoriteFilmListId(id: Int) {
        preferences.edit() { putInt(KEY_MARKED_FILM_LIST_ID, id) }
    }

    fun getDayOfYear() = preferences.getInt(KEY_DAY_OF_YEAR, DEFAULT_DAY_OF_YEAR)

    fun saveDayOfYear(day: Int) {
        preferences.edit() { putInt(KEY_DAY_OF_YEAR, day) }
    }

    companion object {
        private const val KEY_FIRST_LAUNCH = "key_first_launch"
        private const val KEY_DAY_OF_YEAR = "key_day_of_year"
        private const val DEFAULT_DAY_OF_YEAR = 0
        private const val KEY_PLAY_SPLASH_SCREEN = "key_splash_screen"
        private const val KEY_MARKED_FILM_LIST_ID = "key_marked_film_list_id"
        private const val KEY_DEFAULT_THEME = "key_default_theme"
        private const val PREFERENCES_SETTINGS = "settings"
    }
}