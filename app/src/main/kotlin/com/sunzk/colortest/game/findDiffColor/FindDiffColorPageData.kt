package com.sunzk.colortest.game.findDiffColor

import com.sunzk.colortest.entity.HSB

data class FindDiffColorPageData(
	val level: Int,
	val countPerSide: Int,
	val baseColor: HSB,
	val diffColor: HSB,
	val diffIndex: Int
)
