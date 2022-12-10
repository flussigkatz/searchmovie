package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import xyz.flussigkatz.core_api.entity.Film
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val refreshState: BehaviorSubject<Boolean>
    val filmListData: Observable<List<Film>>

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmListData = interactor.getSearchedFilmsFromDB()
        refreshState = interactor.getRefreshState()
    }

    fun getFilms(category: String, page: Int) {
        interactor.getFilmsFromApi(category, page)
    }

    fun removeFavoriteFilmFromList(id: Int){
        interactor.removeFavoriteFilmFromList(id)
    }

    fun addFavoriteFilmToList(id: Int){
        interactor.addFavoriteFilmToList(id)
    }

    fun getSearchedFilms(search_query: String) {
        interactor.getSearchedFilmsFromApi(search_query, 1)
    }

    fun clearSearchedFilmDB() {
        interactor.clearSearchedFilmDB()
    }
}