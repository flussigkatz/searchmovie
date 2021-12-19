package xyz.flussigkatz.searchmovie.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import xyz.flussigkatz.searchmovie.data.MainRepository
import xyz.flussigkatz.searchmovie.data.dao.FilmDao
import xyz.flussigkatz.searchmovie.data.db.AppDatabase
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
