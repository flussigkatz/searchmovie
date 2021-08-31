package xyz.flussikatz.searchmovie.di.modules

import dagger.Module
import dagger.Provides
import xyz.flussikatz.searchmovie.data.MainRepository
import xyz.flussikatz.searchmovie.data.TmdbApi
import xyz.flussikatz.searchmovie.domain.Interactor
import javax.inject.Singleton

@Module
class DomainModule {
    @Provides
    @Singleton
    fun provideInteractor(repository: MainRepository, tmdbApi: TmdbApi) =
        Interactor(repo = repository, retrofitService = tmdbApi)
}