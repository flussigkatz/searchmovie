package xyz.flussigkatz.remote_module

interface RemoteProvider {
    fun provideRemote(): TmdbApi
}