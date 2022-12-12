package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.entity.UpcomingFilm
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.data.ConstantsApp.FIRST_PAGE
import xyz.flussigkatz.searchmovie.data.ConstantsApp.UPCOMING_CATEGORY
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class UpcomingFilmsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val filmListData: Observable<List<UpcomingFilm>>
    var nextPage = FIRST_PAGE



    init {
        App.instance.dagger.inject(this)
        filmListData = interactor.getUpcomingFilmsFromDB()
    }

    fun getFilms(page: Int? = null) {
        page?.let { nextPage = it }
        interactor.getFilmsFromApi(UPCOMING_CATEGORY, nextPage)
        nextPage++
    }

    fun removeFavoriteFilmFromList(id: Int){
        interactor.removeFavoriteFilmFromList(id)
    }

    fun addFavoriteFilmToList(id: Int){
        interactor.addFavoriteFilmToList(id)
    }
}