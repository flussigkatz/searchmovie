package xyz.flussigkatz.searchmovie.di

import dagger.Component
import xyz.flussigkatz.core_api.ContextProvider
import xyz.flussigkatz.core_api.db.DatabaseProvider
import xyz.flussigkatz.remote_module.RemoteProvider
import xyz.flussigkatz.searchmovie.view.MainActivity
import xyz.flussigkatz.searchmovie.view.fragments.*

@AppScope
@Component(
    dependencies = [ContextProvider::class, RemoteProvider::class, DatabaseProvider::class],
    modules = [ViewModelModule::class]
)
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(homeFragment: HomeFragment)
    fun inject(markedFragment: MarkedFragment)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(detailsFragment: DetailsFragment)
    fun inject(historyFragment: HistoryFragment)
    fun inject(popularFilmsFragment: PopularFilmsFragment)
    fun inject(topRatedFilmsFragment: TopRatedFilmsFragment)
    fun inject(upcomingFilmsFragment: UpcomingFilmsFragment)
    fun inject(nowPlayingFilmsFragment: NowPlayingFilmsFragment)

    @Component.Factory
    interface Factory {
        fun create(
            contextProvider: ContextProvider,
            remoteProvider: RemoteProvider,
            databaseProvider: DatabaseProvider,
        ): AppComponent
    }
}