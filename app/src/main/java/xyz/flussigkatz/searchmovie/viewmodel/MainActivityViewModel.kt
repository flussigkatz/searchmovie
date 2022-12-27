package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

@ExperimentalPagingApi
class MainActivityViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val eventMessage: LiveData<Int>

    init {
        App.instance.dagger.inject(this)
        eventMessage = interactor.getEventMessageLiveData()
    }

    fun getSplashScreenStateStatus() = interactor.getSplashScreenStateFromPreferences()

    fun getNightModeStatus() = interactor.getNightModeFromPreferences()
}