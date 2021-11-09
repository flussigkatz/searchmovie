package xyz.flussigkatz.core_api.db

import androidx.room.*
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.entity.Film

@Dao
interface FilmDao {
    @Query("SELECT * FROM cashed_films")
    fun getCashedFilms(): Observable<List<Film>>

    @Query("SELECT * FROM cashed_films")
    fun getCashedFilmsToList(): List<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Film>)

    @Delete
    fun deleteFilms(films: List<Film>): Int
}