package xyz.flussikatz.searchmovie.domain

import android.os.Parcelable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.parcel.Parcelize
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.ApiConstants
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.view.customview.RatingDonutView

@Parcelize
data class Film(
    val id: Int,
    val title: String,
    val posterId: String,
    val description: String,
    var rating: Double = 0.0,
    var fav_state: Boolean = false
) : Parcelable {

    companion object {
        @BindingAdapter("setImageRes")
        @JvmStatic
        fun setImage(view: ImageView, image: String) {
            Picasso.get()
                .load(ApiConstants.IMAGES_URL + "w342" + image)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(view)
        }

        @BindingAdapter("rating")
        @JvmStatic
        fun animationRatingDonut(view: RatingDonutView, rating: Double) {
            AnimationHelper.ratingDonutAnimation(
                view,
                "progress",
                rating.toInt()
            )
        }

    }
}