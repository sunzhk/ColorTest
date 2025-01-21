package com.sunzk.colortest.sortColor

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.sunzk.demo.tools.ext.dp2px

object SortColorView {
	
	private const val TAG: String = "SortColorView"

	private val cornerRadius = 5.dp2px.toFloat()
	
	@Composable
	fun SortColorView(modifier: Modifier, 
	                  colorArray: Array<SortColorData>,
	                  orientation: Orientation = Orientation.VERTICAL,
					  divider: Dp = 0.dp,
					  onBoxDrag: ((from: Int, to: Int) -> Unit)? = null) {
		val zIndex = remember { mutableFloatStateOf(1f) }
		val touchBoxInfo = remember { mutableStateOf<TouchBoxInfo?>(null) }
		val blockList = ArrayList<SortColorBlockData>()
		Box(modifier.then(Modifier.zIndex(zIndex.floatValue))) {
			Canvas(modifier = Modifier
				.sortColorLayout(orientation, colorArray.size, divider)
				.pointerInput(Unit) {
					handlePointerInputEvent(zIndex, touchBoxInfo, blockList, onBoxDrag)
				}
			) {
				fillBlockList(colorArray, colorArray.size, blockList, orientation, divider)
				drawColorBlockArray(blockList)
				touchBoxInfo.value?.takeIf { it.offset != Offset.Zero }?.let { (offset, originalBoxInfo) ->
					drawDragColorBlock(offset, originalBoxInfo)
				}
			}
		}
	}
	
	// <editor-fold desc="触摸事件处理">
	
	private suspend fun PointerInputScope.handlePointerInputEvent(
		zIndex: MutableFloatState,
		touchBoxInfo: MutableState<TouchBoxInfo?>,
		blockList: ArrayList<SortColorBlockData>,
		onBoxDrag: ((from: Int, to: Int) -> Unit)?
	) {
		detectDragGestures(
			onDragStart = { offset: Offset ->
				Log.d(TAG, "SortColorView#SortColorView- onDragStart offset=$offset")
				zIndex.floatValue = 10f
				findDragBox(offset, blockList, touchBoxInfo)
			},
			onDrag = { change, dragAmount ->
				Log.d(TAG, "SortColorView#SortColorView- onDrag dragAmount=$dragAmount")
				handleBoxDrag(change, dragAmount, touchBoxInfo)
			},
			onDragEnd = {
				Log.d(TAG, "SortColorView#SortColorView- onDragEnd")
				zIndex.floatValue = 1f
				handleBoxDragEnd(touchBoxInfo, blockList, onBoxDrag)
				touchBoxInfo.value = null
			},
			onDragCancel = {
				Log.d(TAG, "SortColorView#SortColorView- onDragCancel")
				zIndex.floatValue = 1f
				touchBoxInfo.value = null
			}
		)
	}

	private fun findDragBox(offset: Offset, blockList: ArrayList<SortColorBlockData>, touchBoxInfo: MutableState<TouchBoxInfo?>) {
		blockList.find { block -> block.checkInBlock(offset) }
			?.let { block ->
				Log.d(TAG, "SortColorView#findDragBox- find block=$block")
				touchBoxInfo.value = TouchBoxInfo(block.offset.copy(), block)
			}
	}
	
	private fun handleBoxDrag(
		change: PointerInputChange,
		dragAmount: Offset,
		touchBoxInfo: MutableState<TouchBoxInfo?>,
	) {
		touchBoxInfo.value?.let { info ->
			change.consume()
			touchBoxInfo.value = info.copy(offset = info.offset + dragAmount)
			Log.d(TAG, "SortColorView#handleBoxDrag- touch offset update to ${touchBoxInfo.value?.offset}")
		}
	}
	
	private fun handleBoxDragEnd(touchBoxInfo: MutableState<TouchBoxInfo?>,
	                             blockList: ArrayList<SortColorBlockData>,
	                             onBoxDrag: ((from: Int, to: Int) -> Unit)?) {
		if (onBoxDrag == null) {
			Log.d(TAG, "SortColorView#handleBoxDragEnd- no listener")
			return
		}
		val info = touchBoxInfo.value
		if (info == null) {
			Log.d(TAG, "SortColorView#handleBoxDragEnd- no info")
			return
		}
		blockList.indexOfFirst { block ->
			block.checkAroundBlock(info.offset)
		}
			.takeIf { it >= 0 }
			?.let { toBox ->
				val from = blockList.indexOf(info.originalBlock)
				Log.d(TAG, "SortColorView#handleBoxDragEnd- drag from $from to $toBox")
				onBoxDrag(from, toBox)
			}
	}

	// </editor-fold>
	
	// <editor-fold desc="色块绘制参数计算">

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

	/**
	 * 计算填充每块的数据
	 */
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

	// </editor-fold>
	
	// <editor-fold desc="实际绘制">
	/**
	 * 绘制色块
	 */
	private fun DrawScope.drawColorBlockArray(blockList: ArrayList<SortColorBlockData>) {
		blockList.forEach { (color, offset, size) ->
			val radius = (size.width / 10).coerceAtLeast(cornerRadius)
			drawRoundRect(color = color.composeColor,
				topLeft = offset,
				size = size,
				cornerRadius = CornerRadius(radius, radius))
		}
	}
	
	private fun DrawScope.drawDragColorBlock(dragOffset: Offset, originalBoxInfo: SortColorBlockData) {
		val radius = (originalBoxInfo.size.width / 10).coerceAtLeast(cornerRadius)
		// 先绘制色块
		drawRoundRect(color = originalBoxInfo.color.composeColor,
			topLeft = dragOffset,
			size = originalBoxInfo.size,
			cornerRadius = CornerRadius(radius, radius))
		// 然后绘制连接线
		val from = Offset(originalBoxInfo.offset.x + originalBoxInfo.size.width / 2, originalBoxInfo.offset.y + originalBoxInfo.size.height / 2)
		val to = Offset(dragOffset.x + originalBoxInfo.size.width / 2, dragOffset.y + originalBoxInfo.size.height / 2)
		val maxWidth = (originalBoxInfo.size.width / 6).coerceAtLeast(8.dp.toPx())
		val minWidth = maxWidth / 2
		// 从from到to，绘制一条两端粗中间细的线
		drawLine(color = originalBoxInfo.color.composeColor, start = from, end = to, strokeWidth = maxWidth)
	}
	// </editor-fold>

	/**
	 * 排序方向
	 */
	enum class Orientation {
		HORIZONTAL, VERTICAL
	}

	/**
	 * 拖拽时被拖拽的色块数据
	 */
	private data class TouchBoxInfo(
		val offset: Offset,
		val originalBlock: SortColorBlockData
	)
}