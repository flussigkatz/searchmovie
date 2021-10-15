package xyz.flussikatz.searchmovie.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import xyz.flussikatz.searchmovie.data.entity.Film

@Dao
interface FilmDao {
    @Query("SELECT * FROM cashed_films")
    fun getCashedFilms(): LiveData<List<Film>>

    //TODO How to get their LiveData List without null
    @Query("SELECT * FROM cashed_films")
    fun getCashedFilmsToList(): List<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Film>)

    @Delete
    fun deleteFilms(films: List<Film>): Int
}