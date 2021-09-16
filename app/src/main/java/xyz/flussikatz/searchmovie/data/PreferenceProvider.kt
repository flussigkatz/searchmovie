package xyz.flussikatz.searchmovie.data

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
        if(preference.getBoolean(KEY_FIRST_LAUNCH, false)) {
            preference.edit() {putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY)}
            preference.edit() {putBoolean(KEY_FIRST_LAUNCH, false)}
        }
    }

    fun saveDefaultCategory(category: String) {
        preference.edit() {putString(KEY_DEFAULT_CATEGORY, category)}
    }

    fun getDefaultCategory(): String {
        return preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    }

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DEFAULT_CATEGORY = "deafult_category"
        private const val DEFAULT_CATEGORY = "porular"

    }
}