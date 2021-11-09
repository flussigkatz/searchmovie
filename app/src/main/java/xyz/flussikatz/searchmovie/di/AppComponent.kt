package xyz.flussikatz.searchmovie.di

import dagger.Component
import xyz.flussigkatz.remote_module.RemoteProvider
import xyz.flussikatz.searchmovie.di.modules.DatabaseModule
import xyz.flussikatz.searchmovie.di.modules.DomainModule
import xyz.flussikatz.searchmovie.view.MainActivity
import xyz.flussikatz.searchmovie.viewmodel.DetailsFragmentViewModel
import xyz.flussikatz.searchmovie.viewmodel.HomeFragmentViewModel
import xyz.flussikatz.searchmovie.viewmodel.SettingsFragmentViewModel
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [RemoteProvider::class],
    modules = [DatabaseModule::class, DomainModule::class]
)

interface AppComponent {
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)

    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)

    fun inject(detailsFragmentViewModel: DetailsFragmentViewModel)

    fun inject(mainActivity: MainActivity)
}