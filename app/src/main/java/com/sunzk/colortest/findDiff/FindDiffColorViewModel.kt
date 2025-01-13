package com.sunzk.colortest.findDiff

import android.util.Log
import androidx.lifecycle.ViewModel
import com.sunzk.base.expand.emitBy
import com.sunzk.base.utils.ColorUtils
import com.sunzk.demo.tools.ext.square
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Random

class FindDiffColorViewModel : ViewModel() {

	companion object {
		private const val TAG: String = "FindDiffColorViewModel"
		const val MIN_LEVEL = 1
		const val DEFAULT_LEVEL = 3
		const val MAX_LEVEL = 10
	}

	val currentGameData = MutableStateFlow(randomData(DEFAULT_LEVEL))

	fun nextRandomData() {
		val level = currentGameData.value.level
		currentGameData.emitBy(randomData(level))
	}
	
	fun changeLevel(level: Int) {
		currentGameData.emitBy(randomData(level))
	}

	private fun randomData(level: Int): FindDiffColorData {
		val countPerSide = level + 2
		val baseColor = ColorUtils.randomHSBColor(0f, 0f, 0.2f)
		val colorDiff = colorDiff(level)
		Log.d(TAG, "resetColor-colorDiff: $colorDiff")
		val diffSmall = colorDiff / 20
		val diffS =
			if (baseColor[1] > 0.5f) baseColor[1] - diffSmall else baseColor[1] + diffSmall
		val diffB =
			if (baseColor[2] > 0.6f) baseColor[2] - colorDiff else baseColor[2] + colorDiff
		val diffColor = floatArrayOf(baseColor[0], diffS, diffB)
		return FindDiffColorData(level, countPerSide, baseColor, diffColor, Random().nextInt(square(countPerSide)))
	}

	private fun colorDiff(level: Int): Float {
		Log.d(TAG, "getColorDiff: " + level + " - " + 0.3f / (level * 10))
		return 0.02f + 0.5f / (level * 10)
	}
}