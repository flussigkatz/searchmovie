package xyz.flussikatz.searchmovie.data

import io.reactivex.rxjava3.core.Observable
import xyz.flussikatz.searchmovie.data.dao.FilmDao
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.data.entity.MarkedFilm


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
    fun getAllMarkedFilmsFromDB(): Observable<List<MarkedFilm>>{
        return filmDao.getCashedMarkedFilms()
    }

    fun clearDB(): Int {
        val films = filmDao.getCashedFilmsToList()
        return filmDao.deleteFilms(films)
    }

    /*fun deleteMarkedFilmFromDB(): Int {

    }*/
}