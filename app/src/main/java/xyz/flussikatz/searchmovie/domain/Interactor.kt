package xyz.flussikatz.searchmovie.domain

import xyz.flussikatz.searchmovie.data.MainRepository

class Interactor(val repo: MainRepository) {
    fun getFilmsDB(): List<Film> = repo.filmsDataBase
}