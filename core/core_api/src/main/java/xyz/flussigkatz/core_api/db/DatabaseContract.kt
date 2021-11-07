package xyz.flussigkatz.core_api.db

interface DatabaseContract {
    fun filmsDao(): FilmDao
}