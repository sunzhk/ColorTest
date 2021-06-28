package com.sunzk.colortest.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Created by chances on 16/6/23.
 */
class SpaceItemDecoration : ItemDecoration {
    private var leftSpace: Int
    private var topSpace: Int
    private var rightSpace: Int
    private var bottomSpace: Int
    private var isShowInterValInEdge = true

    constructor(leftSpace: Int, topSpace: Int, rightSpace: Int, bottomSpace: Int) {
        this.leftSpace = leftSpace
        this.topSpace = topSpace
        this.rightSpace = rightSpace
        this.bottomSpace = bottomSpace
    }

    constructor(
        leftSpace: Int,
        topSpace: Int,
        rightSpace: Int,
        bottomSpace: Int,
        isShowInterValInEdge: Boolean
    ) {
        this.leftSpace = leftSpace
        this.topSpace = topSpace
        this.rightSpace = rightSpace
        this.bottomSpace = bottomSpace
        this.isShowInterValInEdge = isShowInterValInEdge
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = bottomSpace
        outRect.left = leftSpace
        outRect.right = rightSpace
        outRect.top = topSpace
        if (!isShowInterValInEdge) {
            val layoutManager = parent.layoutManager
            if (layoutManager is GridLayoutManager) {
            } else if (layoutManager is LinearLayoutManager) {
                val orientation = layoutManager.orientation
                if (orientation == LinearLayoutManager.HORIZONTAL) {
                    val childAdapterPosition = parent.getChildAdapterPosition(view)
                    if (childAdapterPosition == 0) {
                        outRect.left = 0
                    } else if (childAdapterPosition == layoutManager.getItemCount() - 1) {
                        outRect.right = 0
                    }
                } else if (orientation == LinearLayoutManager.VERTICAL) {
                    val childAdapterPosition = parent.getChildAdapterPosition(view)
                    if (childAdapterPosition == 0) {
                        outRect.top = 0
                    } else if (childAdapterPosition == layoutManager.getItemCount() - 1) {
                        outRect.bottom = 0
                    }
                }
            }
        }
    }
}