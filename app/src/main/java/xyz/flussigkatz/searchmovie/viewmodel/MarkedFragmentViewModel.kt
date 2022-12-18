package xyz.flussigkatz.searchmovie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.data.ConstantsApp.EMPTY_QUERY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.MARKED_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.SEARCH_DEBOUNCE_TIME_MILLISECONDS
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class MarkedFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    private val searchQueryLiveData = MutableLiveData(EMPTY_QUERY)

    init {
        App.instance.dagger.inject(this)
        getMarkedFilmsFromApi()
    }

    @OptIn(FlowPreview::class)
    val filmFlow = searchQueryLiveData.asFlow()
        .distinctUntilChanged()
        .debounce(SEARCH_DEBOUNCE_TIME_MILLISECONDS)
        .flatMapLatest { interactor.getFilms(MARKED_CATEGORY, it.lowercase().trim()) }
        .cachedIn(viewModelScope)

    suspend fun changeFavoriteMark(id: Int, flag: Boolean) = interactor.changeFavoriteMark(id, flag)

    fun getMarkedFilmsFromApi() {
        viewModelScope.launch {
            try {
                interactor.getMarkedFilmsFromApi()
            } catch (e: Exception) {
                Timber.d(e)
            }
        }
    }

    fun setSearchQuery(query: String) {
        searchQueryLiveData.postValue(query)
    }
}