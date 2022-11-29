package xyz.flussigkatz.remote_module.entity


import com.google.gson.annotations.SerializedName

data class MarkFimStatusDto(
    @SerializedName("id")
    val id: Any,
    @SerializedName("item_present")
    val itemPresent: Boolean
)