package xyz.flussikatz.searchmovie.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import xyz.flussikatz.searchmovie.data.dao.FilmDao
import xyz.flussikatz.searchmovie.data.entity.Film
import java.util.concurrent.Executors


class MainRepository(private val filmDao: FilmDao) {


    fun putToDB(films: List<Film>) {
            filmDao.insertAll(films)
    }

    fun getAllFromDB(): LiveData<List<Film>>{
        return filmDao.getCashedFilms()
    }

    fun clearDB(): Int {
        val films = filmDao.getCashedFilmsToList()
        return filmDao.deleteFilms(films)
    }
}