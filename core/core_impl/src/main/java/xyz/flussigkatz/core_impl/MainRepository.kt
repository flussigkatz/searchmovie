package xyz.flussigkatz.core_impl

import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.db.FilmDao
import xyz.flussigkatz.core_api.entity.Film


class MainRepository(private val filmDao: FilmDao) {


    fun putToDB(films: List<Film>) {
        filmDao.insertAll(films)
    }

    fun getAllFromDB(): Observable<List<Film>>{
        return filmDao.getCashedFilms()
    }

    fun clearDB(): Int {
        val films = filmDao.getCashedFilmsToList()
        return filmDao.deleteFilms(films)
    }
}