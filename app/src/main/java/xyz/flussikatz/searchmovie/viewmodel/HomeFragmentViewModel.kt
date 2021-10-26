package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.domain.Interactor
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
}