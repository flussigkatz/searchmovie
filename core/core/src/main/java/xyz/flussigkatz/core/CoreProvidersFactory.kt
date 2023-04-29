package xyz.flussigkatz.core

import xyz.flussigkatz.core_api.ContextProvider
import xyz.flussigkatz.core_api.db.DatabaseProvider
import xyz.flussigkatz.core_impl.DaggerDatabaseComponent

object CoreProvidersFactory {
    fun createDatabaseBuilder(contextProvider: ContextProvider): DatabaseProvider {
        return DaggerDatabaseComponent.factory().create(contextProvider)
    }
}