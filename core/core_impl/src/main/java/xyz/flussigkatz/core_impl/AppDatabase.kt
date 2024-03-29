package xyz.flussigkatz.core_impl

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.flussigkatz.core_api.db.DatabaseContract
import xyz.flussigkatz.core_api.entity.Film
import xyz.flussigkatz.core_api.entity.MarkedFilm
import xyz.flussigkatz.core_api.entity.BrowsingFilm

@Database(entities = [Film::class, MarkedFilm::class, BrowsingFilm::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase(), DatabaseContract