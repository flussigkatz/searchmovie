package xyz.flussigkatz.searchmovie.view.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.paging.PagingDataAdapter
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.databinding.FilmItemBinding
import xyz.flussigkatz.searchmovie.util.FilmItemDiff
import xyz.flussigkatz.searchmovie.view.rv_viewholder.FilmViewHolder

class FilmPagingAdapter(
    private val clickListener: OnItemClickListener,
    private val checkboxClickListener: OnCheckboxClickListener,
) : PagingDataAdapter<FilmUiModel, FilmViewHolder>(FilmItemDiff) {
    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, clickListener, checkboxClickListener) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FilmViewHolder(FilmItemBinding.inflate(inflater, parent, false))
    }

    interface OnItemClickListener {
        fun click(film: FilmUiModel)
    }

    interface OnCheckboxClickListener {
        fun click(film: FilmUiModel, view: CheckBox)
    }
}