package xyz.flussikatz.searchmovie.data

import androidx.lifecycle.LiveData
import xyz.flussikatz.searchmovie.data.dao.FilmDao
import xyz.flussikatz.searchmovie.data.entity.Film
import java.util.concurrent.Executors


class MainRepository(private val filmDao: FilmDao) {

    fun putToDB(films: List<Film>) {
        Executors.newSingleThreadExecutor().execute{
            filmDao.insertAll(films)
        }
    }

    fun getAllFromDB(): LiveData<List<Film>>{
        return filmDao.getCashedFims()
    }

    fun clearDB(): Int {
        val films = filmDao.getCashedFimsForDelete()
        val count = filmDao.deleteFilms(films)
        return count
    }
}