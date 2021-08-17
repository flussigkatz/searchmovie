package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import xyz.flussikatz.searchmovie.domain.Film
import xyz.flussikatz.searchmovie.domain.MainInteractor

class MarkedFragmentViewModel : ViewModel(), KoinComponent {
    val filmListLiveData = MutableLiveData<List<Film>>()
    private val mainInteractor: MainInteractor by inject()

    init {
//        val films = interactor.getFilmsDB().filter { it.fav_state }
//        filmListLiveData.postValue(films)
    }
}