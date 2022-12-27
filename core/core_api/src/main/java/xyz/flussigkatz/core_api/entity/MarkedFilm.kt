package xyz.flussigkatz.core_api.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "marked_films", indices = [Index(value = ["title"], unique = true)])
data class MarkedFilm(
    @PrimaryKey(autoGenerate = true) override val localId: Int = 0,
    @ColumnInfo(name = "id") override val id: Int,
    @ColumnInfo(name = "title") override val title: String,
    @ColumnInfo(name = "poster_path") override val posterId: String,
    @ColumnInfo(name = "overview") override val description: String,
    @ColumnInfo(name = "vote_average") override var rating: Int,
    @ColumnInfo(name = "marked") override var favState: Boolean,
) : IFilm {
    constructor(film: IFilm, favState: Boolean? = null) : this(
        id = film.id,
        title = film.title,
        posterId = film.posterId,
        description = film.description,
        rating = film.rating,
        favState = favState ?: film.favState
    )
}