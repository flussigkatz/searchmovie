package xyz.flussigkatz.core_impl

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.flussigkatz.core_api.db.DatabaseContract
import xyz.flussigkatz.core_api.entity.*

@Database(
    entities = [
        SearchedFilm::class,
        PopularFilm::class,
        TopRatedFilm::class,
        UpcomingFilm::class,
        NowPlayingFilm::class,
        MarkedFilm::class,
        BrowsingFilm::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase(), DatabaseContract