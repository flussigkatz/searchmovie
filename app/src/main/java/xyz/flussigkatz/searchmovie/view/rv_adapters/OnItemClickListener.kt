package xyz.flussigkatz.searchmovie.view.rv_adapters

import android.content.Intent
import android.os.Bundle
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.NAVIGATE_TO_DETAILS
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.view.rv_adapters.FilmPagingAdapter.OnItemClickListener

typealias OnItemClickAction = (intent: Intent) -> Unit

class OnItemClickListener(private val action: OnItemClickAction) : OnItemClickListener {
    override fun click(film: FilmUiModel) {
        val intent = Intent().apply {
            action = NAVIGATE_TO_DETAILS
            putExtra(DETAILS_FILM_KEY, Bundle().apply { putParcelable(DETAILS_FILM_KEY, film) })
        }
        action(intent)
    }
}