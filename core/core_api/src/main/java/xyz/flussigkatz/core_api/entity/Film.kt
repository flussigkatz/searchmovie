package xyz.flussigkatz.core_api.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cashed_films", indices = [Index(value = ["title"], unique = true)])
data class Film(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "poster_path") val posterId: String,
    @ColumnInfo(name = "overview") val description: String,
    @ColumnInfo(name = "vote_average") var rating: Int,
    @ColumnInfo(name = "marked") var fav_state: Boolean = false,
) : Parcelable