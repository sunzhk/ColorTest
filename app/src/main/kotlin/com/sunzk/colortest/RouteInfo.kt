package com.sunzk.colortest

object RouteInfo {

	enum class GameMap(val modeName: String, val navigationAction: Int, val enable: Boolean = false) {
		ActivityMockColor("模拟色彩", R.id.action_modeSelect_to_mockColor, true),
		ActivityIntermediateColor("寻找中间色", R.id.action_modeSelect_to_intermediateColor, true),
		ActivityFindDiffColor("找不同", R.id.action_modeSelect_to_findDiffColor, true),
		ActivityFindSameColor("找相同", R.id.action_modeSelect_to_findSameColor),
		ActivitySortColor("色彩排序", R.id.action_modeSelect_to_sortColor),
	}
}
