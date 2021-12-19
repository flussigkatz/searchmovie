package xyz.flussigkatz.searchmovie.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.flussigkatz.searchmovie.data.dao.FilmDao
import xyz.flussigkatz.searchmovie.data.entity.Film
import xyz.flussigkatz.searchmovie.data.entity.MarkedFilm

@Database(entities = [Film::class, MarkedFilm::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract  fun filmsDao(): FilmDao
}