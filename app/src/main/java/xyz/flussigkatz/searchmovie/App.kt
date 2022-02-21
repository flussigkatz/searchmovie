package xyz.flussigkatz.searchmovie

import android.app.Application
import xyz.flussigkatz.core.CoreProvidersFactory
import xyz.flussigkatz.remote_module.di.DaggerRemoteComponent
import xyz.flussigkatz.searchmovie.di.AppComponent
import xyz.flussigkatz.searchmovie.di.MainComponent
import xyz.flussigkatz.searchmovie.di.DaggerMainComponent
import xyz.flussigkatz.searchmovie.di.modules.DomainModule
import xyz.flussigkatz.searchmovie.view.notification.NotificationHelper

class App : Application() {

    lateinit var dagger: MainComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        initDagger()

        NotificationHelper.initNotification(this)

    }

    private fun initDagger() {
        dagger = DaggerMainComponent.builder()
            .appProvider(AppComponent.create(this))
            .remoteProvider(DaggerRemoteComponent.create())
            .databaseProvider(CoreProvidersFactory.createDatabaseBuilder(AppComponent.create(this)))
            .domainModule(DomainModule())
            .build()
    }


    companion object {
        lateinit var instance: App
            private set
    }
}