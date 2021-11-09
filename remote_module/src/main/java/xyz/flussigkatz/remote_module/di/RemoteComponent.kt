package xyz.flussigkatz.remote_module.di

import dagger.Component
import xyz.flussigkatz.remote_module.RemoteProvider
import javax.inject.Singleton

@Singleton
@Component(modules = [RemoteModule::class])
interface RemoteComponent: RemoteProvider