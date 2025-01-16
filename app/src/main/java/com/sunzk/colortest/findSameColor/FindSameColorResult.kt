package com.sunzk.colortest.findSameColor

import android.util.Range

data class FindSameColorResult(
	val difficulty: FindSameColorResult.Difficulty,
	val clickCount: Int,
	val rightCount: Int
) {

	enum class Difficulty(val text: String, val boxNumber: Int, val hDiffRange: Range<Int>, val sDiffRange: Range<Int>, val bDiffRange: Range<Int>) {
		Easy("简单", 4, Range(5, 10), Range(5, 10), Range(5, 10)),
		Normal("普通", 9, Range(4, 8), Range(4, 8), Range(4, 8)),
		Hard("困难", 16, Range(3, 6), Range(3, 6), Range(3, 6));
	}

}
