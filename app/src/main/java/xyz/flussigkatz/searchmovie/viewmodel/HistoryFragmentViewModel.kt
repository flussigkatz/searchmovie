package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import timber.log.Timber
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.data.ConstantsApp.BROWSING_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.EMPTY_QUERY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SEARCH_DEBOUNCE_TIME_MILLISECONDS
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class HistoryFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    private val searchQueryLiveData = MutableLiveData(EMPTY_QUERY)

    init {
        App.instance.dagger.inject(this)
    }

    @OptIn(FlowPreview::class)
    val filmFlow = searchQueryLiveData.asFlow()
        .distinctUntilChanged()
        .debounce(SEARCH_DEBOUNCE_TIME_MILLISECONDS)
        .flatMapLatest { interactor.getFilms(BROWSING_CATEGORY, it.lowercase().trim()) }
        .catch { Timber.d(it) }
        .cachedIn(viewModelScope)

    suspend fun changeFavoriteMark(id: Int, flag: Boolean) = interactor.changeFavoriteMark(id, flag)

    fun setSearchQuery(query: String) {
        searchQueryLiveData.postValue(query)
    }
}