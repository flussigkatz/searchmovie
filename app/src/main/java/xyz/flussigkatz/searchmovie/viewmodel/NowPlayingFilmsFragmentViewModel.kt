package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.entity.NowPlayingFilm
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NOW_PLAYING_CATEGORY
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class NowPlayingFilmsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val filmListData: Observable<List<NowPlayingFilm>>


    init {
        App.instance.dagger.inject(this)
        filmListData = interactor.getNowPlayingFilmsFromDB()
    }

    fun getFilms(page: Int) {
        interactor.getFilmsFromApi(NOW_PLAYING_CATEGORY, page)
    }

    fun removeFavoriteFilmFromList(id: Int){
        interactor.removeFavoriteFilmFromList(id)
    }

    fun addFavoriteFilmToList(id: Int){
        interactor.addFavoriteFilmToList(id)
    }
}