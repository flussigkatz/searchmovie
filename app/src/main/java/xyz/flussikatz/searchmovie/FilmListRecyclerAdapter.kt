package xyz.flussikatz.searchmovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.film_item.view.*

class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener, private val checkedListener: OnCheckedChangeListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = ArrayList<Film>()


    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FilmViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.film_item, parent, false))
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is FilmViewHolder -> {
                holder.bind(items[position])
                holder.itemView.item_container.setOnClickListener {
                    clickListener.click(items[position])
                }
            }
        }
    }
    fun addItems(list:List<Film>){
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


    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.title
        private val poster = itemView.poster
        private val description = itemView.description
        val favorite = itemView.favorite
        fun bind(film: Film) {
            title.text = film.title
            poster.setImageResource(film.poster)
            description.text = film.description
            favorite.isChecked = items[adapterPosition].fav_state
            favorite.setOnCheckedChangeListener { _, isChecked ->
//                items[adapterPosition].fav_state = isChecked
                checkedListener.checkedChange(adapterPosition, isChecked)
            }
        }
    }
}