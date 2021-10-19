package xyz.flussikatz.searchmovie.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import xyz.flussikatz.searchmovie.data.MainRepository
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.data.TmdbApi
import xyz.flussikatz.searchmovie.domain.Interactor
import javax.inject.Singleton
import kotlin.coroutines.EmptyCoroutineContext

@Module
class DomainModule(val context: Context) {

    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    fun providePreferences(context: Context) = PreferenceProvider(context)

    @Provides
    @Singleton
    fun provideCoroutinesScope() = CoroutineScope(EmptyCoroutineContext)

    @Provides
    @Singleton
    fun provideInteractor(
        repository: MainRepository,
        tmdbApi: TmdbApi,
        preferenceProvider: PreferenceProvider,
        coroutinesScope: CoroutineScope
    ) = Interactor(
        repo = repository,
        retrofitService = tmdbApi,
        preferences = preferenceProvider,
        coroutinesScope = coroutinesScope
    )
}