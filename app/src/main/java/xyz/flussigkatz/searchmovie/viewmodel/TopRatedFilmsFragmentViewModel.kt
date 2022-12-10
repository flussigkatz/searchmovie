package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.entity.TopRatedFilm
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.data.ConstantsApp
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class TopRatedFilmsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val filmListData: Observable<List<TopRatedFilm>>


    init {
        App.instance.dagger.inject(this)
        filmListData = interactor.getTopRatedFilmsFromDB()
    }

    fun getFilms(page: Int) {
        interactor.getFilmsFromApi(ConstantsApp.POPULAR_CATEGORY, page)
    }

    fun removeFavoriteFilmFromList(id: Int){
        interactor.removeFavoriteFilmFromList(id)
    }

    fun addFavoriteFilmToList(id: Int){
        interactor.addFavoriteFilmToList(id)
    }
}