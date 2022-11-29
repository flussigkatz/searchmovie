package xyz.flussigkatz.remote_module

import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import xyz.flussigkatz.remote_module.entity.FavoriteMovieInfoDto
import xyz.flussigkatz.remote_module.entity.FavoriteMovieListDto.FavoriteMovieListDto
import xyz.flussigkatz.remote_module.entity.FilmListsDto.FilmListsDto
import xyz.flussigkatz.remote_module.entity.ListInfo
import xyz.flussigkatz.remote_module.entity.MarkFimStatusDto
import xyz.flussigkatz.remote_module.entity.TmdbResultDto.TmdbResultsDto

interface TmdbApi {
    //region GET
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
        @Query("page") page: Int,
    ): Observable<TmdbResultsDto>

    @GET("list/{list_id}")
    fun getMarkedFilms(
        @Path("list_id") list_id: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String,
    ): Observable<FavoriteMovieListDto>

    @GET("list/{list_id}/item_status")
    fun getFilmMarkStatus(
        @Path("list_id") list_id: Int,
        @Query("api_key") api_key: String,
        @Query("movie_id") movie_id: Int,
    ): Observable<MarkFimStatusDto>

    @GET("account/{account_id}/lists")
    fun getFilmLists(
        @Path("account_id") account_id: String,
        @Query("api_key") api_key: String,
        @Query("session_id") session_id: String,
        @Query("language") language: String,
    ): Observable<FilmListsDto>
    //endregion

    //region POST
    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("list")
    fun createFavoriteFilmList(
        @Query("api_key") api_key: String,
        @Query("session_id") session_id: String,
        @Body listInfo: ListInfo,
    ): Call<ListInfo>

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("list/{list_id}/add_item")
    fun addFavoriteFilmToList(
        @Path("list_id") list_id: Int,
        @Query("api_key") api_key: String,
        @Query("session_id") session_id: String,
        @Body favoriteMovieInfo: FavoriteMovieInfoDto,
    ): Call<FavoriteMovieInfoDto>

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("list/{list_id}/remove_item")
    fun removeFavoriteFilmFromList(
        @Path("list_id") list_id: Int,
        @Query("api_key") api_key: String,
        @Query("session_id") session_id: String,
        @Body favoriteMovieInfo: FavoriteMovieInfoDto,
    ): Call<FavoriteMovieInfoDto>
    //endregion
}