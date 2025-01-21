package com.sunzk.colortest.sortColor

import androidx.lifecycle.ViewModel
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.entity.HSB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SortColorViewModel: ViewModel() {
	companion object {
		private const val TAG: String = "SortColorViewModel"
		const val COLOR_COUNT = 9
	}

	/**
	 * 颜色列表1
	 */
	private val _colorArray1 = MutableStateFlow(randomLeftColors())
	val colorArray1: StateFlow<Array<SortColorData>> = _colorArray1

	/**
	 * 颜色列表2
	 */
	private val _colorArray2 = MutableStateFlow(randomRightColors())
	val colorArray2: StateFlow<Array<SortColorData>> = _colorArray2

	private fun randomLeftColors(): Array<SortColorData> {
		return randomColors(start = HSB.random(minH = 0f, maxH = 140f, minS = 40f, maxS = 70f, minB = 50f, maxB = 70f))
	}
	private fun randomRightColors(): Array<SortColorData> {
		return randomColors(start = HSB.random(minH = 180f, maxH = 320f, minS = 40f, maxS = 70f, minB = 50f, maxB = 70f))
	}

	private fun randomColors(
		start: HSB,
		end: HSB = start.copy(h = start.h + 40, s = start.s + 30, b = start.b + 30),
		number: Int = COLOR_COUNT,
	): Array<SortColorData> {
		val randomColorDataArray = Array(number - 2) { index ->
			SortColorData(
				ordinal = index + 1,
				color = HSB(
					start.h + (end.h - start.h) * (index + 1) / (number - 1),
					start.s + (end.s - start.s) * (index + 1) / (number - 1),
					start.b + (end.b - start.b) * (index + 1) / (number - 1)
				)
			)
		}
		randomColorDataArray.shuffle()
		val colors = Array(number) { index ->
			when (index) {
				0 -> SortColorData(start, index)
				number - 1 -> SortColorData(end, index)
				else -> randomColorDataArray[index - 1]
			}
		}
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
	
	fun checkResult(): Boolean {
		colorArray1.value.forEachIndexed { index, sortColorData -> 
			if (index != sortColorData.ordinal) {
				return false
			}
		}
		colorArray2.value.forEachIndexed { index, sortColorData ->
			if (index != sortColorData.ordinal) {
				return false
			}
		}
		return true
	}
}

data class SortColorData(
	val color: HSB,
	val ordinal: Int,
	val showResult: Boolean = false
)