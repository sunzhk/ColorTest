package com.sunzk.colortest.sortColor

import androidx.lifecycle.ViewModel
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


	private fun randomColors(start: HSB = HSB.random(minH = 0f, maxH = 360f, minS = 40f, maxS = 80f, minB = 50f, maxB = 80f), 
	                         end: HSB = start.copy(h = start.h + 20, s = start.s + 20, b = start.b + 20), number: Int = COLOR_COUNT): Array<SortColorData> {
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
}

data class SortColorData(
	val color: HSB,
	val ordinal: Int
)