package xyz.flussikatz.searchmovie.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.flussikatz.searchmovie.data.dao.FilmDao
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.data.entity.MarkedFilm

@Database(entities = [Film::class, MarkedFilm::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract  fun filmsDao(): FilmDao
}