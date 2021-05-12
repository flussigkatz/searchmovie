package xyz.flussikatz.searchmovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.film_item.view.*
import xyz.flussikatz.searchmovie.MainActivity.Up

class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
    interface OnItemClickListener {
        fun click(film: Film)
    }


    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.title
        private val poster = itemView.poster
        private val description = itemView.description
        private val favorite = itemView.favorite
        fun bind(film: Film) {
            title.text = film.title
            poster.setImageResource(film.poster)
            description.text = film.description
            favorite.isChecked = items[adapterPosition].fav_state
            favorite.setOnCheckedChangeListener { _, isChecked ->
                val newList = items
                newList[adapterPosition].fav_state = isChecked
            }
        }


    }
}