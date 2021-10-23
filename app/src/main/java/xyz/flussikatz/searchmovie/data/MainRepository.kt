package xyz.flussikatz.searchmovie.data

import kotlinx.coroutines.flow.Flow
import xyz.flussikatz.searchmovie.data.dao.FilmDao
import xyz.flussikatz.searchmovie.data.entity.Film


class MainRepository(private val filmDao: FilmDao) {


    fun putToDB(films: List<Film>) {
            filmDao.insertAll(films)
    }

    fun getAllFromDB(): Flow<List<Film>>{
        return filmDao.getCashedFilms()
    }

    fun clearDB(): Int {
        val films = filmDao.getCashedFilmsToList()
        return filmDao.deleteFilms(films)
    }
}