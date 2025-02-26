package com.sunzk.colortest.view.colorPicker

import android.content.Context

enum class ColorPickerType(val isDefault: Boolean, val text: String, val creator: (Context) -> IColorPicker) {
	HSBProgress(true, "滑轨", { HSBColorPicker(it) }),
	ColorWheel(false, "色轮", { ColorWheelColorPicker(it) })
}