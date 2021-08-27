package xyz.flussikatz.searchmovie

import android.app.Application
import xyz.flussikatz.searchmovie.di.AppComponent
import xyz.flussikatz.searchmovie.di.DaggerAppComponent

class App : Application() {

    lateinit var dagger: AppComponent
    
    override fun onCreate() {
        super.onCreate()
        instance = this

        dagger = DaggerAppComponent.create()



    }
    companion object {
        lateinit var instance: App
        private set
    }
}