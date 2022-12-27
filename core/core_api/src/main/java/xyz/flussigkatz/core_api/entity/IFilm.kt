package xyz.flussigkatz.core_api.entity

import android.os.Parcelable

interface IFilm : Parcelable {
    val localId: Int
    val id: Int
    val title: String
    val posterId: String
    val description: String
    val rating: Int
    var favState: Boolean
}