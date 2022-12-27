package xyz.flussigkatz.searchmovie.data.model

import kotlinx.parcelize.Parcelize
import xyz.flussigkatz.core_api.entity.IFilm

@Parcelize
data class FilmUiModel(
    override val localId: Int,
    override val id: Int,
    override val title: String,
    override val posterId: String,
    override val description: String,
    override var rating: Int,
    override var favState: Boolean,
) : IFilm {
    constructor(film: IFilm) : this(
        localId = film.localId,
        id = film.id,
        title = film.title,
        posterId = film.posterId,
        description = film.description,
        rating = film.rating,
        favState = film.favState
    )
}