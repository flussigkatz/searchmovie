package xyz.flussigkatz.searchmovie.util

import androidx.recyclerview.widget.DiffUtil
import xyz.flussigkatz.core_api.entity.AbstractFilmEntity

class FilmDiff(
    private var oldList: List<AbstractFilmEntity>,
    private var newList: List<AbstractFilmEntity>,
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].title == newList[newItemPosition].title &&
                oldList[oldItemPosition].posterId == newList[newItemPosition].posterId &&
                oldList[oldItemPosition].description == newList[newItemPosition].description &&
                oldList[oldItemPosition].rating == newList[newItemPosition].rating &&
                oldList[oldItemPosition].fav_state == newList[newItemPosition].fav_state
}