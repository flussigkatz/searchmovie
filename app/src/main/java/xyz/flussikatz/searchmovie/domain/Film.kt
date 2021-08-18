package xyz.flussikatz.searchmovie.domain

import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import kotlinx.parcelize.Parcelize
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.ApiConstants

@Parcelize
data class Film(
    val id: Int,
    val title: String,
    val posterId: String,
    val description: String,
    var rating: Int = 0,
    var fav_state: Boolean = false
) : Parcelable {

    companion object {
        @BindingAdapter("setImageRes")
        @JvmStatic
        fun setImage(view: ImageView, image: String) {
            Picasso.get()
                .load(ApiConstants.IMAGES_URL + "w154" + image)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.wait)
                .error(R.drawable.err)
                .into(view)
        }
    }
}