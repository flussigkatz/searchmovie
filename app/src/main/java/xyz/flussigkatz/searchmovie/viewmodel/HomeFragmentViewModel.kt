package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.data.entity.Film
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val refreshState: BehaviorSubject<Boolean>
    val eventMessage: PublishSubject<String>
    val filmListData: Observable<List<Film>>

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmListData = interactor.getFilmsFromDB()
        refreshState = interactor.getRefreshState()
        eventMessage = interactor.getEventMessage()
        getFilms()
    }

    fun getFilms() {
        interactor.getFilmsFromApi(1)
    }
    fun getFilmsFromDB(): Observable<List<Film>> {
        return interactor.getFilmsFromDB()
    }

    fun getSearchedFilms(search_query: String): Observable<List<Film>> {
        return interactor.getSearchedFilmsFromApi(search_query, 1)
    }

}