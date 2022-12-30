package xyz.flussigkatz.searchmovie.view.rv_adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener

typealias OnScrollAction = (recyclerView: RecyclerView) -> Unit

class OnScrollListener(private val action: OnScrollAction) : OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy != IS_SCROLL_FLAG) action(recyclerView)
    }

    companion object {
        private const val IS_SCROLL_FLAG = 0
    }
}