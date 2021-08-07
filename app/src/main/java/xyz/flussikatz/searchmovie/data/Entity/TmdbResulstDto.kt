package xyz.flussikatz.searchmovie.data.Entity


import com.google.gson.annotations.SerializedName

data class TmdbResulstDto(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val tmdbFilms: List<TmdbFilm>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)