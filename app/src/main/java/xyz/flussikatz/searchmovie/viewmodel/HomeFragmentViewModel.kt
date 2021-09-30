package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.data.ApiCallback
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.domain.Interactor
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val filmListLiveData: LiveData<List<Film>>

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmListLiveData = interactor.getFilmsFromDB()
        getFilms()
    }

    fun getFilms() {
        val realTime = System.currentTimeMillis()
        val lastLoadTime = interactor.getLoadFromApiTimeIntervalToPreferences()
        if (lastLoadTime + TIME_INTERVAL < realTime) {
            interactor.getFilmsFromApi(1, object : ApiCallback {
                override fun onSuccess() {
                    interactor.saveLoadFromApiTimeIntervalToPreferences(System.currentTimeMillis())
                }

                override fun onFailure() {
                }

            })
        }
    }


    companion object {
        private const val TIME_INTERVAL = 600000L
    }
}