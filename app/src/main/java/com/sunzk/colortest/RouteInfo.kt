package com.sunzk.colortest

object RouteInfo {

	const val GROUP_APP = "app"
	const val GROUP_GAME = "game"
	
	const val PATH_ACTIVITY_MODE_SELECT = "/$GROUP_APP/modeSelect"
	const val DESC_ACTIVITY_MODE_SELECT = "模式选择页"


	const val PATH_ACTIVITY_MOCK_COLOR = "/$GROUP_GAME/mockColor"
	const val DESC_ACTIVITY_MOCK_COLOR = "游戏-模仿颜色"
	const val PATH_ACTIVITY_INTERMEDIATE_COLOR = "/$GROUP_GAME/intermediateColor"
	const val DESC_ACTIVITY_INTERMEDIATE_COLOR = "游戏-找中间色"
	const val PATH_ACTIVITY_FIND_DIFF_COLOR = "/$GROUP_GAME/findDiffColor"
	const val DESC_ACTIVITY_FIND_DIFF_COLOR = "游戏-找不同"
	const val PATH_ACTIVITY_FIND_SAME_COLOR = "/$GROUP_GAME/findSameColor"
	const val DESC_ACTIVITY_FIND_SAME_COLOR = "游戏-找相同"

	enum class RouteMap(val path: String, val group: String = "", val desc: String = "") {
		activityModeSelect(PATH_ACTIVITY_MODE_SELECT, GROUP_APP, DESC_ACTIVITY_MODE_SELECT),
		activityMockColor(PATH_ACTIVITY_MOCK_COLOR, GROUP_GAME, DESC_ACTIVITY_MOCK_COLOR),
		activityIntermediateColor(PATH_ACTIVITY_INTERMEDIATE_COLOR, GROUP_GAME, DESC_ACTIVITY_INTERMEDIATE_COLOR),
		activityFindDiffColor(PATH_ACTIVITY_FIND_DIFF_COLOR, GROUP_GAME, DESC_ACTIVITY_FIND_DIFF_COLOR),
		activityFindSameColor(PATH_ACTIVITY_FIND_SAME_COLOR, GROUP_GAME, DESC_ACTIVITY_FIND_SAME_COLOR)
	}
}
