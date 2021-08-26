package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import xyz.flussikatz.searchmovie.domain.Film
import xyz.flussikatz.searchmovie.domain.MainInteractor

class HomeFragmentViewModel : ViewModel(), KoinComponent {
    val filmListLiveData = MutableLiveData<List<Film>>()
    private val mainInteractor: MainInteractor by inject()

    init {
        mainInteractor.getFilmsFromApi(1, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                filmListLiveData.postValue(films)
            }

            override fun onFailure() {
            }

        })
    }

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}