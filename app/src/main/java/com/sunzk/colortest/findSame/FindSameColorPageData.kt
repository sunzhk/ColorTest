package com.sunzk.colortest.findSame

import com.sunzk.colortest.entity.HSB

data class FindSameColorPageData(
	val difficulty: FindSameColorResult.Difficulty,
	val questionHSB: HSB,
)