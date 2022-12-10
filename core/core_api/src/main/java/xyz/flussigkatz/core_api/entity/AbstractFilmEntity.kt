package xyz.flussigkatz.core_api.entity

import android.os.Parcelable


abstract class AbstractFilmEntity(
    open val id: Int,
    open val title: String,
    open val posterId: String,
    open val description: String,
    open var rating: Int,
    open var fav_state: Boolean,
) : Parcelable