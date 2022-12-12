package xyz.flussigkatz.searchmovie.util

import xyz.flussigkatz.core_api.entity.*
import xyz.flussigkatz.remote_module.entity.tmdb_result_dto.TmdbFilm

object Converter {
    fun convertToFilmFromApi(
        list: List<TmdbFilm>,
        listIdsMarkedFilms: List<Int>
    ) = list.map {
        Film(
            id = it.id,
            title = it.title,
            posterId = it.posterPath ?: "",
            description = it.overview,
            rating = (it.voteAverage * 10).toInt(),
            fav_state = listIdsMarkedFilms.contains(it.id)
        )
    }

    fun convertToPopularFilmFromApi(
        list: List<TmdbFilm>,
        listIdsMarkedFilms: List<Int>
    ) = list.map {
        PopularFilm(
            id = it.id,
            title = it.title,
            posterId = it.posterPath ?: "",
            description = it.overview,
            rating = (it.voteAverage * 10).toInt(),
            fav_state = listIdsMarkedFilms.contains(it.id)
        )
    }

    fun convertToTopRatedFilmFromApi(
        list: List<TmdbFilm>,
        listIdsMarkedFilms: List<Int>
    ) = list.map {
        TopRatedFilm(
            id = it.id,
            title = it.title,
            posterId = it.posterPath ?: "",
            description = it.overview,
            rating = (it.voteAverage * 10).toInt(),
            fav_state = listIdsMarkedFilms.contains(it.id)
        )
    }

    fun convertToUpcomingFilmFromApi(
        list: List<TmdbFilm>,
        listIdsMarkedFilms: List<Int>
    ) = list.map {
        UpcomingFilm(
            id = it.id,
            title = it.title,
            posterId = it.posterPath ?: "",
            description = it.overview,
            rating = (it.voteAverage * 10).toInt(),
            fav_state = listIdsMarkedFilms.contains(it.id)
        )
    }

    fun convertToNowPlayingFilmFromApi(
        list: List<TmdbFilm>,
        listIdsMarkedFilms: List<Int>
    ) = list.map {
        NowPlayingFilm(
            id = it.id,
            title = it.title,
            posterId = it.posterPath ?: "",
            description = it.overview,
            rating = (it.voteAverage * 10).toInt(),
            fav_state = listIdsMarkedFilms.contains(it.id)
        )
    }
}