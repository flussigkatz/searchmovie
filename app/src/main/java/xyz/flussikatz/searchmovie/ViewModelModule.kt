package xyz.flussikatz.searchmovie

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import xyz.flussikatz.searchmovie.data.TmdbApi
import xyz.flussikatz.searchmovie.domain.Interactor
import xyz.flussikatz.searchmovie.domain.MainInteractor
import xyz.flussikatz.searchmovie.domain.Remote

@Module
@InstallIn(FragmentComponent::class)
object ViewModelModule {
    @Provides
    fun provideRetrofit(): TmdbApi = Remote().retrofitService

    @Provides
    fun provideInteractor(retrofitService: TmdbApi): Interactor = MainInteractor(retrofitService)
}