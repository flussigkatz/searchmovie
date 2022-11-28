package xyz.flussigkatz.searchmovie.view.rv_adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import xyz.flussigkatz.core_api.entity.Film
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.ConstantsApp.IMAGES_URL
import xyz.flussigkatz.searchmovie.data.ConstantsApp.IMAGE_FORMAT_W154
import xyz.flussigkatz.searchmovie.databinding.FilmItemBinding
import xyz.flussigkatz.searchmovie.util.FilmDiff
import xyz.flussigkatz.searchmovie.view.rv_viewholder.FilmViewHolder

class FilmListRecyclerAdapter(
    private val clickListener: OnItemClickListener,
    private val checkboxClickListener: OnCheckboxClickListener,
) : RecyclerView.Adapter<FilmViewHolder>() {
    private var items = ArrayList<Film>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FilmViewHolder(FilmItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = findFilmById(items[position].id)

        if (film != null) {
            holder.binding.film = film
            val callbackPicasso = object : Callback {
                override fun onSuccess() {
                    holder.binding.poster.setBackgroundColor(Color.TRANSPARENT)
                }

                override fun onError(e: Exception?) {}
            }

            Picasso.get()
                .load(IMAGES_URL + IMAGE_FORMAT_W154 + film.posterId)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_default_picture)
                .error(R.drawable.ic_default_picture)
                .into(holder.binding.poster, callbackPicasso)

            holder.binding.favoriteCheckBox.setOnClickListener {
                checkboxClickListener.click(film, it as CheckBox)
            }

            holder.binding.rootFilmItem.setOnClickListener {
                clickListener.click(film)
            }
        }

    }

    override fun getItemCount() = items.size

    fun updateData(newList: List<Film>) {
        val diffResult = DiffUtil.calculateDiff(FilmDiff(items, newList as ArrayList<Film>))
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    private fun findFilmById(id: Int): Film? {
        var res: Film? = null
        items.forEach {
            if (it.id == id) res = it
            return@forEach
        }
        return res
    }

    interface OnItemClickListener {
        fun click(film: Film)
    }

    interface OnCheckboxClickListener {
        fun click(film: Film, view: CheckBox)
    }
}