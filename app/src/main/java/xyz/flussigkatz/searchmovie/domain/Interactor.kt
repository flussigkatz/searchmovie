package xyz.flussigkatz.searchmovie.domain

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.flussigkatz.core_api.entity.Film
import xyz.flussigkatz.core_api.entity.MarkedFilm
import xyz.flussigkatz.remote_module.TmdbApi
import xyz.flussigkatz.remote_module.entity.FavoriteMovieInfoDto
import xyz.flussigkatz.remote_module.entity.ListInfo
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.Api.ACCOUNT_ID
import xyz.flussigkatz.searchmovie.data.Api.API_KEY
import xyz.flussigkatz.searchmovie.data.Api.SESSION_ID
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DEFAULT_LIST_ID
import xyz.flussigkatz.searchmovie.data.ConstantsApp.FAVORITE_FILM_LIST_NAME
import xyz.flussigkatz.searchmovie.data.MainRepository
import xyz.flussigkatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussigkatz.searchmovie.util.Converter
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
            page)
            .subscribeOn(Schedulers.io())
            .filter { it.tmdbFilms.isNotEmpty() }
            .map { Converter.convertToFilmFromApi(it.tmdbFilms) }
            .doOnSubscribe { refreshState.onNext(true) }
            .doOnComplete { refreshState.onNext(false) }
            .doOnError {
                refreshState.onNext(false)
                eventMessage.onNext(getText(R.string.error_upload_message))
            }.subscribe(
                {
                    repository.clearCashedFilmsDB()
                    repository.putFilmToDB(it)
                },
                { println("$TAG getFilmsFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getSearchedFilmsFromApi(search_query: String, page: Int): Observable<List<Film>> {
        return retrofitService.getSearchedFilms(
            API_KEY,
            language,
            search_query,
            page
        ).map {
            Converter.convertToFilmFromApi(it.tmdbFilms)
        }.doOnError {
            eventMessage.onNext(getText(R.string.error_upload_message))
        }
    }

    fun getMarkedFilmsFromApi() {
        retrofitService.getMarkedFilms(
            list_id = getFavoriteListIdFromPreferences(),
            api_key = API_KEY,
            language = language,
        ).map {
            Converter.convertToMarkedFilmFromApi(it.favoriteListItems)
        }.doOnSubscribe {
            refreshState.onNext(true)
        }.doOnComplete {
            refreshState.onNext(false)
        }.doOnError {
            refreshState.onNext(false)
            eventMessage.onNext(getText(R.string.error_upload_message))
        }.subscribeOn(Schedulers.io())
            .subscribe(
                {
                    repository.clearMarkedFilmsDB()
                    repository.putMarkedFilmToDB(it)
                },
                { println("$TAG getMarkedFilmsFromApi onError: ${it.localizedMessage}") })
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
                println("$TAG addFavoriteFilmToList onFailure: ${t.localizedMessage}")
            }

            override fun onResponse(
                call: Call<FavoriteMovieInfoDto>,
                response: Response<FavoriteMovieInfoDto>,
            ) {
                println("$TAG addFavoriteFilmToList onResponse: ${response.body()}")
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
                println("$TAG removeFavoriteFilmToList onFailure: ${t.localizedMessage}")
            }

            override fun onResponse(
                call: Call<FavoriteMovieInfoDto>,
                response: Response<FavoriteMovieInfoDto>,
            ) {
                println("$TAG removeFavoriteFilmToList onResponse: ${response.body()}")
            }
        })
    }
    //endregion

    //region DB
    fun getFilmsFromDB(): Observable<List<Film>> {
        return repository.getAllFilmsFromDB()
    }

    fun getMarkedFilmsFromDB(): Observable<List<MarkedFilm>> {
        return repository.getAllMarkedFilmsFromDB()
    }

    fun getSearchedMarkedFilms(query: String): Observable<List<MarkedFilm>> {
        return repository.getSearchedMarkedFilms(query)
    }
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

    fun getSplashScreenStateFromPreferences(): Boolean {
        return preferences.getPlaySplashScreenState()
    }

    private fun getFavoriteListIdFromPreferences(): Int {
        val id = preferences.getFavoriteFilmListId()
        if (id == DEFAULT_LIST_ID) {
            getFilmListsFromApi().map { it.results }
                .doOnError {
                    eventMessage.onNext(getText(R.string.error_upload_message))
                }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { resultList ->
                        resultList.find { it.name == FAVORITE_FILM_LIST_NAME }.let {
                            if (it != null) {
                                preferences.setFavoriteFilmListId(it.id)
                            } else {
                                retrofitService.createFavoriteFilmList(
                                    api_key = API_KEY,
                                    session_id = SESSION_ID,
                                    listInfo = ListInfo(description = "",
                                        language = "",
                                        name = FAVORITE_FILM_LIST_NAME)).execute()
                            }
                        }
                    },
                    { println("$TAG getFavoriteListId onError: ${it.localizedMessage}") })
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

    companion object {
        private const val TAG = "Interactor"
    }
}