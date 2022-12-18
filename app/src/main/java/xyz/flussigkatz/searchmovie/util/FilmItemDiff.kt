package xyz.flussigkatz.searchmovie.util

import androidx.recyclerview.widget.DiffUtil
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel

object FilmItemDiff : DiffUtil.ItemCallback<FilmUiModel>() {

    override fun areItemsTheSame(oldItem: FilmUiModel, newItem: FilmUiModel) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: FilmUiModel, newItem: FilmUiModel) = oldItem == newItem
}