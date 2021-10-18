package xyz.flussikatz.searchmovie.domain

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.flussikatz.searchmovie.data.Api
import xyz.flussikatz.searchmovie.data.ApiCallback
import xyz.flussikatz.searchmovie.data.MainRepository
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.data.entity.TmdbResultsDto
import xyz.flussikatz.searchmovie.data.TmdbApi
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.util.Converter
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Interactor(
    private val repo: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider
) {
    val scope = CoroutineScope(EmptyCoroutineContext)

    fun getFilmsFromApi(page: Int, callback: ApiCallback) {
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
                    val list = Converter.convertApiListToDtoList(response.body()?.tmdbFilms)
                    scope.launch {
                        clearDB()
                        repo.putToDB(list)
                    }
                    callback.onSuccess()
                }

                override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                    callback.onFailure()
                }

            })
    }

    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    fun getDefaultCategoryFromPreferences(): String {
        return preferences.getDefaultCategory()
    }

    fun saveLoadFromApiTimeIntervalToPreferences(time: Long) {
        preferences.saveLoadFromApiTimeInterval(time)
    }

    fun getLoadFromApiTimeIntervalToPreferences(): Long {
        return preferences.getLoadFromApiTimeInterval()
    }

    fun getFilmsFromDB(): LiveData<List<Film>> {
        return repo.getAllFromDB()
    }

    fun clearScope() {
        scope.cancel()
    }

    private suspend fun clearDB() {
        return suspendCoroutine {
            do {
                repo.clearDB()
            } while (repo.clearDB() != 0)
            it.resume(Unit)
        }
    }
}