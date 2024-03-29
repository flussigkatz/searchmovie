package xyz.flussigkatz.core_impl

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import xyz.flussigkatz.core_api.db.DatabaseContract
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideFilmDatabase(context: Context): DatabaseContract =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun provideFilmsDao(databaseContract: DatabaseContract)  = databaseContract.filmsDao()

    companion object {
        private const val DATABASE_NAME = "film_db"
    }
}