package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import xyz.flussigkatz.core_api.entity.Film
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.data.ConstantsApp.FIRST_PAGE
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val refreshState: BehaviorSubject<Boolean>
    val filmListData: Observable<List<Film>>
    var nextPage = FIRST_PAGE


    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmListData = interactor.getSearchedFilmsFromDB()
        refreshState = interactor.getRefreshState()
    }

    fun getFilms(category: String) {
        interactor.getFilmsFromApi(category, FIRST_PAGE)
        nextPage++
    }

    fun removeFavoriteFilmFromList(id: Int){
        interactor.removeFavoriteFilmFromList(id)
    }

    fun addFavoriteFilmToList(id: Int){
        interactor.addFavoriteFilmToList(id)
    }

    fun getSearchedFilms(search_query: String, page: Int? = null) {
        page?.let { nextPage = it }
        interactor.getSearchedFilmsFromApi(search_query, nextPage)
        nextPage++
    }

    fun clearSearchedFilmDB() {
        interactor.clearSearchedFilmDB()
    }
}