package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.domain.Film
import xyz.flussikatz.searchmovie.domain.Interactor

class MarkedFragmentViewModel : ViewModel() {
    val filmListLiveData = MutableLiveData<List<Film>>()
    private  var interactor: Interactor = App.instance.interactor

    init {
//        val films = interactor.getFilmsDB().filter { it.fav_state }
//        filmListLiveData.postValue(films)
    }
}