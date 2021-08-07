package xyz.flussikatz.searchmovie.domain

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.flussikatz.searchmovie.data.Api
import xyz.flussikatz.searchmovie.data.Entity.TmdbResultsDto
import xyz.flussikatz.searchmovie.data.MainRepository
import xyz.flussikatz.searchmovie.data.TmdbApi
import xyz.flussikatz.searchmovie.util.Converter
import xyz.flussikatz.searchmovie.viewmodel.HomeFragmentViewModel

class Interactor(private val repo: MainRepository, private val retrofitService: TmdbApi) {

    fun getFilmsFromApi (page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        retrofitService.getFilms(Api.API_KEY, "ru-RU", page).enqueue(object : Callback<TmdbResultsDto> {
            override fun onResponse(
                call: Call<TmdbResultsDto>,
                response: Response<TmdbResultsDto>
            ) {
                callback.onSuccess(Converter.convertApiListToDtoList(response.body()?.tmdbFilms))
            }

            override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                callback.onFailure()
            }

        })
    }
}