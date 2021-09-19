package xyz.flussikatz.searchmovie.domain

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.flussikatz.searchmovie.data.Api
import xyz.flussikatz.searchmovie.data.MainRepository
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.data.entity.TmdbResultsDto
import xyz.flussikatz.searchmovie.data.TmdbApi
import xyz.flussikatz.searchmovie.util.Converter
import xyz.flussikatz.searchmovie.viewmodel.HomeFragmentViewModel

class Interactor(
    private val repo: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider
    ) {

    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
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
                    list.forEach{repo.putToDb(film = it)}
                    callback.onSuccess(list)
                }

                override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                    callback.onFailure()
                }

            })
    }

    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()

    fun getFilmsFromDb(): List<Film> = repo.getAllFromDb()
}