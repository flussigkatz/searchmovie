package xyz.flussikatz.searchmovie

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import xyz.flussigkatz.remote_module.di.DaggerRemoteComponent
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.di.AppComponent
import xyz.flussikatz.searchmovie.di.DaggerAppComponent
import xyz.flussikatz.searchmovie.di.modules.DatabaseModule
import xyz.flussikatz.searchmovie.di.modules.DomainModule
import xyz.flussikatz.searchmovie.view.notification.NotificationConstants
import xyz.flussikatz.searchmovie.view.notification.NotificationHelper
import javax.inject.Inject

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
        AppCompatDelegate.setDefaultNightMode(theme)
    }

    companion object {
        lateinit var instance: App
        private set
    }
}