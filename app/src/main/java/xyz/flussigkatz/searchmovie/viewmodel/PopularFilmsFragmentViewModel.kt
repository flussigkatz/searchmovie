package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.entity.PopularFilm
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.data.ConstantsApp.POPULAR_CATEGORY
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class PopularFilmsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val filmListData: Observable<List<PopularFilm>>


    init {
        App.instance.dagger.inject(this)
        filmListData = interactor.getPopularFilmsFromDB()
    }

    fun getFilms(page: Int) {
        interactor.getFilmsFromApi(POPULAR_CATEGORY, page)
    }

    fun removeFavoriteFilmFromList(id: Int){
        interactor.removeFavoriteFilmFromList(id)
    }

    fun addFavoriteFilmToList(id: Int){
        interactor.addFavoriteFilmToList(id)
    }
}