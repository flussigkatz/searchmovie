package xyz.flussigkatz.searchmovie.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "marked_films", indices = [Index(value = ["title"], unique = true)])
data class MarkedFilm(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "title") val title: String = "Empty",
    @ColumnInfo(name = "poster_path") val posterId: String = "Empty",
    @ColumnInfo(name = "overview") val description: String = "Empty",
    @ColumnInfo(name = "vote_average") var rating: Int = 0,
    @ColumnInfo(name = "marked") var fav_state: Boolean = false
) : Parcelable