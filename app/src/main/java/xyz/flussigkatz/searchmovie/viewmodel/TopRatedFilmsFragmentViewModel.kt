package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.data.ConstantsApp.TOP_RATED_CATEGORY
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

class TopRatedFilmsFragmentViewModel : ViewModel() {
    @Inject lateinit var interactor: Interactor
    val filmFlow: Flow<PagingData<FilmUiModel>>

    init {
        App.instance.dagger.inject(this)
        filmFlow = interactor.getFilms(TOP_RATED_CATEGORY)
            .catch { Timber.d(it) }
            .cachedIn(viewModelScope)
    }

    suspend fun changeFavoriteMark(id: Int, flag: Boolean) = interactor.changeFavoriteMark(id, flag)
}