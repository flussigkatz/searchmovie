package xyz.flussigkatz.remote_module.entity


import com.google.gson.annotations.SerializedName

data class ListInfo(
    @SerializedName("description")
    val description: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("name")
    val name: String
)