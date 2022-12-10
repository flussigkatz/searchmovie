package xyz.flussigkatz.searchmovie.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val themePropertyLifeData: MutableLiveData<Int> = MutableLiveData()
    val splashScreenPropertyLifeData: MutableLiveData<Boolean> = MutableLiveData()

    init {
        App.instance.dagger.inject(this)
        getNightMode()
        getSplashScreenProperty()
    }

    private fun getNightMode() {
        themePropertyLifeData.value = interactor.getNightModeFromPreferences()
    }

    fun setNightMode(mode: Int, activity: Activity) {
        val mMode = interactor.getNightModeFromPreferences()
        if (mMode != mode) {
            interactor.saveNightModeToPreferences(mode)
            activity.recreate()
            getNightMode()
        }
    }

    private fun getSplashScreenProperty() {
        splashScreenPropertyLifeData.value = interactor.getSplashScreenStateFromPreferences()
    }

    fun putSplashScreenProperty(state: Boolean) {
        interactor.setSplashScreenState(state)
        getSplashScreenProperty()
    }
}