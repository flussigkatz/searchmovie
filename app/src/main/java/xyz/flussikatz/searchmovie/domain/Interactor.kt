package xyz.flussikatz.searchmovie.domain

import android.text.format.DateFormat
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.flussikatz.searchmovie.App
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.Api
import xyz.flussikatz.searchmovie.data.MainRepository
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.data.entity.TmdbResultsDto
import xyz.flussikatz.searchmovie.data.TmdbApi
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.data.entity.TmdbFilm

class Interactor(
    private val repo: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider,
    private val scope: CoroutineScope,
    private val refreshState: BehaviorSubject<Boolean>,
    private val eventMessage: PublishSubject<String>,
) {

    fun getFilmsFromApi(page: Int) {
        refreshState.onNext(true)
        val realTime = System.currentTimeMillis()
        val lastLoadTime = preferences.getLoadFromApiTimeInterval()
        if (lastLoadTime + TIME_INTERVAL < realTime) {
            retrofitService.getFilms(
                getDefaultCategoryFromPreferences(),
                Api.API_KEY,
                "ru-RU",
                page)
                .enqueue(object : Callback<TmdbResultsDto> {
                    override fun onResponse(
                        call: Call<TmdbResultsDto>,
                        response: Response<TmdbResultsDto>,
                    ) {
                        val responseObservable = Observable.create<List<TmdbFilm>> {
                            it.onNext(response.body()?.tmdbFilms)
                            it.onComplete()
                        }
                        responseObservable.doOnSubscribe {
                            do {
                                repo.clearDB()
                            } while (repo.clearDB() != 0)
                        }.doOnComplete {
                            refreshState.onNext(false)
                            preferences.saveLoadFromApiTimeInterval(System.currentTimeMillis())
                        }.doOnError {
                            it.printStackTrace()
                        }.subscribeOn(Schedulers.io()).map {
                            val result = mutableListOf<Film>()
                            it.forEach {
                                result.add(
                                    Film(
                                        id = it.id,
                                        title = it.title,
                                        posterId = it.posterPath,
                                        description = it.overview,
                                        rating = (it.voteAverage * 10).toInt()
                                    )
                                )
                            }
                            result
                        }.subscribe {
                                repo.putToDB(it)
                            }
                    }

                    override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                        refreshState.onNext(false)
                        eventMessage.onNext(getText(R.string.error_upload_message))
                    }

                })
        } else {
            refreshState.onNext(false)
            val timeFormatted = timeFormatter(realTime - lastLoadTime)
            eventMessage.onNext(
                timeFormatted + getText(R.string.upload_time_interval_massage)
            )

        }
    }

    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    fun getDefaultCategoryFromPreferences(): String {
        return preferences.getDefaultCategory()
    }

    fun dropLoadFromApiTimeIntervalFromPreferences() {
        preferences.saveLoadFromApiTimeInterval(0)
    }

    fun getFilmsFromDB(): Observable<List<Film>> {
        return repo.getAllFromDB()
    }

    fun getRefreshState(): BehaviorSubject<Boolean> {
        return refreshState
    }

    fun getEventMessage(): PublishSubject<String> {
        return eventMessage
    }

    private fun timeFormatter(time: Long): String {
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