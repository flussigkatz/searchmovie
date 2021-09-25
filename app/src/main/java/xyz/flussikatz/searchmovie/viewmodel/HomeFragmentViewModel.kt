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
        Executors.newSingleThreadExecutor().execute{
            filmListLiveData.postValue(interactor.getFilmsFromDb())
        }
        getFilms()
    }

    fun getFilms() {
        interactor.getFilmsFromApi(1, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                filmListLiveData.postValue(films)
            }

            override fun onFailure() {
                Executors.newSingleThreadExecutor().execute{
                    filmListLiveData.postValue(interactor.getFilmsFromDb())
                }
            }

        })
    }

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}