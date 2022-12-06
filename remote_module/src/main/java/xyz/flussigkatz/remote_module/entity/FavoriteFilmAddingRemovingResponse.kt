package xyz.flussigkatz.remote_module.entity


import com.google.gson.annotations.SerializedName

data class FavoriteFilmAddingRemovingResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("status_message")
    val statusMessage: String,
    @SerializedName("success")
    val success: Boolean
)