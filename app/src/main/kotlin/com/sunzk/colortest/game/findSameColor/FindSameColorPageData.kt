package com.sunzk.colortest.game.findSameColor

import com.sunzk.colortest.entity.HSB

data class FindSameColorPageData(
	val difficulty: FindSameColorResult.Difficulty,
	val exampleColor: HSB,
	val boxColors: List<HSB>,
)