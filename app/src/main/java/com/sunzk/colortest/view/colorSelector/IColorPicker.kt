package com.sunzk.colortest.view.colorSelector

import androidx.annotation.UiThread
import com.sunzk.colortest.entity.HSB

/**
 * 颜色选择器通用接口
 */
interface IColorPicker {
	
	val hsb: HSB

	var onColorPick: ((HSB) -> Unit)?
	
	@UiThread
	fun updateHSB(h: Float, s: Float, b: Float)
}