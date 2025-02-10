package com.sunzk.colortest.compose.ui.common

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import kotlin.math.max
import kotlin.math.min

@Composable
fun CustomDropdownMenu(expanded: Boolean,
                 onDismissRequest: () -> Unit,
                 modifier: Modifier,
                 properties: PopupProperties = PopupProperties(focusable = true),
                 content: @Composable ColumnScope.() -> Unit) {
	val expandedState = remember { MutableTransitionState(false) }
	expandedState.targetState = expanded
	if (expandedState.currentState || expandedState.targetState) {
		val offset = DpOffset(0.dp, 0.dp)
		val density = LocalDensity.current
		val transformOriginState = remember { mutableStateOf(TransformOrigin.Center) }
		val popupPositionProvider =
			remember(offset, density) {
				DropdownMenuPositionProvider(offset, density) { parentBounds, menuBounds ->
					transformOriginState.value = calculateTransformOrigin(parentBounds, menuBounds)
				}
			}
		Popup(
			popupPositionProvider = popupPositionProvider,
			onDismissRequest = onDismissRequest,
			properties = properties
		) {
			DropdownMenuContent(modifier, expandedState, transformOriginState, rememberScrollState(), content)
		}
	}
}

@Composable
private fun DropdownMenuContent(
	modifier: Modifier,
	expandedState: MutableTransitionState<Boolean>,
	transformOriginState: MutableState<TransformOrigin>,
	scrollState: ScrollState,
	content: @Composable ColumnScope.() -> Unit
) {
	// Menu open/close animation.
	@Suppress("DEPRECATION") val transition = updateTransition(expandedState, "DropDownMenu")

	val scale by
	transition.animateFloat(
		transitionSpec = {
			if (false isTransitioningTo true) {
				// Dismissed to expanded
				tween(durationMillis = InTransitionDuration, easing = LinearOutSlowInEasing)
			} else {
				// Expanded to dismissed.
				tween(durationMillis = 1, delayMillis = OutTransitionDuration - 1)
			}
		}
	) { expanded ->
		if (expanded) ExpandedScaleTarget else ClosedScaleTarget
	}

	val alpha by
	transition.animateFloat(
		transitionSpec = {
			if (false isTransitioningTo true) {
				// Dismissed to expanded
				tween(durationMillis = 30)
			} else {
				// Expanded to dismissed.
				tween(durationMillis = OutTransitionDuration)
			}
		}
	) { expanded ->
		if (expanded) ExpandedAlphaTarget else ClosedAlphaTarget
	}

	val isInspecting = LocalInspectionMode.current
	Surface(
		color = Color.Transparent,
		modifier =
		Modifier.graphicsLayer {
			scaleX =
				if (!isInspecting) scale
				else if (expandedState.targetState) ExpandedScaleTarget else ClosedScaleTarget
			scaleY =
				if (!isInspecting) scale
				else if (expandedState.targetState) ExpandedScaleTarget else ClosedScaleTarget
			this.alpha =
				if (!isInspecting) alpha
				else if (expandedState.targetState) ExpandedAlphaTarget else ClosedAlphaTarget
			transformOrigin = transformOriginState.value
		}
	) {
		Column(
			modifier =
			modifier
				.verticalScroll(scrollState),
			content = content
		)
	}
}

// <editor-fold desc="从androidx.compose.material3里复制来的核心代码">

private val MenuVerticalMargin = 48.dp

private const val InTransitionDuration = 120
private const val OutTransitionDuration = 75
private const val ExpandedScaleTarget = 1f
private const val ClosedScaleTarget = 0.8f
private const val ExpandedAlphaTarget = 1f
private const val ClosedAlphaTarget = 0f

@Stable
private object MenuPosition {
	/**
	 * An interface to calculate the vertical position of a menu with respect to its anchor and
	 * window. The returned y-coordinate is relative to the window.
	 *
	 * @see PopupPositionProvider
	 */
	@Stable
	fun interface Vertical {
		fun position(
			anchorBounds: IntRect,
			windowSize: IntSize,
			menuHeight: Int,
		): Int
	}

	/**
	 * An interface to calculate the horizontal position of a menu with respect to its anchor,
	 * window, and layout direction. The returned x-coordinate is relative to the window.
	 *
	 * @see PopupPositionProvider
	 */
	@Stable
	fun interface Horizontal {
		fun position(
			anchorBounds: IntRect,
			windowSize: IntSize,
			menuWidth: Int,
			layoutDirection: LayoutDirection,
		): Int
	}

	/**
	 * Returns a [MenuPosition.Horizontal] which aligns the start of the menu to the start of the
	 * anchor.
	 *
	 * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
	 * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
	 */
	fun startToAnchorStart(offset: Int = 0): Horizontal =
		AnchorAlignmentOffsetPosition.Horizontal(
			menuAlignment = Alignment.Start,
			anchorAlignment = Alignment.Start,
			offset = offset,
		)

	/**
	 * Returns a [MenuPosition.Horizontal] which aligns the end of the menu to the end of the
	 * anchor.
	 *
	 * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
	 * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
	 */
	fun endToAnchorEnd(offset: Int = 0): Horizontal =
		AnchorAlignmentOffsetPosition.Horizontal(
			menuAlignment = Alignment.End,
			anchorAlignment = Alignment.End,
			offset = offset,
		)

	/**
	 * Returns a [MenuPosition.Horizontal] which aligns the left of the menu to the left of the
	 * window.
	 *
	 * The resulting x position will be coerced so that the menu remains within the area inside the
	 * given [margin] from the left and right edges of the window.
	 */
	fun leftToWindowLeft(margin: Int = 0): Horizontal =
		WindowAlignmentMarginPosition.Horizontal(
			alignment = AbsoluteAlignment.Left,
			margin = margin,
		)

	/**
	 * Returns a [MenuPosition.Horizontal] which aligns the right of the menu to the right of the
	 * window.
	 *
	 * The resulting x position will be coerced so that the menu remains within the area inside the
	 * given [margin] from the left and right edges of the window.
	 */
	fun rightToWindowRight(margin: Int = 0): Horizontal =
		WindowAlignmentMarginPosition.Horizontal(
			alignment = AbsoluteAlignment.Right,
			margin = margin,
		)

	/**
	 * Returns a [MenuPosition.Vertical] which aligns the top of the menu to the bottom of the
	 * anchor.
	 */
	fun topToAnchorBottom(offset: Int = 0): Vertical =
		AnchorAlignmentOffsetPosition.Vertical(
			menuAlignment = Alignment.Top,
			anchorAlignment = Alignment.Bottom,
			offset = offset,
		)

	/**
	 * Returns a [MenuPosition.Vertical] which aligns the bottom of the menu to the top of the
	 * anchor.
	 */
	fun bottomToAnchorTop(offset: Int = 0): Vertical =
		AnchorAlignmentOffsetPosition.Vertical(
			menuAlignment = Alignment.Bottom,
			anchorAlignment = Alignment.Top,
			offset = offset,
		)

	/**
	 * Returns a [MenuPosition.Vertical] which aligns the center of the menu to the top of the
	 * anchor.
	 */
	fun centerToAnchorTop(offset: Int = 0): Vertical =
		AnchorAlignmentOffsetPosition.Vertical(
			menuAlignment = Alignment.CenterVertically,
			anchorAlignment = Alignment.Top,
			offset = offset,
		)

	/**
	 * Returns a [MenuPosition.Vertical] which aligns the top of the menu to the top of the window.
	 *
	 * The resulting y position will be coerced so that the menu remains within the area inside the
	 * given [margin] from the top and bottom edges of the window.
	 */
	fun topToWindowTop(margin: Int = 0): Vertical =
		WindowAlignmentMarginPosition.Vertical(
			alignment = Alignment.Top,
			margin = margin,
		)

	/**
	 * Returns a [MenuPosition.Vertical] which aligns the bottom of the menu to the bottom of the
	 * window.
	 *
	 * The resulting y position will be coerced so that the menu remains within the area inside the
	 * given [margin] from the top and bottom edges of the window.
	 */
	fun bottomToWindowBottom(margin: Int = 0): Vertical =
		WindowAlignmentMarginPosition.Vertical(
			alignment = Alignment.Bottom,
			margin = margin,
		)
}

@Immutable
private object AnchorAlignmentOffsetPosition {
	/**
	 * A [MenuPosition.Horizontal] which horizontally aligns the given [menuAlignment] with the
	 * given [anchorAlignment].
	 *
	 * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
	 * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
	 */
	@Immutable
	data class Horizontal(
		private val menuAlignment: Alignment.Horizontal,
		private val anchorAlignment: Alignment.Horizontal,
		private val offset: Int,
	) : MenuPosition.Horizontal {
		override fun position(
			anchorBounds: IntRect,
			windowSize: IntSize,
			menuWidth: Int,
			layoutDirection: LayoutDirection,
		): Int {
			val anchorAlignmentOffset =
				anchorAlignment.align(
					size = 0,
					space = anchorBounds.width,
					layoutDirection = layoutDirection,
				)
			val menuAlignmentOffset =
				-menuAlignment.align(
					size = 0,
					space = menuWidth,
					layoutDirection,
				)
			val resolvedOffset = if (layoutDirection == LayoutDirection.Ltr) offset else -offset
			return anchorBounds.left + anchorAlignmentOffset + menuAlignmentOffset + resolvedOffset
		}
	}

	/**
	 * A [MenuPosition.Vertical] which vertically aligns the given [menuAlignment] with the given
	 * [anchorAlignment].
	 */
	@Immutable
	data class Vertical(
		private val menuAlignment: Alignment.Vertical,
		private val anchorAlignment: Alignment.Vertical,
		private val offset: Int,
	) : MenuPosition.Vertical {
		override fun position(
			anchorBounds: IntRect,
			windowSize: IntSize,
			menuHeight: Int,
		): Int {
			val anchorAlignmentOffset =
				anchorAlignment.align(
					size = 0,
					space = anchorBounds.height,
				)
			val menuAlignmentOffset =
				-menuAlignment.align(
					size = 0,
					space = menuHeight,
				)
			return anchorBounds.top + anchorAlignmentOffset + menuAlignmentOffset + offset
		}
	}
}

@Immutable
private object WindowAlignmentMarginPosition {
	/**
	 * A [MenuPosition.Horizontal] which horizontally aligns the menu within the window according to
	 * the given [alignment].
	 *
	 * The resulting x position will be coerced so that the menu remains within the area inside the
	 * given [margin] from the left and right edges of the window. If this is not possible, i.e.,
	 * the menu is too wide, then it is centered horizontally instead.
	 */
	@Immutable
	data class Horizontal(
		private val alignment: Alignment.Horizontal,
		private val margin: Int,
	) : MenuPosition.Horizontal {
		override fun position(
			anchorBounds: IntRect,
			windowSize: IntSize,
			menuWidth: Int,
			layoutDirection: LayoutDirection,
		): Int {
			if (menuWidth >= windowSize.width - 2 * margin) {
				return Alignment.CenterHorizontally.align(
					size = menuWidth,
					space = windowSize.width,
					layoutDirection = layoutDirection,
				)
			}
			val x =
				alignment.align(
					size = menuWidth,
					space = windowSize.width,
					layoutDirection = layoutDirection,
				)
			return x.coerceIn(margin, windowSize.width - margin - menuWidth)
		}
	}

	/**
	 * A [MenuPosition.Vertical] which vertically aligns the menu within the window according to the
	 * given [alignment].
	 *
	 * The resulting y position will be coerced so that the menu remains within the area inside the
	 * given [margin] from the top and bottom edges of the window. If this is not possible, i.e.,
	 * the menu is too tall, then it is centered vertically instead.
	 */
	@Immutable
	data class Vertical(
		private val alignment: Alignment.Vertical,
		private val margin: Int,
	) : MenuPosition.Vertical {
		override fun position(
			anchorBounds: IntRect,
			windowSize: IntSize,
			menuHeight: Int,
		): Int {
			if (menuHeight >= windowSize.height - 2 * margin) {
				return Alignment.CenterVertically.align(
					size = menuHeight,
					space = windowSize.height,
				)
			}
			val y =
				alignment.align(
					size = menuHeight,
					space = windowSize.height,
				)
			return y.coerceIn(margin, windowSize.height - margin - menuHeight)
		}
	}
}

/** Calculates the position of a Material [DropdownMenu]. */
@Immutable
private data class DropdownMenuPositionProvider(
	val contentOffset: DpOffset,
	val density: Density,
	val verticalMargin: Int = with(density) { MenuVerticalMargin.roundToPx() },
	val onPositionCalculated: (anchorBounds: IntRect, menuBounds: IntRect) -> Unit = { _, _ -> }
) : PopupPositionProvider {
	// Horizontal position
	private val startToAnchorStart: MenuPosition.Horizontal
	private val endToAnchorEnd: MenuPosition.Horizontal
	private val leftToWindowLeft: MenuPosition.Horizontal
	private val rightToWindowRight: MenuPosition.Horizontal
	// Vertical position
	private val topToAnchorBottom: MenuPosition.Vertical
	private val bottomToAnchorTop: MenuPosition.Vertical
	private val centerToAnchorTop: MenuPosition.Vertical
	private val topToWindowTop: MenuPosition.Vertical
	private val bottomToWindowBottom: MenuPosition.Vertical

	init {
		// Horizontal position
		val contentOffsetX = with(density) { contentOffset.x.roundToPx() }
		startToAnchorStart = MenuPosition.startToAnchorStart(offset = contentOffsetX)
		endToAnchorEnd = MenuPosition.endToAnchorEnd(offset = contentOffsetX)
		leftToWindowLeft = MenuPosition.leftToWindowLeft(margin = 0)
		rightToWindowRight = MenuPosition.rightToWindowRight(margin = 0)
		// Vertical position
		val contentOffsetY = with(density) { contentOffset.y.roundToPx() }
		topToAnchorBottom = MenuPosition.topToAnchorBottom(offset = contentOffsetY)
		bottomToAnchorTop = MenuPosition.bottomToAnchorTop(offset = contentOffsetY)
		centerToAnchorTop = MenuPosition.centerToAnchorTop(offset = contentOffsetY)
		topToWindowTop = MenuPosition.topToWindowTop(margin = verticalMargin)
		bottomToWindowBottom = MenuPosition.bottomToWindowBottom(margin = verticalMargin)
	}

	override fun calculatePosition(
		anchorBounds: IntRect,
		windowSize: IntSize,
		layoutDirection: LayoutDirection,
		popupContentSize: IntSize
	): IntOffset {
		val xCandidates =
			listOf(
				startToAnchorStart,
				endToAnchorEnd,
				if (anchorBounds.center.x < windowSize.width / 2) {
					leftToWindowLeft
				} else {
					rightToWindowRight
				}
			)
		var x = 0
		for (index in xCandidates.indices) {
			val xCandidate =
				xCandidates[index].position(
					anchorBounds = anchorBounds,
					windowSize = windowSize,
					menuWidth = popupContentSize.width,
					layoutDirection = layoutDirection
				)
			if (
				index == xCandidates.lastIndex ||
				(xCandidate >= 0 && xCandidate + popupContentSize.width <= windowSize.width)
			) {
				x = xCandidate
				break
			}
		}

		val yCandidates =
			listOf(
				topToAnchorBottom,
				bottomToAnchorTop,
				centerToAnchorTop,
				if (anchorBounds.center.y < windowSize.height / 2) {
					topToWindowTop
				} else {
					bottomToWindowBottom
				}
			)
		var y = 0
		for (index in yCandidates.indices) {
			val yCandidate =
				yCandidates[index].position(
					anchorBounds = anchorBounds,
					windowSize = windowSize,
					menuHeight = popupContentSize.height
				)
			if (
				index == yCandidates.lastIndex ||
				(yCandidate >= verticalMargin &&
						yCandidate + popupContentSize.height <= windowSize.height - verticalMargin)
			) {
				y = yCandidate
				break
			}
		}

		val menuOffset = IntOffset(x, y)
		onPositionCalculated(
			/* anchorBounds = */ anchorBounds,
			/* menuBounds = */ IntRect(offset = menuOffset, size = popupContentSize)
		)
		return menuOffset
	}
}

private fun calculateTransformOrigin(anchorBounds: IntRect, menuBounds: IntRect): TransformOrigin {
	val pivotX =
		when {
			menuBounds.left >= anchorBounds.right -> 0f
			menuBounds.right <= anchorBounds.left -> 1f
			menuBounds.width == 0 -> 0f
			else -> {
				val intersectionCenter =
					(max(anchorBounds.left, menuBounds.left) +
							min(anchorBounds.right, menuBounds.right)) / 2
				(intersectionCenter - menuBounds.left).toFloat() / menuBounds.width
			}
		}
	val pivotY =
		when {
			menuBounds.top >= anchorBounds.bottom -> 0f
			menuBounds.bottom <= anchorBounds.top -> 1f
			menuBounds.height == 0 -> 0f
			else -> {
				val intersectionCenter =
					(max(anchorBounds.top, menuBounds.top) +
							min(anchorBounds.bottom, menuBounds.bottom)) / 2
				(intersectionCenter - menuBounds.top).toFloat() / menuBounds.height
			}
		}
	return TransformOrigin(pivotX, pivotY)
}

// </editor-fold>