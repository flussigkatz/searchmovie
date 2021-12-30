package xyz.flussigkatz.searchmovie.data

import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.searchmovie.data.dao.FilmDao
import xyz.flussigkatz.searchmovie.data.entity.Film
import xyz.flussigkatz.searchmovie.data.entity.MarkedFilm


class MainRepository(private val filmDao: FilmDao) {


    fun putFilmToDB(films: List<Film>) {
            filmDao.insertAllFilms(films)
    }

    fun putMarkedFilmToDB(films: List<MarkedFilm>) {
            filmDao.insertAllMarkedFilms(films)
    }

    fun getAllFilmsFromDB(): Observable<List<Film>>{
        return filmDao.getCashedFilms()
    }


    fun getAllMarkedFilmsDBToList(): Observable<List<MarkedFilm>>?{
        val res = filmDao.getCashedMarkedFilmsToList()
        return if (res.isNotEmpty()) {
            Observable.just(filmDao.getCashedMarkedFilmsToList())
        } else {
            null
        }
    }

    fun getAllMarkedFilmsFromDB(): Observable<List<MarkedFilm>>{
        return filmDao.getCashedMarkedFilms()
    }

    fun clearDB(): Int {
        val films = filmDao.getCashedFilmsToList()
        return filmDao.deleteFilms(films)
    }

    fun deleteMarkedFilmFromDB(id: Int) {
        val film = filmDao.getCashedOneMarkedFilm(id)
        filmDao.deleteOneFilm(film)
    }
}