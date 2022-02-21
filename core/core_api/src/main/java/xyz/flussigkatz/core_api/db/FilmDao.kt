package xyz.flussigkatz.core_api.db

import androidx.room.*
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.entity.Film
import xyz.flussigkatz.core_api.entity.MarkedFilm

@Dao
interface FilmDao {
    @Query("SELECT * FROM cashed_films")
    fun getCashedFilms(): Observable<List<Film>>

    @Query("SELECT * FROM cashed_films")
    fun getCashedFilmsToList(): List<Film>

    @Query("SELECT * FROM marked_films")
    fun getCashedMarkedFilms(): Observable<List<MarkedFilm>>

    @Query("SELECT * FROM marked_films")
    fun getCashedMarkedFilmsToList(): List<MarkedFilm>

    @Query("SELECT * FROM marked_films WHERE id LIKE :id")
    fun getCashedOneMarkedFilm(id: Int): MarkedFilm

    @Query("SELECT * FROM marked_films WHERE title LIKE '%' || :query || '%'")
    fun getSearchedMarkedFilm(query: String): Observable<List<MarkedFilm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFilms(list: List<Film>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMarkedFilms(list: List<MarkedFilm>)

    @Delete
    fun deleteFilms(films: List<Film>): Int

    @Delete
    fun deleteOneMarkedFilm(film: MarkedFilm)
}