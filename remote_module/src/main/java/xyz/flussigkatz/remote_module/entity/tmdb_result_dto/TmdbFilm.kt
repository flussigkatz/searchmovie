package xyz.flussigkatz.remote_module.entity.tmdb_result_dto


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import xyz.flussigkatz.core_api.entity.IFilm

@Parcelize
data class TmdbFilm(
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    @SerializedName("id")
    val idApi: Int,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("title")
    val titleApi: String,
    @SerializedName("video")
    val video: Boolean,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
) : IFilm {
    override val localId: Int get() = 0
    override val id: Int get() = idApi
    override val title: String get() = titleApi
    override val posterId: String get() = posterPath.orEmpty()
    override val description: String get() = overview
    override val rating: Int get() = (voteAverage * 10).toInt()
    @IgnoredOnParcel
    override var favState = false
}