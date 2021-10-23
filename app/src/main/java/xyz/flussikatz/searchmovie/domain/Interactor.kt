package xyz.flussikatz.searchmovie.domain

import android.text.format.DateFormat
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
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
import xyz.flussikatz.searchmovie.util.Converter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Interactor(
    private val repo: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider,
    private val scope: CoroutineScope,
    private val channelRefreshState: Channel<Boolean>,
    private val channelEventMessage: Channel<String>
) {

    fun getFilmsFromApi(page: Int) {
        val job = scope.launch {
            channelRefreshState.send(true)
        }
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
                        response: Response<TmdbResultsDto>
                    ) {
                        //TODO
                        val list = Converter.convertApiListToDtoList(response.body()?.tmdbFilms)
                        scope.launch {
                            clearDB()
                            repo.putToDB(list)
                            job.join()
                            channelRefreshState.send(false)
                            preferences.saveLoadFromApiTimeInterval(System.currentTimeMillis())
                        }
                    }

                    override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                        scope.launch {
                            job.join()
                            channelRefreshState.send(false)
                            channelEventMessage.send(getText(R.string.error_upload_message))
                        }
                    }

                })
        } else {
            scope.launch {
                job.join()
                channelRefreshState.send(false)
                val timeFormatted = timeFormatter(realTime - lastLoadTime)
                channelEventMessage.send(
                    timeFormatted + getText(R.string.upload_time_interval_massage)
                )
            }

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

    fun getFilmsFromDB(): Flow<List<Film>> {
        return repo.getAllFromDB()
    }

    fun getChannelRefreshState(): Channel<Boolean> {
        return channelRefreshState
    }

    fun getChannelEventMessage(): Channel<String> {
        return channelEventMessage
    }

    private suspend fun clearDB() {
        return suspendCoroutine {
            do {
                repo.clearDB()
            } while (repo.clearDB() != 0)
            it.resume(Unit)
        }
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