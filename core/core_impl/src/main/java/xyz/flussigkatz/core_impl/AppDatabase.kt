package xyz.flussigkatz.core_impl

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.flussigkatz.core_api.db.FilmDao
import xyz.flussigkatz.core_api.entity.Film

@Database(entities = [Film::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract  fun filmsDao(): FilmDao
}