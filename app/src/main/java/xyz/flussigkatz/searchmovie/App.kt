package xyz.flussigkatz.searchmovie

import android.app.Application
import xyz.flussigkatz.remote_module.di.DaggerRemoteComponent
import xyz.flussigkatz.searchmovie.di.AppComponent
import xyz.flussigkatz.searchmovie.di.DaggerAppComponent
import xyz.flussigkatz.searchmovie.di.modules.DatabaseModule
import xyz.flussigkatz.searchmovie.di.modules.DomainModule
import xyz.flussigkatz.searchmovie.view.notification.NotificationHelper

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

        NotificationHelper.initNotification(this)

    }

    fun initTheme(theme: Int) {
        //TODO: create init theme
    }

    companion object {
        lateinit var instance: App
        private set
    }
}