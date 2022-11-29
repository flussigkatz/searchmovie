package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.entity.BrowsingFilm
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class HistoryFragmentViewModel : ViewModel() {

    @Inject
    lateinit var interactor: Interactor
    val browsingFilmListData: Observable<List<BrowsingFilm>>


    init {
        App.instance.dagger.inject(this)
        browsingFilmListData = interactor.getCashedBrowsingFilmsFromDB()
    }

    fun removeFavoriteFilmFromList(id: Int){
        interactor.removeFavoriteFilmFromList(id)
    }

    fun addFavoriteFilmToList(id: Int){
        interactor.addFavoriteFilmToList(id)
    }
}