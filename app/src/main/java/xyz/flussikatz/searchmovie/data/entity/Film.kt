package xyz.flussikatz.searchmovie.data.entity

import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.picasso.Picasso
import kotlinx.parcelize.Parcelize
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.ApiConstantsApp

@Parcelize
@Entity(tableName = "cashed_films", indices = [Index(value = ["title"], unique = true)])
data class Film(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "title") val title: String = "Empty",
    @ColumnInfo(name = "poster_path") val posterId: String = "Empty",
    @ColumnInfo(name = "overview") val description: String = "Empty",
    @ColumnInfo(name = "vote_average") var rating: Int = 0,
    @ColumnInfo(name = "marked") var fav_state: Boolean = false
) : Parcelable {

    companion object {
        @BindingAdapter("setImageRes")
        @JvmStatic
        fun setImage(view: ImageView, image: String) {
            Picasso.get()
                .load(ApiConstantsApp.IMAGES_URL + ApiConstantsApp.IMAGE_FORMAT_W154 + image)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.wait)
                .error(R.drawable.err)
                .into(view)
        }
    }
}