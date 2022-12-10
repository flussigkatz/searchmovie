package xyz.flussigkatz.searchmovie.data

import xyz.flussigkatz.core_api.db.FilmDao
import xyz.flussigkatz.core_api.entity.*

class MainRepository(private val filmDao: FilmDao) {
    //region Film
    fun putSearchedFilmsToDB(films: List<Film>) {
        filmDao.insertAllSearchedFilms(films)
    }

    fun getAllSearchedFilmsFromDB() = filmDao.getCashedSearchedFilms()

    fun clearCashedSearchedFilmsDB() = filmDao.deleteSearchedFilms(filmDao.getCashedSearchedFilmsToList())
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

    //region PopularFilm
    fun putPopularFilmsToDB(films: List<PopularFilm>) {
        filmDao.insertAllPopularFilms(films)
    }

    fun getAllPopularFilmsFromDB() = filmDao.getCashedPopularFilms()

    fun clearCashedPopularFilmsDB() =
        filmDao.deletePopularFilms(filmDao.getCashedPopularFilmsToList())
    //endregion

    //region TopRatedFilm
    fun putTopRatedFilmsToDB(films: List<TopRatedFilm>) {
        filmDao.insertAllTopRatedFilms(films)
    }

    fun getAllTopRatedFilmsFromDB() = filmDao.getCashedTopRatedFilms()

    fun clearCashedTopRatedFilmsDB() =
        filmDao.deleteTopRatedFilms(filmDao.getCashedTopRatedFilmsToList())
    //endregion

    //region UpcomingFilm
    fun putUpcomingFilmsToDB(films: List<UpcomingFilm>) {
        filmDao.insertAllUpcomingFilms(films)
    }

    fun getAllUpcomingFilmsFromDB() = filmDao.getCashedUpcomingFilms()

    fun clearCashedUpcomingFilmsDB() =
        filmDao.deleteUpcomingFilms(filmDao.getCashedUpcomingFilmsToList())
    //endregion

    //region NowPlayingFilm
    fun putNowPlayingFilmsToDB(films: List<NowPlayingFilm>) {
        filmDao.insertAllNowPlayingFilms(films)
    }

    fun getAllNowPlayingFilmsFromDB() = filmDao.getCashedNowPlayingFilms()

    fun clearCashedNowPlayingFilmsDB() =
        filmDao.deleteNowPlayingFilms(filmDao.getCashedNowPlayingFilmsToList())
    //endregion
}