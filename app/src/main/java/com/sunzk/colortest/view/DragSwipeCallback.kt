package com.sunzk.colortest.view

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragSwipeCallback(
    /**
     * 通过此变量通知外界发生了排序、删除等操作
     */
    private val mAdapter: IDragSwipe
) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // 确定拖拽、滑动支持的方向
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(
            dragFlags,
            swipeFlags
        )
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    /**
     * 拖拽、交换事件
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mAdapter.onItemSwapped(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    /**
     * 滑动成功的事件
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.END -> mAdapter.onItemDone(viewHolder.adapterPosition)
            ItemTouchHelper.START -> mAdapter.onItemDeleted(viewHolder.adapterPosition)
            else -> {
            }
        }
    } //    /**

}