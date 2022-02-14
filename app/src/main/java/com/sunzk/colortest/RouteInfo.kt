package com.sunzk.colortest

object RouteInfo {

	const val GROUP_APP = "app"
	const val GROUP_GAME = "game"
	
	const val PATH_ACTIVITY_MODE_SELECT = "/$GROUP_APP/modeSelect"
	const val DESC_ACTIVITY_MODE_SELECT = "模式选择页"
	const val PATH_ACTIVITY_FIND_DIFF_COLOR = "/$GROUP_GAME/findDiffColor"
	const val DESC_ACTIVITY_FIND_DIFF_COLOR = "游戏-找不同"
	const val PATH_ACTIVITY_GUESS_COLOR = "/$GROUP_GAME/guessColor"
	const val DESC_ACTIVITY_GUESS_COLOR = "游戏-猜中间色"
	const val PATH_ACTIVITY_MOCK_COLOR = "/$GROUP_GAME/mockColor"
	const val DESC_ACTIVITY_MOCK_COLOR = "游戏-模仿颜色"

	enum class RouteMap(val path: String, val group: String = "", val desc: String = "") {
		activityModeSelect(PATH_ACTIVITY_MODE_SELECT, GROUP_APP, DESC_ACTIVITY_MODE_SELECT),
		activityFindDiffColor(PATH_ACTIVITY_FIND_DIFF_COLOR, GROUP_GAME, DESC_ACTIVITY_FIND_DIFF_COLOR),
		activityGuessColor(PATH_ACTIVITY_GUESS_COLOR, GROUP_GAME, DESC_ACTIVITY_GUESS_COLOR),
		activityMockColor(PATH_ACTIVITY_MOCK_COLOR, GROUP_GAME, DESC_ACTIVITY_MOCK_COLOR)
	}
}
