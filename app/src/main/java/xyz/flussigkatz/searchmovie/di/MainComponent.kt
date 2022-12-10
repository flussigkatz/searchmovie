package xyz.flussigkatz.searchmovie.di

import dagger.Component
import xyz.flussigkatz.core_api.AppProvider
import xyz.flussigkatz.core_api.db.DatabaseProvider
import xyz.flussigkatz.remote_module.RemoteProvider
import xyz.flussigkatz.searchmovie.di.modules.DomainModule
import xyz.flussigkatz.searchmovie.viewmodel.*
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [AppProvider::class, RemoteProvider::class, DatabaseProvider::class],
    modules = [DomainModule::class]
)

interface MainComponent {
    fun inject(mainActivityViewModel: MainActivityViewModel)
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
    fun inject(markedFragmentViewModel: MarkedFragmentViewModel)
    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
    fun inject(detailsFragmentViewModel: DetailsFragmentViewModel)
    fun inject(historyFragmentViewModel: HistoryFragmentViewModel)
    fun inject(popularFilmsFragmentViewModel: PopularFilmsFragmentViewModel)
    fun inject(topRatedFilmsFragmentViewModel: TopRatedFilmsFragmentViewModel)
    fun inject(upcomingFilmsFragmentViewModel: UpcomingFilmsFragmentViewModel)
    fun inject(nowPlayingFilmsFragmentViewModel: NowPlayingFilmsFragmentViewModel)
}