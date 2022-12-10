package xyz.flussigkatz.core_api.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cashed_now_playing_films", indices = [Index(value = ["title"], unique = true)])
data class NowPlayingFilm(
    @PrimaryKey override val id: Int,
    @ColumnInfo(name = "title") override val title: String,
    @ColumnInfo(name = "poster_path") override val posterId: String,
    @ColumnInfo(name = "overview") override val description: String,
    @ColumnInfo(name = "vote_average") override var rating: Int,
    @ColumnInfo(name = "marked") override var fav_state: Boolean,
) : AbstractFilmEntity(
    id = id,
    title = title,
    posterId = posterId,
    description = description,
    rating = rating,
    fav_state = fav_state
)