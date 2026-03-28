package com.sunzk.colortest

import com.sunzk.colortest.game.navigation.GameRoutes

object RouteInfo {

    enum class GameMap(val modeName: String, val route: String, val enable: Boolean = false) {
        ActivityMockColor("模拟色彩", GameRoutes.MOCK_COLOR, true),
        ActivityIntermediateColor("寻找中间色", GameRoutes.INTERMEDIATE_COLOR, true),
        ActivityFindDiffColor("找不同", GameRoutes.FIND_DIFF_COLOR, true),
        ActivityFindSameColor("找相同", GameRoutes.FIND_SAME_COLOR, true),
        ActivitySortColor("色彩排序", GameRoutes.SORT_COLOR, true),
    }
}
