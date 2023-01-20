package xyz.flussigkatz.searchmovie.domain

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
class Interactor(
    private val repository: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider,
) {
    private val language = Locale.getDefault().run { "$language-$country" }
    private var favoriteListId = preferences.getMarkedFilmListId()
    private val mutableLiveEventMessage = MutableLiveData<Int>()
    private val eventMessage: LiveData<Int>
        get() = mutableLiveEventMessage

    init {
        GlobalScope.launch {
            getMarkedFilmsFromApi()
        }
    }

    //region Api
    suspend fun getMarkedFilmsFromApi() {
        checkFavoriteListIdStatus()
        flow {
            emit(
                retrofitService.getMarkedFilms(
                    list_id = favoriteListId,
                    api_key = API_KEY,
                    language = language,
                )
            )
        }.map { favoriteMovieListDto ->
            favoriteMovieListDto.favoriteListItems.map { MarkedFilm(it, true) }
        }.catch { Timber.d(it) }.flowOn(Dispatchers.IO).collectLatest {
            repository.insertMarkedFilms(it)
        }
    }

    suspend fun getFilmMarkStatus(id: Int) = flow {
        emit(
            retrofitService.getFilmMarkStatus(
                list_id = favoriteListId,
                api_key = API_KEY,
                movie_id = id
            ).itemPresent
        )
    }.catch { Timber.d(it) }.flowOn(Dispatchers.IO).singleOrNull() ?: false

    private suspend fun getFilmListsFromApi() = flow {
        emit(
            retrofitService.getFilmLists(
                account_id = ACCOUNT_ID,
                api_key = API_KEY,
                session_id = SESSION_ID,
                language = language
            ).results.find { it.name == FAVORITE_FILM_LIST_NAME }?.id
        )
    }.catch { Timber.d(it) }.flowOn(Dispatchers.IO).singleOrNull()

    suspend fun changeFavoriteMark(id: Int, flag: Boolean) = flow {
        emit(
            if (flag) retrofitService.addFavoriteFilmToList(
                list_id = favoriteListId,
                api_key = API_KEY,
                session_id = SESSION_ID,
                favoriteMovieInfo = FavoriteMovieInfoDto(mediaId = id)
            ).success
            else retrofitService.removeFavoriteFilmFromList(
                list_id = favoriteListId,
                api_key = API_KEY,
                session_id = SESSION_ID,
                favoriteMovieInfo = FavoriteMovieInfoDto(mediaId = id)
            ).success
        )
    }.catch {
        Timber.d(it)
        emit(!flag)
    }.onEach { if (it) getMarkedFilmsFromApi() }.flowOn(Dispatchers.IO).singleOrNull() ?: !flag

    private suspend fun createFavoriteFilmList() = flow {
        emit(
            retrofitService.createFavoriteFilmList(
                api_key = API_KEY,
                session_id = SESSION_ID,
                listInfo = ListInfo(
                    description = FAV_LIST_DESCRIPTION,
                    language = language,
                    name = FAVORITE_FILM_LIST_NAME
                )
            ).listId
        )
    }.catch { Timber.d(it) }.flowOn(Dispatchers.IO).singleOrNull()
//endregion

    //region DB
    suspend fun insertBrowsingFilm(film: BrowsingFilm) {
        withContext(Dispatchers.IO) {
            repository.insertBrowsingFilm(film)
        }
    }

    fun getFilms(category: String, query: String? = null) = repository.getFilms(category, query)

    fun getIdsMarkedFilms() = repository.getIdsMarkedFilms()

    fun getMarkedFilmById(id: Int) = repository.getMarkedFilmById(id)
//endregion

    //region Preferences
    fun saveNightModeToPreferences(mode: Int) {
        preferences.saveNightMode(mode)
    }

    fun getNightModeFromPreferences() = preferences.getNightMode()

    fun saveSplashScreenState(state: Boolean) {
        preferences.savePlaySplashScreenState(state)
    }

    fun getSplashScreenStateFromPreferences() = preferences.getPlaySplashScreenState()

    fun getDayOfYear() = preferences.getDayOfYear()

    fun saveDayOfYear(day: Int) {
        preferences.saveDayOfYear(day)
    }

//endregion

    fun getEventMessageLiveData() = eventMessage

    fun postMessage(@StringRes message: Int) {
        mutableLiveEventMessage.postValue(message)
    }

    private suspend fun checkFavoriteListIdStatus() {
        if (favoriteListId == DEFAULT_LIST_ID) {
            (getFilmListsFromApi() ?: createFavoriteFilmList())?.let {
                favoriteListId = it
                preferences.saveFavoriteFilmListId(it)
            }
        }
    }


    companion object {
        private const val FAV_LIST_DESCRIPTION = "Favorite film list"
    }
}