package xyz.flussikatz.searchmovie.view.rv_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import xyz.flussikatz.searchmovie.util.FilmDiff
import xyz.flussikatz.searchmovie.databinding.FilmItemBinding
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.view.rv_viewholder.FilmViewHolder

class FilmListRecyclerAdapter(
    private val clickListener: OnItemClickListener,
    private val checkboxClickListener: OnCheckboxClickListener
) : RecyclerView.Adapter<FilmViewHolder>() {
    var items = ArrayList<Film>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FilmViewHolder(
            FilmItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.binding.film = items[position]

        holder.binding.favoriteCheckBox.setOnClickListener {
            checkboxClickListener.click(items[position], it)
        }

        holder.binding.filmItemCardview.setOnClickListener {
            clickListener.click(items[position])
        }

    }

    override fun getItemCount() = items.size

    fun addItems(list: List<Film>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }


    fun updateData(newList: ArrayList<Film>) {
        val diffResult = DiffUtil.calculateDiff(FilmDiff(items, newList))
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun click(film: Film)
    }

    interface OnCheckboxClickListener {
        fun click(film: Film, view: View)
    }
}