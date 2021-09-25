package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.domain.Interactor
import java.util.concurrent.Executors
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val filmListLiveData = MutableLiveData<List<Film>>()

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        loadFilmsFromDBInSingleThread()
        getFilms()
    }

    fun getFilms() {
        val realTime = System.currentTimeMillis()
        val lastLoadTime = interactor.getLoadFromApiTimeIntervalToPreferences()
        interactor.getFilmsFromApi(1, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                if (realTime - lastLoadTime >= TIME_INTERVAL) {
                    filmListLiveData.postValue(films)
                    println("!!! Load from api ${realTime - lastLoadTime}")
                    interactor.saveLoadFromApiTimeIntervalToPreferences(System.currentTimeMillis())
                } else {
                    println("!!! Load from DB ${realTime - lastLoadTime}")
                    loadFilmsFromDBInSingleThread()
                }
            }

            override fun onFailure() {
                loadFilmsFromDBInSingleThread()
            }

        })
    }

    fun loadFilmsFromDBInSingleThread() {
        Executors.newSingleThreadExecutor().execute {
            filmListLiveData.postValue(interactor.getFilmsFromDB())
        }
    }

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }

    companion object {
        private const val TIME_INTERVAL = 600000L
    }
}