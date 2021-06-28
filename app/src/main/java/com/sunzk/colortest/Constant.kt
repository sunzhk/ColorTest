package com.sunzk.colortest

import android.content.Intent
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
        val context: MyApplication = MyApplication.instance!!
        val hellIntent1 =
            Intent(context, GuessColorActivity::class.java)
        hellIntent1.putExtra(
            GuessColorActivity.INTENT_KEY_DIFFICULTY,
            Difficulty.EASY
        )
        val hellIntent2 =
            Intent(context, GuessColorActivity::class.java)
        hellIntent2.putExtra(
            GuessColorActivity.INTENT_KEY_DIFFICULTY,
            Difficulty.MEDIUM
        )
        val hellIntent3 =
            Intent(context, GuessColorActivity::class.java)
        hellIntent3.putExtra(
            GuessColorActivity.INTENT_KEY_DIFFICULTY,
            Difficulty.DIFFICULT
        )
        MODE_ENTITY_LIST = arrayOf(
            ModeEntity("标准模式", Intent(context, MockColorActivity::class.java)),
            ModeEntity("地狱模式(体验版)", hellIntent1),
            ModeEntity("地狱模式(中等)", hellIntent2),
            ModeEntity("地狱模式(困难)", hellIntent3),
            ModeEntity(
                "找不同",
                Intent(context, FindDiffColorActivity::class.java)
            ),
            ModeEntity("选图片", Intent(context, SelectPicActivity::class.java))
        )
    }
}