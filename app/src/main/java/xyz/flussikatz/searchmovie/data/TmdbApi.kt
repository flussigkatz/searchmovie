package xyz.flussikatz.searchmovie.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import xyz.flussikatz.searchmovie.data.entity.TmdbResultsDto

interface TmdbApi {
    @GET("movie/popular")
    fun getFilms(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("page") page:Int
    ): Call<TmdbResultsDto>

}