package xyz.flussikatz.searchmovie.domain

import androidx.lifecycle.LiveData
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
import java.util.concurrent.Executors

class Interactor(
    private val repo: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider
    ) {

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
                    var onEnd = false
                    Executors.newSingleThreadExecutor().execute {
                       while (!onEnd) {
                           if (repo.clearDB() >= 0) {
                               repo.putToDB(list)
                               onEnd = true
                           }
                       }
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
}