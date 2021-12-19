package xyz.flussigkatz.searchmovie.util

import androidx.recyclerview.widget.DiffUtil
import xyz.flussigkatz.searchmovie.data.entity.Film

class FilmDiff(var oldList: ArrayList<Film>, val newList: ArrayList<Film>) : DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title&&
        oldList[oldItemPosition].posterId == newList[newItemPosition].posterId&&
        oldList[oldItemPosition].description == newList[newItemPosition].description&&
        oldList[oldItemPosition].fav_state == newList[newItemPosition].fav_state&&
        oldList[oldItemPosition].fav_state == newList[newItemPosition].fav_state
    }
}