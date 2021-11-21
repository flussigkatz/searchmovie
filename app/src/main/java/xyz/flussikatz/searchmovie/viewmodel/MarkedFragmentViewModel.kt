package xyz.flussikatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.domain.Interactor
import javax.inject.Inject

class MarkedFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val favoriteFilmListData: Observable<List<Film>>


    init {
        App.instance.dagger.inject(this)
        favoriteFilmListData = interactor.getFavoriteFilmsFromApi(1)
    }
}