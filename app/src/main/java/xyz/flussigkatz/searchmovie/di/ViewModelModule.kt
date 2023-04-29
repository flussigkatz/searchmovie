package xyz.flussigkatz.searchmovie.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import xyz.flussigkatz.searchmovie.viewmodel.*

@Module
interface ViewModelModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(HomeFragmentViewModel::class)
    fun homeFragmentViewModel(viewModel: HomeFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryFragmentViewModel::class)
    fun historyFragmentViewModel(viewModel: HistoryFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailsFragmentViewModel::class)
    fun detailsFragmentViewModel(viewModel: DetailsFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    fun mainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MarkedFragmentViewModel::class)
    fun markedFragmentViewModel(viewModel: MarkedFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NowPlayingFilmsFragmentViewModel::class)
    fun nowPlayingFilmsFragmentViewModel(viewModel: NowPlayingFilmsFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PopularFilmsFragmentViewModel::class)
    fun popularFilmsFragmentViewModel(viewModel: PopularFilmsFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsFragmentViewModel::class)
    fun settingsFragmentViewModel(viewModel: SettingsFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopRatedFilmsFragmentViewModel::class)
    fun topRatedFilmsFragmentViewModel(viewModel: TopRatedFilmsFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UpcomingFilmsFragmentViewModel::class)
    fun upcomingFilmsFragmentViewModel(viewModel: UpcomingFilmsFragmentViewModel): ViewModel
}