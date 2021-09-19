package xyz.flussikatz.searchmovie.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import xyz.flussikatz.searchmovie.data.MainRepository
import xyz.flussikatz.searchmovie.data.db.DatabaseHelper
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabaseHelper(context: Context) = DatabaseHelper(context)

    @Provides
    @Singleton
    fun provideRepository(databaseHelper: DatabaseHelper) = MainRepository(databaseHelper)
}