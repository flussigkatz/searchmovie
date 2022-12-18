package xyz.flussigkatz.searchmovie.data.model

import kotlinx.parcelize.Parcelize
import xyz.flussigkatz.core_api.entity.AbstractFilmEntity

@Parcelize
data class FilmUiModel(
    override val localId: Int,
    override val id: Int,
    override val title: String,
    override val posterId: String,
    override val description: String,
    override var rating: Int,
    override var fav_state: Boolean,
) : AbstractFilmEntity(
    localId = localId,
    id = id,
    title = title,
    posterId = posterId,
    description = description,
    rating = rating,
    fav_state = fav_state
)