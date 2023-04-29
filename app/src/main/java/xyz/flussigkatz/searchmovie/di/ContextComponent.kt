package xyz.flussigkatz.searchmovie.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import xyz.flussigkatz.core_api.ContextProvider

@AppScope
@Component
interface ContextComponent : ContextProvider {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ContextComponent
    }
}