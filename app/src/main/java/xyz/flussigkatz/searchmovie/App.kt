package xyz.flussigkatz.searchmovie

import android.app.Application
import timber.log.Timber
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
        dagger = initDagger()
        initTimber()
        NotificationHelper.initNotification(this)
    }

    private fun initDagger() = DaggerMainComponent.builder()
        .appProvider(AppComponent.create(this))
        .remoteProvider(DaggerRemoteComponent.create())
        .databaseProvider(CoreProvidersFactory.createDatabaseBuilder(AppComponent.create(this)))
        .domainModule(DomainModule())
        .build()

    private fun initTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var instance: App
            private set
    }
}