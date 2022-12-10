package xyz.flussigkatz.searchmovie.domain

import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.*
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
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NOW_PLAYING_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.POPULAR_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.TOP_RATED_CATEGORY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.UPCOMING_CATEGORY
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
    private val language = Locale.getDefault().run { "$language-$country" }
    private var listIdsMarkedFilms = mutableListOf<Int>()

    init {
        checkFavoriteListIdStatus()
        getMarkedFilmsFromApi()
        initListIdsMarkedFilms()
    }

    //region Api
    fun getFilmsFromApi(category: String, page: Int) {
        retrofitService.getFilms(
            category,
            API_KEY,
            language,
            page
        ).filter { it.tmdbFilms.isNotEmpty() }
            .doOnSubscribe { refreshState.onNext(true) }
            .doOnComplete { refreshState.onNext(false) }
            .doOnError {
                refreshState.onNext(false)
                eventMessage.onNext(getText(R.string.error_upload_message))
            }.subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = {
                    when (category) {
                        POPULAR_CATEGORY -> {
                            repository.clearCashedPopularFilmsDB()
                            repository.putPopularFilmsToDB(
                                Converter.convertToPopularFilmFromApi(
                                    it.tmdbFilms,
                                    listIdsMarkedFilms
                                )
                            )
                        }
                        TOP_RATED_CATEGORY -> {
                            repository.clearCashedTopRatedFilmsDB()
                            repository.putTopRatedFilmsToDB(
                                Converter.convertToTopRatedFilmFromApi(
                                    it.tmdbFilms,
                                    listIdsMarkedFilms
                                )
                            )
                        }
                        UPCOMING_CATEGORY -> {
                            repository.clearCashedUpcomingFilmsDB()
                            repository.putUpcomingFilmsToDB(
                                Converter.convertToUpcomingFilmFromApi(
                                    it.tmdbFilms,
                                    listIdsMarkedFilms
                                )
                            )
                        }
                        NOW_PLAYING_CATEGORY -> {
                            repository.clearCashedNowPlayingFilmsDB()
                            repository.putNowPlayingFilmsToDB(
                                Converter.convertToNowPlayingFilmFromApi(
                                    it.tmdbFilms,
                                    listIdsMarkedFilms
                                )
                            )
                        }
                        else -> throw IllegalArgumentException("Wrong film category")
                    }
                }
            )
    }

    fun getSearchedFilmsFromApi(search_query: String, page: Int) {
        retrofitService.getSearchedFilms(
            api_key = API_KEY,
            language = language,
            query = search_query,
            page = page
        ).filter { it.tmdbFilms.isNotEmpty() }
            .map { Converter.convertToFilmFromApi(it.tmdbFilms, listIdsMarkedFilms) }
            .doOnSubscribe { refreshState.onNext(true) }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = {
                    refreshState.onNext(false)
                    eventMessage.onNext(getText(R.string.error_upload_message))
                    Timber.d(it)
                },
                onComplete = { refreshState.onNext(false) },
                onNext = {
                    repository.clearCashedSearchedFilmsDB()
                    repository.putSearchedFilmsToDB(it)
                }
            )
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
        ).filter { it.success }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = { listIdsMarkedFilms.add(id) }
            )
    }

    fun removeFavoriteFilmFromList(id: Int) {
        retrofitService.removeFavoriteFilmFromList(
            list_id = getFavoriteListIdFromPreferences(),
            api_key = API_KEY,
            session_id = SESSION_ID,
            favoriteMovieInfo = FavoriteMovieInfoDto(mediaId = id)
        ).filter { it.success }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = { listIdsMarkedFilms.add(id) }
            )
    }

    private fun createFavoriteFilmList() {
        retrofitService.createFavoriteFilmList(
            api_key = API_KEY,
            session_id = SESSION_ID,
            listInfo = ListInfo(
                description = "",
                language = "",
                name = FAVORITE_FILM_LIST_NAME
            )
        ).filter { it.success }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = { preferences.setFavoriteFilmListId(it.listId) }
            )
    }
    //endregion

    //region DB
    fun putBrowsingFilmToDB(film: BrowsingFilm) {
        repository.putBrowsingFilmToDB(film)
    }

    fun getSearchedFilmsFromDB() = repository.getAllSearchedFilmsFromDB()

    fun getMarkedFilmsFromDB() = repository.getAllMarkedFilmsFromDB()

    fun getPopularFilmsFromDB() = repository.getAllPopularFilmsFromDB()

    fun getTopRatedFilmsFromDB() = repository.getAllTopRatedFilmsFromDB()

    fun getUpcomingFilmsFromDB() = repository.getAllUpcomingFilmsFromDB()

    fun getNowPlayingFilmsFromDB() = repository.getAllNowPlayingFilmsFromDB()

    fun getSearchedMarkedFilms(query: String) = repository.getSearchedMarkedFilms(query)

    fun getCashedBrowsingFilmsFromDB() = repository.getAllBrowsingFilmsFromDB()

    private fun getIdsMarkedFilmsToListFromDB() = repository.getIdsMarkedFilmsToListFromDB()

    fun clearSearchedFilmDB() {
        repository.clearCashedSearchedFilmsDB()
    }
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

    private fun getFavoriteListIdFromPreferences() = preferences.getFavoriteFilmListId()
    //endregion

    private fun initListIdsMarkedFilms() {
        getIdsMarkedFilmsToListFromDB().subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onNext = { listIdsMarkedFilms = it }
            )
    }

    fun getRefreshState() = refreshState

    fun getEventMessage() = eventMessage

    private fun getText(resId: Int) = App.instance.getText(resId).toString()

    private fun checkFavoriteListIdStatus() {
        if (preferences.getFavoriteFilmListId() == DEFAULT_LIST_ID) {
            getFilmListsFromApi().map { it.results }
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onError = { Timber.d(it) },
                    onNext = { resultList ->
                        resultList.find { it.name == FAVORITE_FILM_LIST_NAME }?.let {
                            preferences.setFavoriteFilmListId(it.id)
                        } ?: createFavoriteFilmList()
                    }
                )
        }
    }
}