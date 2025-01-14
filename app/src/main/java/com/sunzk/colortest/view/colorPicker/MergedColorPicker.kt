package com.sunzk.colortest.view.colorPicker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.sunzk.base.expand.collect
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.entity.HSB
import kotlinx.coroutines.Job

class MergedColorPicker: FrameLayout, IColorPicker {
	companion object {
		private const val TAG: String = "MergedColorPicker"
	}

	// <editor-fold desc="构造方法">
	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet?) : super(
		context,
		attrs
	)

	constructor(
		context: Context,
		attrs: AttributeSet?,
		defStyleAttr: Int
	) : super(context, attrs, defStyleAttr)

	constructor(
		context: Context,
		attrs: AttributeSet?,
		defStyleAttr: Int,
		defStyleRes: Int
	) : super(context, attrs, defStyleAttr, defStyleRes)
	// </editor-fold>

	override val pickerView: View
		get() = this
	override val hsb: HSB
		get() = currentColorPicker.hsb
	override var onColorPick: ((HSB) -> Unit)? = null

	private val colorPickerMap = HashMap<ColorPickerType, IColorPicker>()
	private val currentColorPicker: IColorPicker
		get() = colorPickerMap[Runtime.colorPickerType.value]!!

	private var lifecycleCollector: Job? = null

	init {
		ColorPickerType.entries.forEach { type ->
			type.creator(context).let { colorPicker ->
				colorPickerMap[type] = colorPicker
				colorPicker.onColorPick = { hsb ->
					onColorPick?.invoke(hsb)
				}
				colorPicker.pickerView.visibility = if (type == Runtime.colorPickerType.value) View.VISIBLE else View.GONE
				addView(colorPicker.pickerView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
			}
		}
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		lifecycleCollector?.cancel()
		findViewTreeLifecycleOwner()?.lifecycleScope?.let { lifecycleScope ->
			lifecycleCollector = Runtime.colorPickerType.collect(lifecycleScope) { currentColorPickerType ->
				selectColorPicker(currentColorPickerType)
			}
		}
	}
	
	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		lifecycleCollector?.cancel()
	}

	override fun updateHSB(h: Float, s: Float, b: Float) {
		colorPickerMap.values.forEach { it.updateHSB(h, s, b) }
	}

	private fun selectColorPicker(selectedType: ColorPickerType) {
		colorPickerMap.forEach { (type, colorPicker) -> colorPicker.pickerView.visibility = if (type == selectedType) View.VISIBLE else View.GONE }
	}
}