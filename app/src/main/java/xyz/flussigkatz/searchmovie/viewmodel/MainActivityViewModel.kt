package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.searchmovie.di.AppScope
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

@AppScope
class MainActivityViewModel @Inject constructor(private val interactor: Interactor) : ViewModel() {
    val eventMessage = interactor.getEventMessageLiveData()

    fun getSplashScreenStateStatus() = interactor.getSplashScreenStateFromPreferences()

    fun getIdsMarkedFilms() = interactor.getIdsMarkedFilms()

    fun getMarkedFilmById(id: Int) = interactor.getMarkedFilmById(id)

    fun getNightModeStatus() = interactor.getNightModeFromPreferences()

    fun saveDayOfYear(day: Int) {
        interactor.saveDayOfYear(day)
    }

    fun getDayOfYear() = interactor.getDayOfYear()
}