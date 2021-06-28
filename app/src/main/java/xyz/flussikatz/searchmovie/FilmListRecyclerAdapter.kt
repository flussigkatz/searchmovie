package xyz.flussikatz.searchmovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.film_item.view.*

class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener, private val checkedListener: OnCheckedChangeListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var items = ArrayList<Film>()


    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FilmViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.film_item, parent, false))
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is FilmViewHolder) {
            val film = items[position]
            val title = holder.itemView.title
            val poster = holder.itemView.poster
            val description = holder.itemView.description
            val favorite = holder.itemView.favorite_check_box
//            val rating = holder.itemView.rating_donut
            title.text = film.title
            poster.setImageResource(film.poster)
            description.text = film.description
            favorite.isChecked = items[position].fav_state
            favorite.setOnCheckedChangeListener { _, isChecked ->
                checkedListener.checkedChange(holder.adapterPosition, isChecked)
            }
            holder.itemView.film_item_cardview.setOnClickListener {
                clickListener.click(items[position])
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
}