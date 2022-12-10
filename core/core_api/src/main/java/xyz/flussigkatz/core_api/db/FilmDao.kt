package xyz.flussigkatz.core_api.db

import androidx.room.*
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.entity.*

@Dao
interface FilmDao {
    //region SearchedFilm
    @Query("SELECT * FROM cashed_films")
    fun getCashedSearchedFilms(): Observable<List<Film>>

    @Query("SELECT * FROM cashed_films")
    fun getCashedSearchedFilmsToList(): List<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSearchedFilms(list: List<Film>)

    @Delete
    fun deleteSearchedFilms(films: List<Film>): Int
    //endregion

    // region PopularFilm
    @Query("SELECT * FROM cashed_popular_films")
    fun getCashedPopularFilms(): Observable<List<PopularFilm>>

    @Query("SELECT * FROM cashed_popular_films")
    fun getCashedPopularFilmsToList(): List<PopularFilm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPopularFilms(list: List<PopularFilm>)

    @Delete
    fun deletePopularFilms(films: List<PopularFilm>): Int
    //endregion

    // region TopRatedFilm
    @Query("SELECT * FROM cashed_top_rated_films")
    fun getCashedTopRatedFilms(): Observable<List<TopRatedFilm>>

    @Query("SELECT * FROM cashed_top_rated_films")
    fun getCashedTopRatedFilmsToList(): List<TopRatedFilm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTopRatedFilms(list: List<TopRatedFilm>)

    @Delete
    fun deleteTopRatedFilms(films: List<TopRatedFilm>): Int
    //endregion

    // region UpcomingFilm
    @Query("SELECT * FROM cashed_upcoming_films")
    fun getCashedUpcomingFilms(): Observable<List<UpcomingFilm>>

    @Query("SELECT * FROM cashed_upcoming_films")
    fun getCashedUpcomingFilmsToList(): List<UpcomingFilm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUpcomingFilms(list: List<UpcomingFilm>)

    @Delete
    fun deleteUpcomingFilms(films: List<UpcomingFilm>): Int
    //endregion

    // region NowPlayingFilm
    @Query("SELECT * FROM cashed_now_playing_films")
    fun getCashedNowPlayingFilms(): Observable<List<NowPlayingFilm>>

    @Query("SELECT * FROM cashed_now_playing_films")
    fun getCashedNowPlayingFilmsToList(): List<NowPlayingFilm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllNowPlayingFilms(list: List<NowPlayingFilm>)

    @Delete
    fun deleteNowPlayingFilms(films: List<NowPlayingFilm>): Int
    //endregion

    //region MarkedFilm
    @Query("SELECT * FROM marked_films")
    fun getCashedMarkedFilms(): Observable<List<MarkedFilm>>

    @Query("SELECT * FROM marked_films")
    fun getCashedMarkedFilmsToList(): List<MarkedFilm>

    @Query("SELECT id FROM marked_films")
    fun getIdsMarkedFilmsToList(): Observable<MutableList<Int>>

    @Query("SELECT * FROM marked_films WHERE id LIKE :id")
    fun getCashedOneMarkedFilm(id: Int): MarkedFilm

    @Query("SELECT * FROM marked_films WHERE title LIKE '%' || :query || '%'")
    fun getSearchedMarkedFilm(query: String): Observable<List<MarkedFilm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMarkedFilms(list: List<MarkedFilm>)

    @Delete
    fun deleteMarkedFilms(films: List<MarkedFilm>): Int
    //endregion

    //region BrowsingFilm
    @Query("SELECT * FROM browsing_films")
    fun getCashedBrowsingFilms(): Observable<List<BrowsingFilm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBrowsingFilm(film: BrowsingFilm)
    //endregion
}