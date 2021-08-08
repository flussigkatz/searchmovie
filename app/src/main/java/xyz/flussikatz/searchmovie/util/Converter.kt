package xyz.flussikatz.searchmovie.util

import xyz.flussikatz.searchmovie.data.Entity.TmdbFilm
import xyz.flussikatz.searchmovie.domain.Film

object Converter {
    fun convertApiListToDtoList(list: List<TmdbFilm>?): List<Film> {
        val result = mutableListOf<Film>()
        list?.forEach {
            result.add(
                Film(
                    id = it.id,
                    title = it.title,
                    posterId = it.posterPath,
                    description = it.overview,
                    rating = (it.voteAverage * 10).toInt()
                    )
            )
        }

        return result
    }
}