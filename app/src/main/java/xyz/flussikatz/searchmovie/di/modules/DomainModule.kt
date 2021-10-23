package xyz.flussikatz.searchmovie.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import xyz.flussikatz.searchmovie.data.MainRepository
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.data.TmdbApi
import xyz.flussikatz.searchmovie.domain.Interactor
import javax.inject.Singleton

@Module
class DomainModule(val context: Context) {

    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    fun providePreferences(context: Context) = PreferenceProvider(context)

    @Provides
    @Singleton
    fun provideScope() = CoroutineScope(Dispatchers.IO)

    @Provides
    @Singleton
    fun provideChannelRefreshState() = Channel<Boolean>(Channel.CONFLATED)

    @Provides
    @Singleton
    fun provideChannelEventMessage() = Channel<String>(Channel.BUFFERED)

    @Provides
    @Singleton
    fun provideInteractor(
        repository: MainRepository,
        tmdbApi: TmdbApi,
        preferenceProvider: PreferenceProvider,
        scope: CoroutineScope,
        channelRefreshState: Channel<Boolean>,
        channelEventMessage: Channel<String>
    ) = Interactor(
        repo = repository,
        retrofitService = tmdbApi,
        preferences = preferenceProvider,
        scope = scope,
        channelRefreshState = channelRefreshState,
        channelEventMessage = channelEventMessage

    )
}