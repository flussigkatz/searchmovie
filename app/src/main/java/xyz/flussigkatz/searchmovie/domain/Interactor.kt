package xyz.flussigkatz.searchmovie.domain

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.remote_module.TmdbApi
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.Api.ACCOUNT_ID
import xyz.flussigkatz.searchmovie.data.Api.API_KEY
import xyz.flussigkatz.searchmovie.data.Api.SESSION_ID
import xyz.flussigkatz.searchmovie.data.ApiConstantsApp.FAVORITE_SORT_BY_CREATED_AT_DESC
import xyz.flussigkatz.searchmovie.data.MainRepository
import xyz.flussigkatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussigkatz.searchmovie.data.entity.Film
import xyz.flussigkatz.searchmovie.data.entity.MarkedFilm
import xyz.flussigkatz.searchmovie.util.Converter
import java.util.*

class Interactor(
    private val repo: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider,
    private val refreshState: BehaviorSubject<Boolean>,
    private val eventMessage: PublishSubject<String>,
) {
    val language = Locale.getDefault().run {
        "$language-$country"
    }

    fun getFilmsFromApi(page: Int) {

        retrofitService.getFilms(
            getDefaultCategoryFromPreferences(),
            API_KEY,
            language,
            page)
            .subscribeOn(Schedulers.io())
            .filter {
                !it.tmdbFilms.isNullOrEmpty()
            }
            .map { Converter.convertToFilmFromApi(it.tmdbFilms) }
            .doOnSubscribe { refreshState.onNext(true) }
            .doOnComplete {
                refreshState.onNext(false)
            }
            .doOnError {
                refreshState.onNext(false)
                eventMessage.onNext(getText(R.string.error_upload_message))
            }.subscribe(
                {
                    do repo.clearDB() while (repo.clearDB() != 0)
                    repo.putFilmToDB(it)
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

    fun getMarkedFilmsFromApi(page: Int) {
        retrofitService.getFavoriteFilms(
            ACCOUNT_ID,
            API_KEY,
            SESSION_ID,
            language,
            FAVORITE_SORT_BY_CREATED_AT_DESC,
            page
        ).map {
            Converter.convertToMarkedFilmFromApi(it.tmdbFilms)
        }.doOnSubscribe {
            refreshState.onNext(true)
        }.doOnComplete {
            refreshState.onNext(false)
        }.doOnError {
            refreshState.onNext(false)
            eventMessage.onNext(getText(R.string.error_upload_message))
        }.subscribeOn(Schedulers.io())
            .subscribe(
                { repo.putMarkedFilmToDB(it) },
                { println("$TAG getMarkedFilmsFromApi onError: ${it.localizedMessage}") })
    }

    fun deleteMarkedFilmFromDB(id: Int) {
        repo.deleteMarkedFilmFromDB(id)
    }

    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    fun getDefaultCategoryFromPreferences(): String {
        return preferences.getDefaultCategory()
    }

    fun setDefaultTheme(theme: Int) {
        preferences.saveDefaultTheme(theme)
    }

    fun getDefaultThemeFromPreferences(): Int {
        return preferences.getDefaultTheme()
    }

    fun setSplashScreenState(state: Boolean) {
        preferences.setPlaySplashScreenState(state)
    }

    fun getSplashScreenStateFromPreferences(): Boolean {
        return preferences.getPlaySplashScreenState()
    }

    fun getFilmsFromDB(): Observable<List<Film>> {
        return repo.getAllFilmsFromDB()
    }

    fun getMarkedFilmsFromDB(): Observable<List<MarkedFilm>> {
        return repo.getAllMarkedFilmsFromDB()
    }

    fun getSearchedMarkedFilms(query: String): Observable<List<MarkedFilm>> {
        return repo.getSearchedMarkedFilms(query)
    }

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