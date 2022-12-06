package xyz.flussigkatz.searchmovie.data

import xyz.flussigkatz.core_api.db.FilmDao
import xyz.flussigkatz.core_api.entity.Film
import xyz.flussigkatz.core_api.entity.MarkedFilm
import xyz.flussigkatz.core_api.entity.BrowsingFilm

class MainRepository(private val filmDao: FilmDao) {
    //region Film
    fun putFilmsToDB(films: List<Film>) {
        filmDao.insertAllFilms(films)
    }

    fun getAllFilmsFromDB() = filmDao.getCashedFilms()

    fun clearCashedFilmsDB() = filmDao.deleteFilms(filmDao.getCashedFilmsToList())
    //endregion

    //region MarkedFilm
    fun putMarkedFilmToDB(films: List<MarkedFilm>) {
        filmDao.insertAllMarkedFilms(films)
    }

    fun getSearchedMarkedFilms(query: String) = filmDao.getSearchedMarkedFilm(query)

    fun getAllMarkedFilmsFromDB() = filmDao.getCashedMarkedFilms()

    fun getIdsMarkedFilmsToListFromDB() = filmDao.getIdsMarkedFilmsToList()

    fun clearMarkedFilmsDB() = filmDao.deleteMarkedFilms(filmDao.getCashedMarkedFilmsToList())
    //endregion

    //region BrowsingFilm
    fun putBrowsingFilmToDB(film: BrowsingFilm) {
        filmDao.insertBrowsingFilm(film)
    }

    fun getAllBrowsingFilmsFromDB() = filmDao.getCashedBrowsingFilms()
    //endregion
}