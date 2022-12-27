package xyz.flussigkatz.searchmovie.di.modules

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import dagger.Module
import dagger.Provides
import xyz.flussigkatz.core_api.db.FilmDao
import xyz.flussigkatz.remote_module.TmdbApi
import xyz.flussigkatz.searchmovie.data.FilmRemoteMediator
import xyz.flussigkatz.searchmovie.data.MainRepository
import xyz.flussigkatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Singleton

@Module
@ExperimentalPagingApi
class DomainModule {

    @Singleton
    @Provides
    fun providePreferences(context: Context) = PreferenceProvider(context)

    @Singleton
    @Provides
    fun provideRepository(
        filmDao: FilmDao,
        remoteMediatorFactory: FilmRemoteMediator.Factory
    ) = MainRepository(filmDao, remoteMediatorFactory)

    @Provides
    @Singleton
    fun provideInteractor(
        repository: MainRepository,
        tmdbApi: TmdbApi,
        preferenceProvider: PreferenceProvider,
    ) = Interactor(
        repository = repository,
        retrofitService = tmdbApi,
        preferences = preferenceProvider,
    )
}