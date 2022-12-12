package xyz.flussigkatz.searchmovie.view.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import xyz.flussigkatz.core_api.entity.AbstractFilmEntity
import xyz.flussigkatz.searchmovie.databinding.FilmItemBinding
import xyz.flussigkatz.searchmovie.util.FilmDiff
import xyz.flussigkatz.searchmovie.view.rv_viewholder.FilmViewHolder

class FilmListRecyclerAdapter(
    private val clickListener: OnItemClickListener,
    private val checkboxClickListener: OnCheckboxClickListener,
) : RecyclerView.Adapter<FilmViewHolder>() {
    private var items = listOf<AbstractFilmEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FilmViewHolder(FilmItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        items[position].let { holder.bind(it, clickListener, checkboxClickListener) }
    }

    override fun getItemCount() = items.size

    fun updateData(newList: List<AbstractFilmEntity>) {
        val diffResult = DiffUtil.calculateDiff(FilmDiff(items, newList))
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun click(film: AbstractFilmEntity)
    }

    interface OnCheckboxClickListener {
        fun click(film: AbstractFilmEntity, view: CheckBox)
    }
}