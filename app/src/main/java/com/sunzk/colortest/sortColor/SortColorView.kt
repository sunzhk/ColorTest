package com.sunzk.colortest.sortColor

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sunzk.colortest.entity.HSB
import com.sunzk.demo.tools.ext.dp2px
import org.jetbrains.annotations.TestOnly

@TestOnly
@Preview
@Composable
fun SortColorViewPreview() {
	ConstraintLayout(modifier = Modifier.fillMaxSize()) {
		val (sortColorViewLeft, sortColorViewRight) = createRefs()
		SortColorView.SortColorView(
			modifier = Modifier
				.constrainAs(sortColorViewLeft) {
					top.linkTo(parent.top)
					bottom.linkTo(parent.bottom)
					start.linkTo(parent.start)
					end.linkTo(sortColorViewRight.start)
					height = Dimension.percent(0.3f)
					verticalBias = 0f
				},
			colorArray = testColors(HSB(0f, 40f, 50f), HSB(0f, 70f, 80f), 6),
			divider = 2.5.dp
		)
		SortColorView.SortColorView(
			modifier = Modifier
				.constrainAs(sortColorViewRight) {
					top.linkTo(parent.top)
					bottom.linkTo(parent.bottom)
					start.linkTo(sortColorViewLeft.end)
					end.linkTo(parent.end)
					height = Dimension.percent(0.3f)
					verticalBias = 0f
				},
			colorArray = testColors(HSB(90f, 80f, 60f), HSB(110f, 80f, 90f), 6),
			divider = 2.dp
		)
	}
}

@TestOnly
private fun testColors(start: HSB, end: HSB, number: Int): Array<SortColorData> {
	val colors = Array(number) { index ->
		SortColorData(
			ordinal = index,
			color = if (index == 0) {
				start
			} else if (index == number - 1) {
				end
			} else {
				HSB(
					start.h + (end.h - start.h) * index / (number - 1),
					start.s + (end.s - start.s) * index / (number - 1),
					start.b + (end.b - start.b) * index / (number - 1)
				)
			}
		)
	}
	colors.shuffle()
	return colors
}

object SortColorView {
	
	private const val TAG: String = "SortColorView"

	private val cornerRadius = 5.dp2px.toFloat()
	
	@Composable
	fun SortColorView(modifier: Modifier, 
	                  colorArray: Array<SortColorData>,
	                  orientation: Orientation = Orientation.VERTICAL,
					  divider: Dp = 0.dp) {
		val blockList = ArrayList<SortColorBlockData>()
		Box(modifier.then(Modifier)) {
			Canvas(modifier = Modifier
				.sortColorLayout(orientation, colorArray.size, divider)
			) {
				fillBlockList(colorArray, colorArray.size, blockList, orientation, divider)
				blockList.forEach { (color, offset, size) ->
					val radius = (size.width / 10).coerceAtLeast(cornerRadius)
					drawRoundRect(color = color.composeColor,
					topLeft = offset,
					size = size,
					cornerRadius = CornerRadius(radius, radius))
				}
			}
		}
	}

	/**
	 * 计算组件的尺寸
	 */
	private fun Modifier.sortColorLayout(orientation: Orientation, number: Int, divider: Dp): Modifier {
		return when (orientation) {
			Orientation.HORIZONTAL -> fillMaxWidth().wrapContentHeight()
			Orientation.VERTICAL -> fillMaxHeight().wrapContentWidth()
		}
			.layout { measurable, constraints ->
				val blockSideLength: Int
				val needWidth: Int
				val needHeight: Int
				// 计算所需的宽高
				when (orientation) {
					Orientation.HORIZONTAL -> {
						// 测量子元素
						blockSideLength = ((constraints.maxWidth - (number - 1) * divider.value) / number).toInt()
						needWidth = constraints.maxWidth
						needHeight = blockSideLength
					}
					Orientation.VERTICAL -> {
						blockSideLength = ((constraints.maxHeight - (number - 1) * divider.value) / number).toInt()
						needWidth = blockSideLength
						needHeight = constraints.maxHeight
					}
				}
				// 测量子元素
				val placeable = measurable.measure(Constraints.fixed(needWidth, needHeight))
				Log.d(TAG, "SortColorView#SortColorView- measureWidth=${placeable.width}, measureHeight=${placeable.height}, maxWidth=${constraints.maxWidth}, maxHeight=${constraints.maxHeight}, needWidth=$needWidth, needHeight=$needHeight")
				// 布局子元素
				layout(needWidth, needHeight) {
					placeable.placeRelative((placeable.width - needWidth) / 2, 0)
				}
			}
	}

	private fun DrawScope.fillBlockList(
		colors: Array<SortColorData>,
		number: Int,
		blockList: ArrayList<SortColorBlockData>,
		orientation: Orientation,
		divider: Dp,
	) {
		blockList.clear()
		val blockSideLength = when (orientation) {
			Orientation.HORIZONTAL -> (size.width - (number - 1) * divider.value) / number
			Orientation.VERTICAL -> (size.height - (number - 1) * divider.value) / number
		}
		val startOffset = when (orientation) {
			Orientation.HORIZONTAL -> Offset(0f, (size.height - blockSideLength) / 2)
			Orientation.VERTICAL -> Offset((size.width - blockSideLength) / 2, 0f)
		}
		repeat(number) { index ->
			blockList.add(SortColorBlockData(
				color = colors[index].color,
				offset = when (orientation) {
					Orientation.HORIZONTAL -> Offset(startOffset.x + index * blockSideLength + index * divider.value, startOffset.y)
					Orientation.VERTICAL -> Offset(startOffset.x, startOffset.y + index * blockSideLength + index * divider.value)
				},
				size = Size(blockSideLength, blockSideLength)
			))
		}
	}

	data class SortColorBlockData(
		val color: HSB,
		val offset: Offset,
		val size: Size,
	)

	enum class Orientation {
		HORIZONTAL, VERTICAL
	}
}