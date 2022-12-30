package xyz.flussigkatz.searchmovie.view.rv_adapters

import android.widget.CheckBox
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmPagingAdapter.OnCheckboxClickListener

typealias OnCheckboxClickAction = (film: FilmUiModel, view: CheckBox) -> Unit

class OnCheckboxClickListener(private val action: OnCheckboxClickAction) : OnCheckboxClickListener {
    override fun click(film: FilmUiModel, view: CheckBox) {
        action(film, view)
    }
}