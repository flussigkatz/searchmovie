package xyz.flussigkatz.searchmovie

import android.app.Application
import timber.log.Timber
import xyz.flussigkatz.core.CoreProvidersFactory
import xyz.flussigkatz.core_api.ContextProvider
import xyz.flussigkatz.remote_module.di.DaggerRemoteComponent
import xyz.flussigkatz.searchmovie.di.AppComponent
import xyz.flussigkatz.searchmovie.di.DaggerAppComponent
import xyz.flussigkatz.searchmovie.di.DaggerContextComponent
import xyz.flussigkatz.searchmovie.view.notification.NotificationHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appComponent = initAppComponent(DaggerContextComponent.factory().create(this))
        initTimber()
        NotificationHelper.initNotification(this)
    }

    private fun initAppComponent(contextProvider: ContextProvider) =
        DaggerAppComponent.factory().create(
            contextProvider = contextProvider,
            remoteProvider = DaggerRemoteComponent.factory().create(),
            databaseProvider = CoreProvidersFactory.createDatabaseBuilder(contextProvider)
        )

    private fun initTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}