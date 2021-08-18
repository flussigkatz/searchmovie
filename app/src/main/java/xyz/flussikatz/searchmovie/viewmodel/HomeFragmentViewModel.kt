package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.flussikatz.searchmovie.domain.Film
import xyz.flussikatz.searchmovie.domain.MainInteractor
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel(){
    val filmListLiveData = MutableLiveData<List<Film>>()
    @Inject lateinit var mainInteractor: MainInteractor

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