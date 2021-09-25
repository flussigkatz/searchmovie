package xyz.flussikatz.searchmovie.data.dao

import androidx.room.*
import xyz.flussikatz.searchmovie.data.entity.Film

@Dao
interface FilmDao {
    @Query("SELECT * FROM cashed_films")
    fun getCashedFims(): List<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Film>)

    @Delete
    fun deleteFilms(films: List<Film>): Int
}