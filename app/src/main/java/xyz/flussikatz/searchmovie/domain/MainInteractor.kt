package xyz.flussikatz.searchmovie.domain

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.flussikatz.searchmovie.data.Api
import xyz.flussikatz.searchmovie.data.entity.TmdbResultsDto
import xyz.flussikatz.searchmovie.data.TmdbApi
import xyz.flussikatz.searchmovie.util.Converter
import xyz.flussikatz.searchmovie.viewmodel.HomeFragmentViewModel

class MainInteractor(private val retrofitService: TmdbApi) : Interactor {

    override fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        retrofitService.getFilms(Api.API_KEY, "ru-RU", page)
            .enqueue(object : Callback<TmdbResultsDto> {
                override fun onResponse(
                    call: Call<TmdbResultsDto>,
                    response: Response<TmdbResultsDto>
                ) {
                    callback.onSuccess(
                        Converter.convertApiListToDtoList(response.body()?.tmdbFilms)
                    )
                }

                override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                    callback.onFailure()
                }

            })
    }
}