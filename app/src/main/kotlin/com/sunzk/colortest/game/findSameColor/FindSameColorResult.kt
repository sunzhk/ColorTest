package com.sunzk.colortest.game.findSameColor

import android.util.Range
import com.sunzk.colortest.game.IDifficulty
import kotlin.enums.EnumEntries

data class FindSameColorResult(
	val difficulty: Difficulty,
	val clickCount: Int,
	val rightCount: Int
) {

	enum class Difficulty(
		override val text: String,
		val boxNumber: Int,
		private val maxTryCount: Int,
		private val scoreMagnification: Float,
		val hDiffRange: Range<Int>,
		val sDiffRange: Range<Int>,
		val bDiffRange: Range<Int>,
	): IDifficulty {
		Easy("简单", 4, 2, 1f, Range(10, 20), Range(8, 14), Range(8, 14)),
		Normal("普通", 9, 4, 2f, Range(9, 19), Range(7, 14), Range(7, 14)),
		Hard("困难", 16, 8, 3f, Range(8, 18), Range(6, 14), Range(6, 14));

		fun addScore(clickCount: Int): Int {
			return if (clickCount > maxTryCount) {
				0
			} else {
				(10 * scoreMagnification / clickCount).toInt()
			}
		}
	}

}