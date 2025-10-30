package com.sunzk.colortest.game.sortColor

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.sunzk.colortest.R
import com.sunzk.demo.tools.ext.dp2px
import kotlinx.coroutines.delay

object SortColorView {
	
	private const val TAG: String = "SortColorView"

	private val cornerRadius = 5.dp2px.toFloat()
	private const val SCALE_START = 1.0f
	private const val SCALE_END = 1.1f
	
	@Composable
	fun SortColorView(modifier: Modifier,
	                  colorArray: Array<SortColorData>,
	                  orientation: Orientation = Orientation.VERTICAL,
	                  divider: Dp = 0.dp,
	                  showResult: MutableState<Boolean>,
	                  showResultAnim: ShowResultAnim = ShowResultAnim(),
	                  onBoxDrag: ((from: Int, to: Int) -> Unit)? = null,
	                  onShowResultAnim: ((finish: Boolean) -> Unit)? = null) {
		val rightImage = ImageBitmap.imageResource(R.mipmap.icon_sort_color_right)
		val wrongImage = ImageBitmap.imageResource(R.mipmap.icon_sort_color_wrong)

		val zIndex = remember { mutableFloatStateOf(1f) }
		val touchBoxInfo = remember { mutableStateOf<TouchBoxInfo?>(null) }
		val blockList = ArrayList<SortColorBlockData>()

		val animatedArray = createBlockScaleAnim(colorArray, showResult, showResultAnim, onShowResultAnim)
		Box(modifier.then(Modifier.zIndex(zIndex.floatValue))) {
			Canvas(modifier = Modifier
				.sortColorLayout(orientation, colorArray.size, divider)
				.pointerInput(Unit) {
					handlePointerInputEvent(showResult, zIndex, touchBoxInfo, blockList, onBoxDrag)
				}
			) {
				fillBlockList(colorArray, colorArray.size, blockList, orientation, divider)
				drawColorBlockArray(blockList, animatedArray, rightImage, wrongImage)
				touchBoxInfo.value?.takeIf { it.offset != Offset.Zero }?.let { (offset, originalBoxInfo) ->
					drawDragColorBlock(offset, originalBoxInfo)
				}
			}
		}
	}

	// <editor-fold desc="显示结果动画">
	@Composable
	private fun createBlockScaleAnim(
		colorArray: Array<SortColorData>,
		showResult: MutableState<Boolean>,
		showResultAnim: ShowResultAnim,
		onShowResultAnim: ((finish: Boolean) -> Unit)?
	): Array<State<Float>> {
		var animState by remember { mutableStateOf(false) }
		val scaleArray = Array(colorArray.size) { remember { mutableFloatStateOf(showResultAnim.scaleStart) } }
		LaunchedEffect(showResult.value) {
			Log.d(TAG, "SortColorView#SortColorView-scale-anim showResult=${showResult.value}, animState=$animState")
			if (animState || !showResult.value) {
				return@LaunchedEffect
			}
			animState = true
			Log.d(TAG, "SortColorView#SortColorView-scale-anim anim to $SCALE_END")
			val interval = (showResultAnim.allAnimDuration - showResultAnim.singleAnimDuration) / (colorArray.size - 1)
			onShowResultAnim?.invoke(false)
			repeat(colorArray.size) { index ->
				if (index > 0) {
					delay(interval.toLong())
				}
				scaleArray[index].floatValue = showResultAnim.scaleEnd
			}
		}
		return Array(colorArray.size) { index ->
			val scale = scaleArray[index]
			animateFloatAsState(
				targetValue = scale.floatValue,
				animationSpec = tween(showResultAnim.singleAnimDuration / 2),
				finishedListener = {
					if (scale.floatValue == SCALE_END) {
						Log.d(TAG, "SortColorView#SortColorView-scale-anim anim to 1")
						scale.floatValue = SCALE_START
						colorArray[index] = colorArray[index].copy(showResult = true)
					} else if (index == (colorArray.size - 1) && scale.floatValue == SCALE_START) {
						Log.d(TAG, "SortColorView#SortColorView-scale-anim anim finish")
						animState = false
						onShowResultAnim?.invoke(true)
					} else {
//						scale.floatValue = SCALE_END
					}
				}
			)
		}
	}

	data class ShowResultAnim(
		val singleAnimDuration: Int = 600,
		val allAnimDuration: Int = 1800,
		val scaleStart: Float = SCALE_START,
		val scaleEnd: Float = SCALE_END
	)

	// </editor-fold>

	// <editor-fold desc="触摸事件处理">
	
	private suspend fun PointerInputScope.handlePointerInputEvent(
		showResult: State<Boolean>,
		zIndex: MutableFloatState,
		touchBoxInfo: MutableState<TouchBoxInfo?>,
		blockList: ArrayList<SortColorBlockData>,
		onBoxDrag: ((from: Int, to: Int) -> Unit)?
	) {
		detectDragGestures(
			onDragStart = { offset: Offset ->
				Log.d(TAG, "SortColorView#SortColorView- onDragStart offset=$offset")
				if (showResult.value) {
					return@detectDragGestures
				}
				zIndex.floatValue = 10f
				findDragBox(offset, blockList, touchBoxInfo)
			},
			onDrag = { change, dragAmount ->
				Log.d(TAG, "SortColorView#SortColorView- onDrag dragAmount=$dragAmount")
				if (showResult.value) {
					return@detectDragGestures
				}
				handleBoxDrag(change, dragAmount, touchBoxInfo)
			},
			onDragEnd = {
				Log.d(TAG, "SortColorView#SortColorView- onDragEnd")
				if (showResult.value) {
					return@detectDragGestures
				}
				zIndex.floatValue = 1f
				handleBoxDragEnd(touchBoxInfo, blockList, onBoxDrag)
				touchBoxInfo.value = null
			},
			onDragCancel = {
				Log.d(TAG, "SortColorView#SortColorView- onDragCancel")
				if (showResult.value) {
					return@detectDragGestures
				}
				zIndex.floatValue = 1f
				touchBoxInfo.value = null
			}
		)
	}

	private fun findDragBox(offset: Offset, blockList: ArrayList<SortColorBlockData>, touchBoxInfo: MutableState<TouchBoxInfo?>) {
		blockList.find { block -> block.color.canMove && block.checkInBlock(offset) }
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
			block.color.canMove && block.checkAroundBlock(info.offset)
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
				color = colors[index],
				offset = when (orientation) {
					Orientation.HORIZONTAL -> Offset(startOffset.x + index * blockSideLength + index * divider.value, startOffset.y)
					Orientation.VERTICAL -> Offset(startOffset.x, startOffset.y + index * blockSideLength + index * divider.value)
				},
				size = Size(blockSideLength, blockSideLength),
			))
		}
	}

	// </editor-fold>
	
	// <editor-fold desc="实际绘制">
	/**
	 * 绘制色块
	 */
	private fun DrawScope.drawColorBlockArray(
		blockList: ArrayList<SortColorBlockData>,
		animatedScale: Array<State<Float>>,
		rightImage: ImageBitmap,
		wrongImage: ImageBitmap) {
		val iconSize = IntSize((blockList[0].size.width / 3f).toInt(), (blockList[0].size.height / 3f).toInt())
		val iconOffsetDiff = IntOffset((((blockList[0].size.width - iconSize.width) / 2).toInt()), (((blockList[0].size.height - iconSize.height) / 2).toInt()))
		blockList.forEachIndexed { index, (color, offset, size) ->
			val radius = (size.width / 10).coerceAtLeast(cornerRadius)
			val scale = animatedScale[index].value
			drawRoundRect(color = color.color.composeColor,
				topLeft = offset - Offset(size.width * (scale - 1) / 2, size.height * (scale - 1) / 2),
				size = size * (scale),
				cornerRadius = CornerRadius(radius, radius))
			if (color.showResult && color.ordinal != index) {
//				this.drawImage(if (color.ordinal == index) rightImage else wrongImage,
				this.drawImage(wrongImage,
						dstOffset = IntOffset((offset.x + iconOffsetDiff.x).toInt(),
					(offset.y + iconOffsetDiff.y).toInt()),
					dstSize = iconSize)
			}
		}
	}
	
	private fun DrawScope.drawDragColorBlock(dragOffset: Offset, originalBoxInfo: SortColorBlockData) {
		val radius = (originalBoxInfo.size.width / 10).coerceAtLeast(cornerRadius)
		// 先绘制色块
		drawRoundRect(color = originalBoxInfo.color.color.composeColor,
			topLeft = dragOffset,
			size = originalBoxInfo.size,
			cornerRadius = CornerRadius(radius, radius))
		// 然后绘制连接线
		val from = Offset(originalBoxInfo.offset.x + originalBoxInfo.size.width / 2, originalBoxInfo.offset.y + originalBoxInfo.size.height / 2)
		val to = Offset(dragOffset.x + originalBoxInfo.size.width / 2, dragOffset.y + originalBoxInfo.size.height / 2)
		val maxWidth = (originalBoxInfo.size.width / 6).coerceAtLeast(8.dp.toPx())
		// 从from到to，绘制一条两端粗中间细的线
		drawLine(color = originalBoxInfo.color.color.composeColor, start = from, end = to, strokeWidth = maxWidth)
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