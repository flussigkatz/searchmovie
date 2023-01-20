package xyz.flussigkatz.searchmovie.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel() {
    @Inject lateinit var interactor: Interactor
    private val mutableThemePropertyLifeData = MutableLiveData<Int>()
    val themePropertyLifeData: LiveData<Int>
        get() = mutableThemePropertyLifeData
    private val mutableSplashScreenPropertyLifeData = MutableLiveData<Boolean>()
    val splashScreenPropertyLifeData: LiveData<Boolean>
        get() = mutableSplashScreenPropertyLifeData

    init {
        App.instance.dagger.inject(this)
        getSplashScreenState()
        getNightMode()
    }

    private fun getNightMode() {
        mutableThemePropertyLifeData.postValue(interactor.getNightModeFromPreferences())
    }

    fun setNightMode(mode: Int, activity: Activity) {
        if (interactor.getNightModeFromPreferences() != mode) {
            interactor.saveNightModeToPreferences(mode)
            getNightMode()
            activity.recreate()
        }
    }

    private fun getSplashScreenState() {
        mutableSplashScreenPropertyLifeData.postValue(interactor.getSplashScreenStateFromPreferences())
    }

    fun putSplashScreenProperty(state: Boolean) {
        interactor.saveSplashScreenState(state)
        getSplashScreenState()
    }
}