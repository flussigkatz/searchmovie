package xyz.flussigkatz.remote_module

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import xyz.flussigkatz.searchmovie.data.entity.TmdbResultsDto

interface TmdbApi {
    @GET("movie/{category}")
    fun getFilms(
        @Path("category") category: String,
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("page") page: Int,
    ): Observable<TmdbResultsDto>

    @GET("search/movie")
    fun getSearchedFilms(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Observable<TmdbResultsDto>

    @GET("account/{account_id}/favorite/movies")
    fun getFavoriteFilms(
        @Path("account_id") account_id: String,
        @Query("api_key") api_key: String,
        @Query("session_id") session_id: String,
        @Query("language") language: String,
        @Query("sort_by") sort_by: String,
        @Query("page") page: Int
    ): Observable<TmdbResultsDto>

}