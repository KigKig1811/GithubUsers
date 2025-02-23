package com.xt.githubusers.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * ItemDecoration to add vertical spacing between items in a RecyclerView.
 *
 * This class provides a more idiomatic and flexible way to add vertical spacing
 * compared to directly setting margins on the item views.
 *
 * @param verticalSpaceHeight The desired vertical space (in pixels) between items.
 * @param includeEdge Whether to include spacing at the top and bottom edges of the list.
 */
class VerticalSpaceItemDecoration(
    private val verticalSpaceHeight: Int,
    private val includeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) {
            return
        }

        if (includeEdge) {
            outRect.top = verticalSpaceHeight
            outRect.bottom = verticalSpaceHeight
        } else {
            if (position != 0) {
                outRect.top = verticalSpaceHeight
            }
        }
    }
}