package xyz.flussigkatz.core_impl

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import xyz.flussigkatz.core_api.db.FilmDao
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideFilmDao(context: Context) =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "film_db"
        ).build().filmsDao()

    @Singleton
    @Provides
    fun provideRepository(filmDao: FilmDao) = MainRepository(filmDao)
}