package xyz.flussigkatz.searchmovie.util

import xyz.flussigkatz.core_api.entity.Film
import xyz.flussigkatz.core_api.entity.MarkedFilm
import xyz.flussigkatz.remote_module.entity.FavoriteMovieListDto.FavoriteListItem
import xyz.flussigkatz.remote_module.entity.TmdbResultDto.TmdbFilm

object Converter {
    fun convertToFilmFromApi(list: List<TmdbFilm>?): List<Film> {
        val result = mutableListOf<Film>()
        list?.forEach {
            result.add(
                Film(
                    id = it.id,
                    title = it.title,
                    posterId = it.posterPath ?: "",
                    description = it.overview,
                    rating = (it.voteAverage * 10).toInt(),
                    fav_state = false
                )
            )
        }
        return result
    }

    fun convertToFilm(list: List<MarkedFilm>): List<Film> {
        val result = mutableListOf<Film>()
        list.forEach {
            result.add(
                Film(
                    id = it.id,
                    title = it.title,
                    posterId = it.posterId,
                    description = it.description,
                    rating = it.rating,
                    fav_state = it.fav_state
                )
            )
        }
        return result
    }

    fun convertToMarkedFilmFromApi(list: List<FavoriteListItem>?): List<MarkedFilm> {
        val result = mutableListOf<MarkedFilm>()
        list?.forEach {
            result.add(
                MarkedFilm(
                    id = it.id,
                    title = it.title ?: it.name,
                    posterId = it.posterPath ?: "",
                    description = it.overview,
                    rating = (it.voteAverage * 10).toInt(),
                    fav_state = true
                )
            )
        }
        return result
    }
}