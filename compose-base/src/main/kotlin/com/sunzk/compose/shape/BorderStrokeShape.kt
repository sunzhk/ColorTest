package com.sunzk.compose.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

fun borderStrokeShape(
	borderWidth: Dp,
	vertical: Boolean,
	horizontal: Boolean
): BorderStrokeShape {
	return BorderStrokeShape(borderWidth, horizontal, vertical, horizontal, vertical)
}

fun borderStrokeShape(
	borderWidth: Dp,
	left: Boolean = true,
	top: Boolean = true,
	right: Boolean = true,
	bottom: Boolean = true
): BorderStrokeShape {
	return BorderStrokeShape(borderWidth, left, top, right, bottom)
}

class BorderStrokeShape(
	private val borderWidth: Dp,
	private val left: Boolean = true,
	private val top: Boolean = true,
	private val right: Boolean = true,
	private val bottom: Boolean = true
) : Shape {

	override fun createOutline(
		size: Size,
		layoutDirection: LayoutDirection,
		density: Density
	): Outline {
		val path = if (left || top || right || bottom) {
			val width = size.width
			val height = size.height
			val borderWidth = borderWidth.value * density.density
			createBorderPath(width, height, borderWidth, left, top, right, bottom)
		} else {
			Path()
		}
		return Outline.Generic(path)
	}

	private fun createBorderPath(
		width: Float,
		height: Float,
		borderWidth: Float,
		left: Boolean,
		top: Boolean,
		right: Boolean,
		bottom: Boolean
	): Path {
		val path = Path()
		if (left) {
			path.addRect(Rect(Offset.Zero, Size(borderWidth, height)))
		}
		if (right) {
			path.addRect(Rect(Offset(width - borderWidth, 0f), Size(borderWidth, height)))
		}
		if (top) {
			path.addRect(Rect(Offset.Zero, Size(width, borderWidth)))
		}
		if (bottom) {
			path.addRect(Rect(Offset(0f, height - borderWidth), Size(width, borderWidth)))
		}
		return path
	}

}