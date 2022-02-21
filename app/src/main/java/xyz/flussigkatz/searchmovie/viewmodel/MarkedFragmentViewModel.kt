package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import xyz.flussigkatz.core_api.entity.MarkedFilm
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class MarkedFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val markedFilmListData: Observable<List<MarkedFilm>>
    val refreshState: BehaviorSubject<Boolean>


    init {
        App.instance.dagger.inject(this)
        markedFilmListData = getMarkedFilmsFromDB()
        refreshState = interactor.getRefreshState()
        getMarkedFilms()
    }

    fun getMarkedFilms() {
        interactor.getMarkedFilmsFromApi(1)
    }

    fun deleteMarkedFilmFromDB(id: Int) {
        interactor.deleteMarkedFilmFromDB(id)
    }

    fun getMarkedFilmsFromDB(): Observable<List<MarkedFilm>> {
        return interactor.getMarkedFilmsFromDB()
    }

    fun getSearchedMarkedFilms(query: String): Observable<List<MarkedFilm>> {
        return interactor.getSearchedMarkedFilms(query)
    }
}