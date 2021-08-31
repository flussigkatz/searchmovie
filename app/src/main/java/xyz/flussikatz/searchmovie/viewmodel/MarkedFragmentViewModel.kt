package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.flussikatz.searchmovie.domain.Film

class MarkedFragmentViewModel : ViewModel() {
    val filmListLiveData = MutableLiveData<List<Film>>()

    init {
//        val films = interactor.getFilmsDB().filter { it.fav_state }
//        filmListLiveData.postValue(films)
    }
}