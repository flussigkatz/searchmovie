package xyz.flussigkatz.core_api.db

import androidx.paging.PagingSource
import androidx.room.*
import xyz.flussigkatz.core_api.entity.*

@Dao
interface FilmDao {
    //region SearchedFilm
    @Query("SELECT * FROM searched_films ORDER BY localId DESC")
    fun getSearchedFilms(): PagingSource<Int, SearchedFilm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchedFilms(list: List<SearchedFilm>)

    @Query("DELETE FROM searched_films")
    suspend fun deleteSearchedFilms()

    @Transaction
    suspend fun refreshSearchedFilms(films: List<SearchedFilm>) {
        deleteSearchedFilms()
        insertSearchedFilms(films)
    }
    //endregion

    // region PopularFilm
    @Query("SELECT * FROM popular_films")
    fun getPopularFilms(): PagingSource<Int, PopularFilm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPopularFilms(list: List<PopularFilm>)

    @Query("DELETE FROM popular_films")
    suspend fun deletePopularFilms()

    @Transaction
    suspend fun refreshPopularFilms(films: List<PopularFilm>) {
        deletePopularFilms()
        insertPopularFilms(films)
    }
    //endregion

    // region TopRatedFilm
    @Query("SELECT * FROM top_rated_films")
    fun getTopRatedFilms(): PagingSource<Int, TopRatedFilm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopRatedFilms(list: List<TopRatedFilm>)

    @Query("DELETE FROM top_rated_films")
    suspend fun deleteTopRatedFilms()

    @Transaction
    suspend fun refreshTopRatedFilms(films: List<TopRatedFilm>) {
        deleteTopRatedFilms()
        insertTopRatedFilms(films)
    }
    //endregion

    // region UpcomingFilm
    @Query("SELECT * FROM upcoming_films")
    fun getUpcomingFilms(): PagingSource<Int, UpcomingFilm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpcomingFilms(list: List<UpcomingFilm>)

    @Query("DELETE FROM upcoming_films")
    suspend fun deleteUpcomingFilms()

    @Transaction
    suspend fun refreshUpcomingFilms(films: List<UpcomingFilm>) {
        deleteUpcomingFilms()
        insertUpcomingFilms(films)
    }
    //endregion

    // region NowPlayingFilm
    @Query("SELECT * FROM now_playing_films")
    fun getNowPlayingFilms(): PagingSource<Int, NowPlayingFilm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNowPlayingFilms(list: List<NowPlayingFilm>)

    @Query("DELETE FROM now_playing_films")
    suspend fun deleteNowPlayingFilms()

    @Transaction
    suspend fun refreshNowPlayingFilms(films: List<NowPlayingFilm>) {
        deleteNowPlayingFilms()
        insertNowPlayingFilms(films)
    }
    //endregion

    //region MarkedFilm
    @Query("SELECT * FROM marked_films WHERE title LIKE '%' || :query || '%' ORDER BY localId DESC")
    fun getMarkedFilms(query: String): PagingSource<Int, MarkedFilm>

    @Query("SELECT id FROM marked_films")
    fun getIdsMarkedFilms(): List<Int>

    @Query("SELECT * FROM marked_films WHERE id LIKE :id")
    fun getMarkedFilmById(id: Int): MarkedFilm

    @Query("SELECT * FROM marked_films WHERE title LIKE '%' || :query || '%'")
    fun getSearchedMarkedFilm(query: String): PagingSource<Int, MarkedFilm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarkedFilms(list: List<MarkedFilm>)

    @Query("DELETE FROM marked_films")
    suspend fun deleteMarkedFilms()

    @Transaction
    suspend fun refreshMarkedFilms(films: List<MarkedFilm>) {
        deleteMarkedFilms()
        insertMarkedFilms(films)
    }
    //endregion

    //region BrowsingFilm
    @Query("SELECT * FROM browsing_films WHERE title LIKE '%' || :query || '%' ORDER BY localId DESC")
    fun getBrowsingFilms(query: String): PagingSource<Int, BrowsingFilm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrowsingFilm(film: BrowsingFilm)
    //endregion
}