package xyz.flussikatz.searchmovie.di.modules

import dagger.Module
import dagger.Provides
import xyz.flussikatz.searchmovie.data.MainRepository
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideRepository() = MainRepository()
}