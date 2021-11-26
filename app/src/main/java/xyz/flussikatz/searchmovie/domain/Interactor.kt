package xyz.flussikatz.searchmovie.domain

import android.text.format.DateFormat
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.remote_module.TmdbApi
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.Api
import xyz.flussikatz.searchmovie.data.ApiConstantsApp
import xyz.flussikatz.searchmovie.data.MainRepository
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.data.entity.MarkedFilm
import xyz.flussikatz.searchmovie.util.Converter
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
                Api.API_KEY,
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

    //TODO: При введении "вен" ошибка
    fun getSearchedFilmsFromApi(search_query: String, page: Int): Observable<List<Film>> {
        val lang = Locale.getDefault().run {
            "$language-$country"
        }
        return retrofitService.getSearchedFilms(
            Api.API_KEY,
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
            Api.ACCOUNT_ID,
            Api.API_KEY,
            Api.SESSION_ID,
            lang,
            ApiConstantsApp.FAVORITE_SORT_BY_CREATED_AT_DESC,
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

    fun getFilmsFromDB(): Observable<List<Film>> {
        return repo.getAllFilmsFromDB()
    }
    fun getMarkedFilmsFromDB(): Observable<List<MarkedFilm>> {
        return repo.getAllMarkedFilmsFromDB()
    }

    fun getMarkedFilmsFromDBToList(): Observable<List<MarkedFilm>> {
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