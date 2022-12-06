package xyz.flussigkatz.remote_module.entity


import com.google.gson.annotations.SerializedName

data class CreateFavoriteListResponseDto(
    @SerializedName("list_id")
    val listId: Int,
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("status_message")
    val statusMessage: String,
    @SerializedName("success")
    val success: Boolean
)