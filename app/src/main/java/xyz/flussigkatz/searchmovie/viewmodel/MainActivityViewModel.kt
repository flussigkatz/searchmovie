package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class MainActivityViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val eventMessage: PublishSubject<String>

    init {
        App.instance.dagger.inject(this)
        eventMessage = interactor.getEventMessage()
    }

    fun getSplashScreenStateStatus() = interactor.getSplashScreenStateFromPreferences()

    fun getMarkedFilmsFromDB() = interactor.getMarkedFilmsFromDB()

    fun getNightModeStatus() = interactor.getNightModeFromPreferences()
}