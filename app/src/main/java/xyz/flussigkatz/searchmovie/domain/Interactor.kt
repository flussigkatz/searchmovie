package xyz.flussigkatz.searchmovie.domain

import android.text.format.DateFormat
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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

    //TODO: Dispose observable

    fun getFilmsFromApi(page: Int) {
        val lang = Locale.getDefault().run {
            "$language-$country"
        }
        if (checkUploadInterval()) {
            retrofitService.getFilms(
                getDefaultCategoryFromPreferences(),
                API_KEY,
                lang,
                page).map {
                Converter.convertToFilmFromApi(it.tmdbFilms)
            }.doOnSubscribe {
                refreshState.onNext(true)
                do {
                    repo.clearDB()
                } while (repo.clearDB() != 0)
            }.doOnComplete {
                refreshState.onNext(false)
                preferences.saveLoadFromApiTimeInterval(System.currentTimeMillis())
            }.doOnError {
                refreshState.onNext(false)
                eventMessage.onNext(getText(R.string.error_upload_message))
            }.subscribeOn(Schedulers.io())
                .subscribe {
                    repo.putFilmToDB(it)
                }
        } else {
            refreshState.onNext(false)
            eventMessage.onNext(
                timeFormatter() + getText(R.string.upload_time_interval_massage)
            )
        }
    }
    fun getSpecificFilmFromApi(id: String): Observable<Film> {
        val lang = Locale.getDefault().run {
            "$language-$country"
        }
        return retrofitService.getSpecificFilm(id, API_KEY, lang)
            .filter {it != null}
            .map { Converter.convertToFilmFromApi(it) }
            .doOnError {
                println(it.message)
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    //TODO: При введении "вен" ошибка
    fun getSearchedFilmsFromApi(search_query: String, page: Int): Observable<List<Film>> {
        val lang = Locale.getDefault().run {
            "$language-$country"
        }
        return retrofitService.getSearchedFilms(
            API_KEY,
            lang,
            search_query,
            page
        ).map {
            Converter.convertToFilmFromApi(it.tmdbFilms)
        }.doOnError {
            eventMessage.onNext(getText(R.string.error_upload_message))
        }
    }

    fun getMarkedFilmsFromApi(page: Int) {
        val lang = Locale.getDefault().run {
            "$language-$country"
        }
        retrofitService.getFavoriteFilms(
            ACCOUNT_ID,
            API_KEY,
            SESSION_ID,
            lang,
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
            .subscribe {
                repo.putMarkedFilmToDB(it)
            }
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
        App.instance.initTheme(theme)
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

    fun getMarkedFilmsFromDBToList(): Observable<List<MarkedFilm>>? {
        return repo.getAllMarkedFilmsDBToList()
    }

//    fun setFavoriteMark(id: Int) {
    //TODO: create setFavoriteMark
//    }

    fun getRefreshState(): BehaviorSubject<Boolean> {
        return refreshState
    }

    fun getEventMessage(): PublishSubject<String> {
        return eventMessage
    }

    fun checkUploadInterval(): Boolean {
        var res = false
        val realTime = System.currentTimeMillis()
        val lastLoadTime = preferences.getLoadFromApiTimeInterval()
        if (lastLoadTime + TIME_INTERVAL < realTime) {
            res = true
        }
        return res
    }

    private fun timeFormatter(): String {
        val realTime = System.currentTimeMillis()
        val lastLoadTime = preferences.getLoadFromApiTimeInterval()
        val time = realTime - lastLoadTime
        val min = DateFormat.format("mm", time)
        val sec = DateFormat.format("ss", time)
        val arr = arrayOf(min, sec).map {
            if (it[0].toString().equals("0")) {
                it[1].toString()
            } else it
        }
        var res = ""

        if (time >= ONE_MIN) {
            res = "${arr[0]} ${getText(R.string.min)} "
        }
        res += "${arr[1]} ${getText(R.string.sec)} "

        return res
    }

    private fun getText(resId: Int): String {
        return App.instance.getText(resId).toString()
    }

    companion object {
        private const val TIME_INTERVAL = 600000L
        private const val ONE_MIN = 60000L
    }
}