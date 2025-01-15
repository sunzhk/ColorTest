package com.sunzk.colortest.findDiffColor

import android.util.Log
import androidx.lifecycle.ViewModel
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.entity.HSB
import com.sunzk.demo.tools.ext.square
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Random

class FindDiffColorViewModel : ViewModel() {

	companion object {
		private const val TAG: String = "FindDiffColorViewModel"
		const val MIN_LEVEL = 1
		const val DEFAULT_LEVEL = 1
		const val MAX_LEVEL = 10
		const val MAX_COUNT_PER_SIDE = 7
	}

	val currentGameData = MutableStateFlow(randomData(DEFAULT_LEVEL))

	fun nextRandomData() {
		val level = currentGameData.value.level
		currentGameData.emitBy(randomData(level))
	}
	
	fun changeLevel(level: Int) {
		currentGameData.emitBy(randomData(level))
	}

	private fun randomData(level: Int): FindDiffColorPageData {
		val countPerSide = (level + 2).coerceAtMost(MAX_COUNT_PER_SIDE)
		val baseColor = HSB.random(0f, 360f, 30f, 90f, 30f, 90f)
		val colorDiff = colorDiff(level)
		Log.d(TAG, "resetColor-colorDiff: $colorDiff")
		val diffSmall = colorDiff / 20
		val diffColor = baseColor.copy(
			s = if (baseColor[1] > 50f) baseColor[1] - diffSmall else baseColor[1] + diffSmall, 
			b = if (baseColor[2] > 60f) baseColor[2] - colorDiff else baseColor[2] + colorDiff
		)
		return FindDiffColorPageData(level, countPerSide, baseColor, diffColor, Random().nextInt(square(countPerSide)))
	}

	private fun colorDiff(level: Int): Float {
		Log.d(TAG, "getColorDiff: " + level + " - " + 0.3f / (level * 10))
		return 1 + (8f / (level * 1.6f))
	}
}