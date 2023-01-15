package xyz.flussigkatz.searchmovie.view.rv_viewholder

import android.graphics.Color
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import timber.log.Timber
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.ConstantsApp
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.databinding.FilmItemBinding
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmPagingAdapter.OnCheckboxClickListener
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmPagingAdapter.OnItemClickListener

class FilmViewHolder(private val binding: FilmItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private val callbackPicasso = object : Callback {
        override fun onSuccess() {
            binding.poster.setBackgroundColor(Color.TRANSPARENT)
        }

        override fun onError(e: Exception?) {
            Timber.d(e)
        }
    }

    fun bind(
        film: FilmUiModel,
        clickListener: OnItemClickListener,
        checkboxClickListener: OnCheckboxClickListener,
    ) {
        binding.film = film
        binding.favoriteCheckBox.setOnClickListener {
            checkboxClickListener.click(film, it as CheckBox)
        }
        binding.rootFilmItem.setOnClickListener {
            clickListener.click(film)
        }
        Picasso.get()
            .load(ConstantsApp.IMAGES_URL + IMAGE_FORMAT_W154 + film.posterId)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_default_picture)
            .error(R.drawable.ic_default_picture)
            .into(binding.poster, callbackPicasso)
    }

    companion object {
        private const val IMAGE_FORMAT_W154 = "w154"
    }
}