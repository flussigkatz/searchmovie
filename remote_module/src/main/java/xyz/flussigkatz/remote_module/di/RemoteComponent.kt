package xyz.flussigkatz.remote_module.di

import dagger.Component
import xyz.flussigkatz.remote_module.RemoteProvider

@RemoteComponentScope
@Component(modules = [RemoteModule::class])
interface RemoteComponent: RemoteProvider {
    @Component.Factory
    interface Factory {
        fun create(): RemoteComponent
    }
}