package com.sunzk.colortest.view

interface IDragSwipe {
    /**
     * 两个Item交换位置
     * @param fromPosition 第一个Item的位置
     * @param toPosition 第二个Item的位置
     */
    fun onItemSwapped(fromPosition: Int, toPosition: Int)

    /**
     * 删除Item
     * @param position 待删除Item的位置
     */
    fun onItemDeleted(position: Int)

    /**
     * Item标记完成
     * @param position Item的位置
     */
    fun onItemDone(position: Int)
}