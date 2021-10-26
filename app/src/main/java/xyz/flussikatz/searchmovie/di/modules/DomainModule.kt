package xyz.flussikatz.searchmovie.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
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
    fun provideBehaviorSubjectRefreshState() = BehaviorSubject.create<Boolean>()

    @Provides
    @Singleton
    fun providePublishSubjectEventMessage() = PublishSubject.create<String>()

    @Provides
    @Singleton
    fun provideInteractor(
        repository: MainRepository,
        tmdbApi: TmdbApi,
        preferenceProvider: PreferenceProvider,
        scope: CoroutineScope,
        refreshState: BehaviorSubject<Boolean>,
        eventMessage: PublishSubject<String>
    ) = Interactor(
        repo = repository,
        retrofitService = tmdbApi,
        preferences = preferenceProvider,
        scope = scope,
        refreshState = refreshState,
        eventMessage = eventMessage

    )
}