package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import xyz.flussigkatz.searchmovie.data.ConstantsApp.TOP_RATED_CATEGORY
import xyz.flussigkatz.searchmovie.di.AppScope
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

@AppScope
class TopRatedFilmsFragmentViewModel @Inject constructor(private val interactor: Interactor) : ViewModel() {
    val filmFlow = interactor.getFilms(TOP_RATED_CATEGORY).catch { Timber.d(it) }
        .cachedIn(viewModelScope)

    suspend fun changeFavoriteMark(id: Int, flag: Boolean) = interactor.changeFavoriteMark(id, flag)
}