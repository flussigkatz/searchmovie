package xyz.flussikatz.searchmovie.di

import dagger.Component
import xyz.flussikatz.searchmovie.di.modules.DatabaseModule
import xyz.flussikatz.searchmovie.di.modules.DomainModule
import xyz.flussikatz.searchmovie.di.modules.RemoteModule
import xyz.flussikatz.searchmovie.viewmodel.HomeFragmentViewModel
import xyz.flussikatz.searchmovie.viewmodel.SettingsFragmentViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [RemoteModule::class, DatabaseModule::class, DomainModule::class])

interface AppComponent {
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)

    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
}