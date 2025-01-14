package com.sunzk.colortest.mockColor

import com.sunzk.colortest.db.bean.MockColorResult
import com.sunzk.colortest.entity.HSB

/**
 * @param difficulty 难度
 * @param questionHSB 当前问题的颜色
 */
data class MockColorPageData(
	val difficulty: MockColorResult.Difficulty,
	val questionHSB: HSB,
	val pickHSB: HSB,
) {
	companion object {
		fun default(difficulty: MockColorResult.Difficulty = MockColorResult.Difficulty.Normal) = MockColorPageData(
			difficulty = difficulty,
			questionHSB = HSB.random(0f, 20f, 20f),
			pickHSB = HSB(180f, 50f, 50f)
		)
	}
}
