package xyz.flussigkatz.searchmovie.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.core_api.db.FilmDao
import xyz.flussigkatz.remote_module.TmdbApi
import xyz.flussigkatz.searchmovie.data.MainRepository
import xyz.flussigkatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussigkatz.searchmovie.domain.Interactor
import javax.inject.Singleton

@Module
class DomainModule {

    @Singleton
    @Provides
    fun providePreferences(context: Context) = PreferenceProvider(context)

    @Singleton
    @Provides
    fun provideRepository(filmDao: FilmDao) = MainRepository(filmDao)

    @Provides
    @Singleton
    fun provideBehaviorSubjectRefreshState(): BehaviorSubject<Boolean> = BehaviorSubject.create()

    @Provides
    @Singleton
    fun providePublishSubjectEventMessage(): PublishSubject<String> = PublishSubject.create()

    @Provides
    @Singleton
    fun provideInteractor(
        repository: MainRepository,
        tmdbApi: TmdbApi,
        preferenceProvider: PreferenceProvider,
        refreshState: BehaviorSubject<Boolean>,
        eventMessage: PublishSubject<String>
    ) = Interactor(
        repository = repository,
        retrofitService = tmdbApi,
        preferences = preferenceProvider,
        refreshState = refreshState,
        eventMessage = eventMessage

    )
}