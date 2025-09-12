package com.sunzk.colortest.game.sortColor

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

	/**
	 * 触摸拦截 - 动画过程中禁止点击按钮什么的
	 */
	private val _canTouch = MutableStateFlow(true)
	val canTouch: StateFlow<Boolean> = _canTouch

	/**
	 * 结束动画 - true成功；false失败；null不显示动画
	 */
	private val _resultAnim = MutableStateFlow<Boolean?>(null)
	val resultAnim: StateFlow<Boolean?> = _resultAnim
	
	fun nextQuestion() {
		_colorArray1.emitBy(randomLeftColors())
		_colorArray2.emitBy(randomRightColors())
		_resultAnim.emitBy(null)
	}
	
	private fun randomLeftColors(): Array<SortColorData> {
		return randomColors(start = HSB.random(minH = 0f, maxH = 140f, minS = 40f, maxS = 70f, minB = 50f, maxB = 70f), positiveSequence = true)
	}
	private fun randomRightColors(): Array<SortColorData> {
		return randomColors(start = HSB.random(minH = 180f, maxH = 320f, minS = 40f, maxS = 70f, minB = 50f, maxB = 70f), positiveSequence = false)
	}

	private fun randomColors(
		start: HSB,
		end: HSB = start.copy(h = start.h + 25, s = start.s + 20, b = start.b + 25),
		number: Int = COLOR_COUNT,
		positiveSequence: Boolean = true
	): Array<SortColorData> {
		val fromColor = if (positiveSequence) start else end
		val toColor = if (positiveSequence) end else start
		val randomColorDataArray = Array(number - 2) { index ->
			SortColorData(
				ordinal = index + 1,
				canMove = true,
				color = HSB(
					fromColor.h + (toColor.h - fromColor.h) * (index + 1) / (number - 1),
					fromColor.s + (toColor.s - fromColor.s) * (index + 1) / (number - 1),
					fromColor.b + (toColor.b - fromColor.b) * (index + 1) / (number - 1)
				)
			)
		}
		randomColorDataArray.shuffle()
		val colors = Array(number) { index ->
			when (index) {
				0 -> SortColorData(fromColor, index, false)
				number - 1 -> SortColorData(toColor, index, false)
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
	
	fun setCanTouch(finish: Boolean) {
		_canTouch.emitBy(finish)
	}

	fun checkResult() {
		var result = true
		colorArray1.value.forEachIndexed { index, sortColorData ->
			if (index != sortColorData.ordinal) {
				result = false
				return@forEachIndexed
			}
		}
		colorArray2.value.forEachIndexed { index, sortColorData ->
			if (index != sortColorData.ordinal) {
				result = false
				return@forEachIndexed
			}
		}
		_resultAnim.emitBy(result)
	}
}

data class SortColorData(
	val color: HSB,
	val ordinal: Int,
	val canMove: Boolean,
	val showResult: Boolean = false
)