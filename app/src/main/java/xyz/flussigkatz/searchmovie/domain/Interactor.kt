package xyz.flussigkatz.searchmovie.domain

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.Film
import xyz.flussigkatz.core_api.entity.BrowsingFilm
import xyz.flussigkatz.core_api.entity.MarkedFilm
import xyz.flussigkatz.remote_module.TmdbApi
import xyz.flussigkatz.remote_module.entity.FavoriteMovieInfoDto
import xyz.flussigkatz.remote_module.entity.ListInfo
import xyz.flussigkatz.remote_module.entity.TmdbResultDto.TmdbFilm
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.Api.ACCOUNT_ID
import xyz.flussigkatz.searchmovie.data.Api.API_KEY
import xyz.flussigkatz.searchmovie.data.Api.SESSION_ID
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DEFAULT_LIST_ID
import xyz.flussigkatz.searchmovie.data.ConstantsApp.FAVORITE_FILM_LIST_NAME
import xyz.flussigkatz.searchmovie.data.MainRepository
import xyz.flussigkatz.searchmovie.data.preferences.PreferenceProvider
import java.util.*

class Interactor(
    private val repository: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider,
    private val refreshState: BehaviorSubject<Boolean>,
    private val eventMessage: PublishSubject<String>,
) {
    private val language = Locale.getDefault().run {
        "$language-$country"
    }

    //region Api
    fun getFilmsFromApi(page: Int) {
        retrofitService.getFilms(
            getDefaultCategoryFromPreferences(),
            API_KEY,
            language,
            page
        )
            .filter { it.tmdbFilms.isNotEmpty() }
            .map { convertToFilmFromApi(it.tmdbFilms) }
            .doOnSubscribe { refreshState.onNext(true) }
            .doOnComplete { refreshState.onNext(false) }
            .doOnError {
                refreshState.onNext(false)
                eventMessage.onNext(getText(R.string.error_upload_message))
            }.subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = {
                    repository.clearCashedFilmsDB()
                    repository.putFilmsToDB(it)
                }
            )
    }

    fun getSearchedFilmsFromApi(search_query: String, page: Int): Observable<List<Film>> {
        return retrofitService.getSearchedFilms(
            api_key = API_KEY,
            language = language,
            query = search_query,
            page = page
        ).map { convertToFilmFromApi(it.tmdbFilms) }
            .doOnError { eventMessage.onNext(getText(R.string.error_upload_message)) }
    }

    fun getMarkedFilmsFromApi() {
        retrofitService.getMarkedFilms(
            list_id = getFavoriteListIdFromPreferences(),
            api_key = API_KEY,
            language = language,
        ).map { favoriteMovieListDto ->
            favoriteMovieListDto.favoriteListItems.map {
                MarkedFilm(
                    id = it.id,
                    title = it.title ?: it.name,
                    posterId = it.posterPath ?: "",
                    description = it.overview,
                    rating = (it.voteAverage * 10).toInt(),
                    fav_state = true
                )
            }
        }.doOnSubscribe { refreshState.onNext(true) }
            .doOnComplete { refreshState.onNext(false) }
            .doOnError {
                refreshState.onNext(false)
                eventMessage.onNext(getText(R.string.error_upload_message))
            }.subscribeOn(Schedulers.io()).subscribeBy(
                onError = { Timber.d(it) },
                onNext = {
                    repository.clearMarkedFilmsDB()
                    repository.putMarkedFilmToDB(it)
                }
            )
    }

    fun getFilmMarkStatusFromApi(id: Int) = retrofitService.getFilmMarkStatus(
        list_id = getFavoriteListIdFromPreferences(),
        api_key = API_KEY,
        movie_id = id
    )

    private fun getFilmListsFromApi() = retrofitService.getFilmLists(
        account_id = ACCOUNT_ID,
        api_key = API_KEY,
        session_id = SESSION_ID,
        language = language
    )

    fun addFavoriteFilmToList(id: Int) {
        retrofitService.addFavoriteFilmToList(
            list_id = getFavoriteListIdFromPreferences(),
            api_key = API_KEY,
            session_id = SESSION_ID,
            favoriteMovieInfo = FavoriteMovieInfoDto(mediaId = id)
        ).enqueue(object : Callback<FavoriteMovieInfoDto> {
            override fun onFailure(call: Call<FavoriteMovieInfoDto>, t: Throwable) {
                eventMessage.onNext(getText(R.string.error_add_to_favorite_message))
                Timber.d(t)
            }

            override fun onResponse(
                call: Call<FavoriteMovieInfoDto>,
                response: Response<FavoriteMovieInfoDto>,
            ) {
            }
        })
    }

    fun removeFavoriteFilmFromList(id: Int) {
        retrofitService.removeFavoriteFilmFromList(
            list_id = getFavoriteListIdFromPreferences(),
            api_key = API_KEY,
            session_id = SESSION_ID,
            favoriteMovieInfo = FavoriteMovieInfoDto(mediaId = id)
        ).enqueue(object : Callback<FavoriteMovieInfoDto> {
            override fun onFailure(call: Call<FavoriteMovieInfoDto>, t: Throwable) {
                eventMessage.onNext(getText(R.string.error_remove_from_favorite_message))
                Timber.d(t)
            }

            override fun onResponse(
                call: Call<FavoriteMovieInfoDto>,
                response: Response<FavoriteMovieInfoDto>,
            ) {
            }
        })
    }
    //endregion

    //region DB
    fun putBrowsingFilmToDB(film: BrowsingFilm) {
        repository.putBrowsingFilmToDB(film)
    }

    fun getFilmsFromDB() = repository.getAllFilmsFromDB()

    fun getMarkedFilmsFromDB() = repository.getAllMarkedFilmsFromDB()

    fun getSearchedMarkedFilms(query: String) = repository.getSearchedMarkedFilms(query)

    fun getCashedBrowsingFilmsFromDB() = repository.getAllBrowsingFilmsFromDB()
    //endregion

    //region Preferences
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    fun getDefaultCategoryFromPreferences(): String {
        return preferences.getDefaultCategory()
    }

    fun saveNightModeToPreferences(mode: Int) {
        preferences.saveNightMode(mode)
    }

    fun getNightModeFromPreferences(): Int {
        return preferences.getNightMode()
    }

    fun setSplashScreenState(state: Boolean) {
        preferences.setPlaySplashScreenState(state)
    }

    fun getSplashScreenStateFromPreferences() = preferences.getPlaySplashScreenState()

    private fun getFavoriteListIdFromPreferences(): Int {
        val id = preferences.getFavoriteFilmListId()
        if (id == DEFAULT_LIST_ID) {
            getFilmListsFromApi().map { it.results }
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onError = { Timber.d(it) },
                    onNext = { resultList ->
                        resultList.find { it.name == FAVORITE_FILM_LIST_NAME }.let {
                            if (it != null) preferences.setFavoriteFilmListId(it.id)
                            else {
                                retrofitService.createFavoriteFilmList(
                                    api_key = API_KEY,
                                    session_id = SESSION_ID,
                                    listInfo = ListInfo(
                                        description = "",
                                        language = "",
                                        name = FAVORITE_FILM_LIST_NAME
                                    )
                                ).execute()
                            }
                        }
                    }
                )
        }
        return id
    }
    //endregion

    fun getRefreshState(): BehaviorSubject<Boolean> {
        return refreshState
    }

    fun getEventMessage(): PublishSubject<String> {
        return eventMessage
    }

    private fun getText(resId: Int): String {
        return App.instance.getText(resId).toString()
    }

    private fun convertToFilmFromApi(list: List<TmdbFilm>) = list.map {
        Film(
            id = it.id,
            title = it.title,
            posterId = it.posterPath ?: "",
            description = it.overview,
            rating = (it.voteAverage * 10).toInt(),
            fav_state = false
        )
    }
}