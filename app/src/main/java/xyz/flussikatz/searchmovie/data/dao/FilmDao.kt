package xyz.flussikatz.searchmovie.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import xyz.flussikatz.searchmovie.data.entity.Film

@Dao
interface FilmDao {
    @Query("SELECT * FROM cashed_films")
    fun getCashedFilms(): Flow<List<Film>>

    @Query("SELECT * FROM cashed_films")
    fun getCashedFilmsToList(): List<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Film>)

    @Delete
    fun deleteFilms(films: List<Film>): Int
}