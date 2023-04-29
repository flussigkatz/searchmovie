package xyz.flussigkatz.core_impl

import dagger.Component
import xyz.flussigkatz.core_api.ContextProvider
import xyz.flussigkatz.core_api.db.DatabaseProvider

@DatabaseComponentScope
@Component(
    dependencies = [ContextProvider::class],
    modules = [DatabaseModule::class]
)
interface DatabaseComponent: DatabaseProvider {
    @Component.Factory
    interface Factory {
        fun create(contextProvider: ContextProvider): DatabaseComponent
    }
}