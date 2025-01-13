package com.sunzk.colortest

import com.sunzk.colortest.findDiff.FindDiffColorActivity
import com.sunzk.colortest.intermediateColor.IntermediateColorActivity
import com.sunzk.colortest.mockColor.MockColorActivity
import com.sunzk.colortest.entity.GameMode
import com.sunzk.colortest.entity.ModeEntity

object Constant {

	val BASE_URL_GITHUB = "https://api.codetabs.com/v1/proxy/?quest=https://raw.githubusercontent.com/sunzhk/ColorTest/data/"
	val BASE_URL_MOCK = "http://mock-api.com/rnNrALnl.mock/"

	const val SP_NAME_APP = "AppSetting"
	const val SP_KEY_NIGHT_MODE = "Key_NightMode"
	const val SP_KEY_BGM = "Key_BGM"
	
	val MODE_ENTITY_LIST: Array<ModeEntity>
	const val MODE_SELECT_DATA_NAME = "settings"
	const val MODE_SELECT_DATA_KEY = "modeList"

	init {
		MODE_ENTITY_LIST = arrayOf(
			ModeEntity("标准模式", GameMode.MockColor, MockColorActivity::class.java.name),
			ModeEntity("找中间色", GameMode.IntermediateColor, IntermediateColorActivity::class.java.name),
			ModeEntity("找不同", GameMode.FindDiffColor, FindDiffColorActivity::class.java.name),
//			ModeEntity("选图片", SelectPicActivity::class.java.name)
		)
	}
}