package xyz.flussikatz.searchmovie.domain

import android.os.Parcelable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import kotlinx.android.parcel.Parcelize
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.view.customview.RatingDonutView

@Parcelize
data class Film(
    val id: Int,
    val title: String,
    @DrawableRes val posterId: Int,
    val description: String,
    var rating: Int = 0,
    var fav_state: Boolean = false
) : Parcelable {

    companion object {
        @BindingAdapter("setImageRes")
        @JvmStatic
        fun setImage(view: ImageView, imageId: Int) {
            view.setImageResource(imageId)
        }

        @BindingAdapter("rating")
        @JvmStatic
        fun animationRatingDonut(view: RatingDonutView, rating: Int) {
            AnimationHelper.ratingDonutAnimation(
                view,
                "progress",
                rating
            )
        }

    }
}