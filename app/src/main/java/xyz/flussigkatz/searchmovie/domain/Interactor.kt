package xyz.flussigkatz.searchmovie.domain

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.*
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.*
import xyz.flussigkatz.remote_module.TmdbApi
import xyz.flussigkatz.remote_module.entity.FavoriteMovieInfoDto
import xyz.flussigkatz.remote_module.entity.ListInfo
import xyz.flussigkatz.searchmovie.data.Api.ACCOUNT_ID
import xyz.flussigkatz.searchmovie.data.Api.API_KEY
import xyz.flussigkatz.searchmovie.data.Api.SESSION_ID
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DEFAULT_LIST_ID
import xyz.flussigkatz.searchmovie.data.ConstantsApp.FAVORITE_FILM_LIST_NAME
import xyz.flussigkatz.searchmovie.data.MainRepository
import xyz.flussigkatz.searchmovie.data.preferences.PreferenceProvider
import java.util.*

@OptIn(DelicateCoroutinesApi::class)
@ExperimentalPagingApi
class Interactor(
    private val repository: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider,
) {
    private val language = Locale.getDefault().run { "$language-$country" }
    private val mutableLiveEventMessage = MutableLiveData<Int>()
    private val eventMessage: LiveData<Int>
        get() = mutableLiveEventMessage

    init {
        GlobalScope.launch {
            try {
                checkFavoriteListIdStatus()
                getMarkedFilmsFromApi()
            } catch (e: Exception) {
                Timber.d(e)
            }
        }
    }

    //region Api
    suspend fun getMarkedFilmsFromApi() {
        withContext(Dispatchers.IO) {
            retrofitService.getMarkedFilms(
                list_id = preferences.getMarkedFilmListId(),
                api_key = API_KEY,
                language = language,
            ).favoriteListItems.map { MarkedFilm(it, true) }.let {
                repository.insertMarkedFilms(it)
            }
        }
    }

    suspend fun getFilmMarkStatus(id: Int) = withContext(Dispatchers.IO) {
        retrofitService.getFilmMarkStatus(
            list_id = getFavoriteListIdFromPreferences(),
            api_key = API_KEY,
            movie_id = id
        )
    }

    private suspend fun getFilmListsFromApi() = retrofitService.getFilmLists(
        account_id = ACCOUNT_ID,
        api_key = API_KEY,
        session_id = SESSION_ID,
        language = language
    )

    suspend fun changeFavoriteMark(id: Int, flag: Boolean) = withContext(Dispatchers.IO) {
        try {
            val res = if (flag) retrofitService.addFavoriteFilmToList(
                list_id = getFavoriteListIdFromPreferences(),
                api_key = API_KEY,
                session_id = SESSION_ID,
                favoriteMovieInfo = FavoriteMovieInfoDto(mediaId = id)
            ).success
            else retrofitService.removeFavoriteFilmFromList(
                list_id = getFavoriteListIdFromPreferences(),
                api_key = API_KEY,
                session_id = SESSION_ID,
                favoriteMovieInfo = FavoriteMovieInfoDto(mediaId = id)
            ).success
            if (res) getMarkedFilmsFromApi()
            res
        } catch (e: Exception) {
            Timber.d(e)
            false
        }
    }

    private suspend fun createFavoriteFilmList() {
        withContext(Dispatchers.IO) {
            retrofitService.createFavoriteFilmList(
                api_key = API_KEY,
                session_id = SESSION_ID,
                listInfo = ListInfo(
                    description = FAV_LIST_DESCRIPTION,
                    language = language,
                    name = FAVORITE_FILM_LIST_NAME
                )
            ).let {
                if (it.success) preferences.setFavoriteFilmListId(it.listId)
            }
        }
    }
    //endregion

    //region DB
    suspend fun insertBrowsingFilm(film: BrowsingFilm) {
        withContext(Dispatchers.IO) {
            repository.insertBrowsingFilm(film)
        }
    }

    fun getFilms(category: String, query: String? = null) = repository.getFilms(category, query)
    //endregion

    //region Preferences
    fun saveNightModeToPreferences(mode: Int) {
        preferences.saveNightMode(mode)
    }

    fun getNightModeFromPreferences() = preferences.getNightMode()

    fun setSplashScreenState(state: Boolean) {
        preferences.setPlaySplashScreenState(state)
    }

    fun getSplashScreenStateFromPreferences() = preferences.getPlaySplashScreenState()

    private fun getFavoriteListIdFromPreferences() = preferences.getMarkedFilmListId()
    //endregion

    fun getEventMessageLiveData() = eventMessage

    fun postMessage(@StringRes message: Int) {
        mutableLiveEventMessage.postValue(message)
    }

    private suspend fun checkFavoriteListIdStatus() {
        withContext(Dispatchers.IO) {
            if (preferences.getMarkedFilmListId() == DEFAULT_LIST_ID) {
                getFilmListsFromApi().results.find { it.name == FAVORITE_FILM_LIST_NAME }?.let {
                    preferences.setFavoriteFilmListId(it.id)
                } ?: createFavoriteFilmList()
            }
        }
    }

    companion object {
        private const val FAV_LIST_DESCRIPTION = "Favorite film list"
    }
}