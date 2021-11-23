package xyz.flussikatz.searchmovie.data.dao

import androidx.room.*
import io.reactivex.rxjava3.core.Observable
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.data.entity.MarkedFilm

@Dao
interface FilmDao {
    @Query("SELECT * FROM cashed_films")
    fun getCashedFilms(): Observable<List<Film>>

    @Query("SELECT * FROM marked_films")
    fun getCashedMarkedFilms(): Observable<List<MarkedFilm>>

    @Query("SELECT * FROM marked_films WHERE id LIKE :id")
    fun getOneCashedMarkedFilms(id: Int): MarkedFilm

    @Query("SELECT * FROM cashed_films")
    fun getCashedFilmsToList(): List<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFilms(list: List<Film>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMarkedFilms(list: List<MarkedFilm>)

    @Delete
    fun deleteFilms(films: List<Film>): Int
}