package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class MainActivityViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun getSplashScreenStateStatus() = interactor.getSplashScreenStateFromPreferences()

    fun getMarkedFilmsFromDB() = interactor.getMarkedFilmsFromDB()

    fun getNightModeStatus() = interactor.getNightModeFromPreferences()
}