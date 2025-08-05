package com.sunzk.familycontroller.compose.style

import androidx.compose.ui.graphics.Color

object Colors {
	private const val TAG: String = "Colors"

	val colorPrimary = Color(0xFF4285F4)
//	<color name="colorPrimary">#FFF0F0F0</color>
//	<color name="colorPrimaryDark">#FF333333</color>
//
//	<color name="app_base">#FF006396</color>
//	<color name="splash_title">#FFF0F0F0</color>
//	<color name="splash_hint">#FFA0A0A0</color>
//	<color name="common_bg">#FFF0F0F0</color>

	val common_block_bg = Color(0xFFFAFAFA)
	val common_block_stroke = Color(0xFF101010)

	val common_bt_bg: Color
		get() = colorPrimary
	val common_bt_bg_disable = Color(0xFFAAAAAA)
	val common_bt_bg_pressed = Color(0xFFCCCCCC)
	val common_bt_stroke = Color(0xFF101010)
	val common_bt_text = Color(0xFF333333)

	val common_divider = Color(0xFFE0E0E0)

	// <editor-fold desc="文字颜色">
	val common_text_hint = Color(0xFF555555)
	val theme_txt_standard = Color(0xFF333333)
	val theme_txt_white = Color(0xFFF9F9F9)
	// </editor-fold>
}