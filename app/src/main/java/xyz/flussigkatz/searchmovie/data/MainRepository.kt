package xyz.flussigkatz.searchmovie.data

import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.db.FilmDao
import xyz.flussigkatz.core_api.entity.Film
import xyz.flussigkatz.core_api.entity.MarkedFilm

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

    fun getSearchedMarkedFilms(query: String): Observable<List<MarkedFilm>>{
        return filmDao.getSearchedMarkedFilm(query)
    }

    fun getAllMarkedFilmsFromDB(): Observable<List<MarkedFilm>>{
        return filmDao.getCashedMarkedFilms()
    }

    fun clearCashedFilmsDB(): Int {
        val films = filmDao.getCashedFilmsToList()
        return filmDao.deleteFilms(films)
    }

    fun clearMarkedFilmsDB(): Int {
        val films = filmDao.getCashedMarkedFilmsToList()
        return filmDao.deleteMarkedFilms(films)
    }
}