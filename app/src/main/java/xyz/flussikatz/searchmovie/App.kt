package xyz.flussikatz.searchmovie

import android.app.Application
import xyz.flussikatz.searchmovie.di.AppComponent
import xyz.flussikatz.searchmovie.di.DaggerAppComponent
import xyz.flussikatz.searchmovie.di.modules.DatabaseModule
import xyz.flussikatz.searchmovie.di.modules.DomainModule
import xyz.flussikatz.searchmovie.di.modules.RemoteModule
import xyz.flussikatz.searchmovie.domain.Interactor
import javax.inject.Inject

class App : Application() {

    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        dagger = DaggerAppComponent.builder()
            .remoteModule(RemoteModule())
            .databaseModule(DatabaseModule())
            .domainModule(DomainModule(this))
            .build()
    }

    companion object {
        lateinit var instance: App
        private set
    }
}