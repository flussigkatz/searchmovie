package xyz.flussigkatz.searchmovie.view.rv_adapters

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(private val paddingInDp: Int) : RecyclerView.ItemDecoration() {
    private val Int.convertPx: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == 0) outRect.top = paddingInDp.convertPx
        outRect.bottom = paddingInDp.convertPx
        outRect.left = (paddingInDp.convertPx * SIDE_COEFFICIENT).toInt()
        outRect.right = (paddingInDp.convertPx * SIDE_COEFFICIENT).toInt()
    }

    companion object {
        private const val SIDE_COEFFICIENT = 1.25
    }
}