package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.domain.Film
import xyz.flussikatz.searchmovie.domain.FilmDiff
import xyz.flussikatz.searchmovie.domain.Interactor

class HomeFragmentViewModel : ViewModel() {
    val filmListLiveData = MutableLiveData<List<Film>>()
    private var interactor: Interactor = App.instance.interactor
    init {
        val films = interactor.getFilmsDB()
        filmListLiveData.postValue(films)
    }
}