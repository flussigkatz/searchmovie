package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.data.ConstantsApp.POPULAR_CATEGORY
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

@ExperimentalPagingApi
class PopularFilmsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val filmFlow: Flow<PagingData<FilmUiModel>>

    init {
        App.instance.dagger.inject(this)
        filmFlow = interactor.getFilms(POPULAR_CATEGORY).cachedIn(viewModelScope)
    }

    suspend fun changeFavoriteMark(id: Int, flag: Boolean) = interactor.changeFavoriteMark(id, flag)
}