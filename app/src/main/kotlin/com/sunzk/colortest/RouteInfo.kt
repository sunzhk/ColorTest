package com.sunzk.colortest

object RouteInfo {

	enum class GameMap(val modeName: String, val navigationAction: Int) {
		ActivityMockColor("模拟色彩", R.id.action_modeSelect_to_mockColor),
		ActivityIntermediateColor("寻找中间色", R.id.action_modeSelect_to_intermediateColor),
		ActivityFindDiffColor("找不同", R.id.action_modeSelect_to_findDiffColor),
		ActivityFindSameColor("找相同", R.id.action_modeSelect_to_findSameColor),
		ActivitySortColor("色彩排序", R.id.action_modeSelect_to_sortColor),
	}
}
