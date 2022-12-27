package xyz.flussigkatz.remote_module

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import xyz.flussigkatz.remote_module.entity.*
import xyz.flussigkatz.remote_module.entity.favorite_movie_list_dto.FavoriteMovieListDto
import xyz.flussigkatz.remote_module.entity.film_lists_dto.FilmListsDto
import xyz.flussigkatz.remote_module.entity.tmdb_result_dto.TmdbResultsDto

interface TmdbApi {
    //region GET
    @GET("movie/{category}")
    suspend fun getFilms(
        @Path("category") category: String,
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("page") page: Int,
    ): TmdbResultsDto

    @GET("search/movie")
    suspend fun getSearchedFilms(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: Int,
    ): TmdbResultsDto

    @GET("list/{list_id}")
    suspend fun getMarkedFilms(
        @Path("list_id") list_id: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String,
    ): FavoriteMovieListDto

    @GET("list/{list_id}/item_status")
    suspend fun getFilmMarkStatus(
        @Path("list_id") list_id: Int,
        @Query("api_key") api_key: String,
        @Query("movie_id") movie_id: Int,
    ): MarkFimStatusDto

    @GET("account/{account_id}/lists")
    suspend fun getFilmLists(
        @Path("account_id") account_id: String,
        @Query("api_key") api_key: String,
        @Query("session_id") session_id: String,
        @Query("language") language: String,
    ): FilmListsDto
    //endregion

    //region POST
    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("list")
    suspend fun createFavoriteFilmList(
        @Query("api_key") api_key: String,
        @Query("session_id") session_id: String,
        @Body listInfo: ListInfo,
    ): CreateFavoriteListResponseDto

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("list/{list_id}/add_item")
    suspend fun addFavoriteFilmToList(
        @Path("list_id") list_id: Int,
        @Query("api_key") api_key: String,
        @Query("session_id") session_id: String,
        @Body favoriteMovieInfo: FavoriteMovieInfoDto,
    ): FavoriteFilmAddingRemovingResponse

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("list/{list_id}/remove_item")
    suspend fun removeFavoriteFilmFromList(
        @Path("list_id") list_id: Int,
        @Query("api_key") api_key: String,
        @Query("session_id") session_id: String,
        @Body favoriteMovieInfo: FavoriteMovieInfoDto,
    ): FavoriteFilmAddingRemovingResponse
    //endregion
}