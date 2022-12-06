package xyz.flussigkatz.remote_module.entity.film_lists_dto


import com.google.gson.annotations.SerializedName

data class FilmListsDto(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)