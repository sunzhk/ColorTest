package com.sunzk.colortest.findSameColor

import android.util.Range
import org.jetbrains.annotations.TestOnly

data class FindSameColorResult(
	val difficulty: Difficulty,
	val clickCount: Int,
	val rightCount: Int
) {

	enum class Difficulty(val text: String, val boxNumber: Int, private val maxTryCount: Int, private val scoreMagnification: Float, val hDiffRange: Range<Int>, val sDiffRange: Range<Int>, val bDiffRange: Range<Int>) {
		Easy("简单", 4, 3, 1f, Range(5, 10), Range(5, 10), Range(5, 10)),
		Normal("普通", 9, 5, 2f, Range(4, 8), Range(4, 8), Range(4, 8)),
		Hard("困难", 16, 9, 3f, Range(3, 6), Range(3, 6), Range(3, 6));
		
		fun addScore(clickCount: Int): Int {
			return if (clickCount > maxTryCount) {
				0
			} else {
				(10 * scoreMagnification / clickCount).toInt()
			}
		}
	}

}