package xyz.flussikatz.searchmovie

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import xyz.flussigkatz.remote_module.di.DaggerRemoteComponent
import xyz.flussikatz.searchmovie.data.preferences.PreferenceProvider
import xyz.flussikatz.searchmovie.di.AppComponent
import xyz.flussikatz.searchmovie.di.DaggerAppComponent
import xyz.flussikatz.searchmovie.di.modules.DatabaseModule
import xyz.flussikatz.searchmovie.di.modules.DomainModule
import javax.inject.Inject

class App : Application() {
    @Inject
    private lateinit var preferences: PreferenceProvider

    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        dagger = DaggerAppComponent.builder()
            .remoteProvider(DaggerRemoteComponent.create())
            .databaseModule(DatabaseModule())
            .domainModule(DomainModule(this))
            .build()

        dagger.inject(this)

        initTheme(preferences.getDefaultTheme())
    }

    fun initTheme(theme: Int) {
        AppCompatDelegate.setDefaultNightMode(theme)
    }

    companion object {
        lateinit var instance: App
        private set
    }
}