package xyz.flussikatz.searchmovie.util

import xyz.flussikatz.searchmovie.data.entity.TmdbFilm
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.data.entity.MarkedFilm

object Converter {
    fun convertToFilmFromApi(list: List<TmdbFilm>?): List<Film> {
        val result = mutableListOf<Film>()
        list?.forEach {
            result.add(
                Film(
                    id = it.id,
                    title = it.title,
                    posterId = it.posterPath,
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
                    rating = (it.rating * 10),
                    fav_state = it.fav_state
                )
            )
        }
        return result
    }

    fun convertToMarkedFilmFromApi(list: List<TmdbFilm>?): List<MarkedFilm> {
        val result = mutableListOf<MarkedFilm>()
        list?.forEach {
            result.add(
                MarkedFilm(
                    id = it.id,
                    title = it.title,
                    posterId = it.posterPath,
                    description = it.overview,
                    rating = (it.voteAverage * 10).toInt(),
                    fav_state = true
                )
            )
        }
        return result
    }
}