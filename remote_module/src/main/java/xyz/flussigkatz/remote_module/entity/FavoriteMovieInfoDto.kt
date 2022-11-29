package xyz.flussigkatz.remote_module.entity


import com.google.gson.annotations.SerializedName

data class FavoriteMovieInfoDto(
    @SerializedName("media_id")
    val mediaId: Int
)