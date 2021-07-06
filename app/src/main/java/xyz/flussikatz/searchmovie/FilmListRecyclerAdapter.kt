package xyz.flussikatz.searchmovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import xyz.flussikatz.searchmovie.databinding.FilmItemBinding

class FilmListRecyclerAdapter(
    private val clickListener: OnItemClickListener,
    private val checkedListener: OnCheckedChangeListener
) : RecyclerView.Adapter<FilmListRecyclerAdapter.FilmViewHolder>() {
    var items = ArrayList<Film>()

    class FilmViewHolder(var binding: FilmItemBinding) :
        RecyclerView.ViewHolder(binding.rootFilmItem)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FilmViewHolder(
// В модуле 32_3 было так. Не заработало. NullPointer
// DataBindingUtil.inflate(inflater, R.layout.film_item, parent, false)
            FilmItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = items[position]
        val title = holder.binding.title
        val poster = holder.binding.poster
        val description = holder.binding.description
        val favorite = holder.binding.favoriteCheckBox
        val ratingView = holder.binding.ratingDonut

        title.text = film.title
        poster.setImageResource(film.poster)
        description.text = film.description
        favorite.isChecked = items[position].fav_state

        AnimationHelper.ratingDonutAnimation(ratingView, "progress", film.rating)

        favorite.setOnCheckedChangeListener { _, isChecked ->
            checkedListener.checkedChange(holder.adapterPosition, isChecked)
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
        val diffResult = DiffUtil.calculateDiff(FilmDiff(this.items, newList))
        this.items = newList
        diffResult.dispatchUpdatesTo(this)

    }

    interface OnItemClickListener {
        fun click(film: Film)
    }

    interface OnCheckedChangeListener {
        fun checkedChange(position: Int, state: Boolean)
    }
}