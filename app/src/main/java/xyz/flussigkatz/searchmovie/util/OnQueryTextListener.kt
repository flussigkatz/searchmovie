package xyz.flussigkatz.searchmovie.util

import androidx.appcompat.widget.SearchView

typealias QueryAction = (query: String) -> Unit

class OnQueryTextListener(private val action: QueryAction) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        action(query.orEmpty())
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        action(newText.orEmpty())
        return true
    }
}