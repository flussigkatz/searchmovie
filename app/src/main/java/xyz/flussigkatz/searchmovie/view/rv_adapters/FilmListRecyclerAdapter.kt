package xyz.flussigkatz.searchmovie.view.rv_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import xyz.flussigkatz.searchmovie.util.FilmDiff
import xyz.flussigkatz.searchmovie.databinding.FilmItemBinding
import xyz.flussigkatz.searchmovie.data.entity.Film
import xyz.flussigkatz.searchmovie.view.rv_viewholder.FilmViewHolder

class FilmListRecyclerAdapter(
    private val clickListener: OnItemClickListener,
    private val checkboxClickListener: OnCheckboxClickListener,
) : RecyclerView.Adapter<FilmViewHolder>() {
    var items = ArrayList<Film>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FilmViewHolder(
            FilmItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = findFilmById(items[position].id)

        if (film != null) {
            holder.binding.film = film

            holder.binding.favoriteCheckBox.setOnClickListener {
                checkboxClickListener.click(film, it)
            }

            holder.binding.filmItemCardview.setOnClickListener {
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

    fun findFilmById(id: Int): Film? {
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
        fun click(film: Film, view: View)
    }
}