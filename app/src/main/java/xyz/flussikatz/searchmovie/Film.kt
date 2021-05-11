package xyz.flussikatz.searchmovie

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Film (
    val id: Int,
    val title: String,
    val poster: Int,
    val description: String,
    var fav_state: Boolean
) : Parcelable