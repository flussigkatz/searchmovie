package xyz.flussigkatz.searchmovie.di

import dagger.Component
import xyz.flussigkatz.remote_module.RemoteProvider
import xyz.flussigkatz.searchmovie.di.modules.DatabaseModule
import xyz.flussigkatz.searchmovie.di.modules.DomainModule
import xyz.flussigkatz.searchmovie.view.MainActivity
import xyz.flussigkatz.searchmovie.viewmodel.DetailsFragmentViewModel
import xyz.flussigkatz.searchmovie.viewmodel.HomeFragmentViewModel
import xyz.flussigkatz.searchmovie.viewmodel.MarkedFragmentViewModel
import xyz.flussigkatz.searchmovie.viewmodel.SettingsFragmentViewModel
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [RemoteProvider::class],
    modules = [DatabaseModule::class, DomainModule::class]
)

interface AppComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(homeFragmentViewModel: HomeFragmentViewModel)

    fun inject(markedFragmentViewModel: MarkedFragmentViewModel)

    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)

    fun inject(detailsFragmentViewModel: DetailsFragmentViewModel)
}