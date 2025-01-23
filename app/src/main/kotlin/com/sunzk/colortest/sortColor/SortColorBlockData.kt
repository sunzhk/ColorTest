package com.sunzk.colortest.sortColor

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.sunzk.colortest.entity.HSB

/**
 * 色块数据实体
 */
data class SortColorBlockData(
	val color: SortColorData,
	val offset: Offset,
	val size: Size,
) {

	companion object {
		private const val TAG: String = "SortColorBlockData"
	}

	/**
	 * 判断是否在色块内，用于在手指按下时找到对应的色块
	 */
	fun checkInBlock(offset: Offset): Boolean {
		return offset.x in (this.offset.x..(this.offset.x + this.size.width))
				&& offset.y in (this.offset.y..(this.offset.y + this.size.height))
	}

	/**
	 * 检查是否在色块周围，用于判断是否可以交换位置
	 */
	fun checkAroundBlock(offset: Offset): Boolean {
		val distance = (offset - this.offset).getDistance()
		Log.d(TAG, "SortColorBlockData#checkAroundBlock- distance=$distance, allowDistance=${this.size.width / 3}")
		return distance < (this.size.width / 2)
	}
}