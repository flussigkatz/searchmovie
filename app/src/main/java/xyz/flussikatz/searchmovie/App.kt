package xyz.flussikatz.searchmovie

import android.app.Application
import xyz.flussigkatz.remote_module.di.DaggerRemoteComponent
import xyz.flussikatz.searchmovie.di.AppComponent
import xyz.flussikatz.searchmovie.di.DaggerAppComponent
import xyz.flussikatz.searchmovie.di.modules.DatabaseModule
import xyz.flussikatz.searchmovie.di.modules.DomainModule

class App : Application() {

    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        dagger = DaggerAppComponent.builder()
            .remoteProvider(DaggerRemoteComponent.create())
            .databaseModule(DatabaseModule())
            .domainModule(DomainModule(this))
            .build()
    }

    companion object {
        lateinit var instance: App
        private set
    }
}