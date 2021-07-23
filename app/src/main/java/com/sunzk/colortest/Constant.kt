package com.sunzk.colortest

import com.sunzk.colortest.activity.FindDiffColorActivity
import com.sunzk.colortest.activity.GuessColorActivity
import com.sunzk.colortest.activity.GuessColorActivity.Difficulty
import com.sunzk.colortest.activity.MockColorActivity
import com.sunzk.colortest.activity.SelectPicActivity
import com.sunzk.colortest.entity.ModeEntity

object Constant {
    val MODE_ENTITY_LIST: Array<ModeEntity>
    const val MODE_SELECT_DATA_NAME = "settings"
    const val MODE_SELECT_DATA_KEY = "modeList"

    init {
        val bundle1 = mapOf(Pair(GuessColorActivity.INTENT_KEY_DIFFICULTY, Difficulty.EASY.name))
        val bundle2 = mapOf(Pair(GuessColorActivity.INTENT_KEY_DIFFICULTY, Difficulty.MEDIUM.name))
        val bundle3 = mapOf(Pair(GuessColorActivity.INTENT_KEY_DIFFICULTY, Difficulty.DIFFICULT.name))
        MODE_ENTITY_LIST = arrayOf(
            ModeEntity("标准模式", MockColorActivity::class.java.name),
            ModeEntity("地狱模式(体验版)", GuessColorActivity::class.java.name, bundle1),
            ModeEntity("地狱模式(中等)", GuessColorActivity::class.java.name, bundle2),
            ModeEntity("地狱模式(困难)", GuessColorActivity::class.java.name, bundle3),
            ModeEntity("找不同", FindDiffColorActivity::class.java.name),
            ModeEntity("选图片", SelectPicActivity::class.java.name)
        )
    }
}