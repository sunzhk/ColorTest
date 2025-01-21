package com.sunzk.colortest.sortColor

import androidx.lifecycle.ViewModel
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.entity.HSB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SortColorViewModel: ViewModel() {
	companion object {
		private const val TAG: String = "SortColorViewModel"
		const val COLOR_COUNT = 7
	}

	/**
	 * 颜色列表1
	 */
	private val _colorArray1 = MutableStateFlow(randomColors())
	val colorArray1: StateFlow<Array<SortColorData>> = _colorArray1

	/**
	 * 颜色列表2
	 */
	private val _colorArray2 = MutableStateFlow(randomColors())
	val colorArray2: StateFlow<Array<SortColorData>> = _colorArray2


	private fun randomColors(start: HSB = HSB.random(minH = 0f, maxH = 320f, minS = 40f, maxS = 70f, minB = 50f, maxB = 70f), 
	                         end: HSB = start.copy(h = start.h + 40, s = start.s + 30, b = start.b + 30), number: Int = COLOR_COUNT): Array<SortColorData> {
		val colors = Array(number) { index ->
			SortColorData(
				ordinal = index,
				color = if (index == 0) {
					start
				} else if (index == number - 1) {
					end
				} else {
					HSB(
						start.h + (end.h - start.h) * index / (number - 1),
						start.s + (end.s - start.s) * index / (number - 1),
						start.b + (end.b - start.b) * index / (number - 1)
					)
				}
			)
		}
		colors.shuffle()
		return colors
	}

	fun onBoxArray1Drag(from: Int, to: Int) {
		_colorArray1.emitBy(_colorArray1.value.apply {
			val temp = this[from]
			this[from] = this[to]
			this[to] = temp
		})
	}

	fun onBoxArray2Drag(from: Int, to: Int) {
		_colorArray2.emitBy(_colorArray2.value.apply {
			val temp = this[from]
			this[from] = this[to]
			this[to] = temp
		})
	}
}

data class SortColorData(
	val color: HSB,
	val ordinal: Int
)