package com.sunzk.colortest.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.sunzk.base.expand.onClick
import com.sunzk.base.utils.DisplayUtil
import com.sunzk.colortest.R
import com.sunzk.colortest.entity.HSB
import com.sunzk.demo.tools.ext.dp2px
import java.util.*

class FindDiffView : FrameLayout {

	companion object {
		private const val TAG = "FindDiffView"
		private const val CORNER_RADIUS = 5
	}

	var needSpacing: Boolean = true
		set(value) {
			field = value
			resetCount(countPerSide, true)
		}
	private var spacing = 0
	private lateinit var colorViewMap: Array<Array<CustomItemView?>>
	private val viewArrayList = ArrayList<CustomItemView?>()
	var countPerSide = -1
		private set
	private var diffIndex = 0
	private var onDiffColorViewClickListener: ((view: View?, result: Boolean) -> Unit?)? = null
	private var waitForLayout = false
	private var tempBaseColor: HSB? = null
	private var tempDiffColor: HSB? = null
	private var resultStrokeColor = 0
	var isShowingResult = false
		private set
	
	private val itemBorderColor = resources.getColor(R.color.common_bt_stroke)

	constructor(context: Context) : super(context) {
		init()
	}

	constructor(
		context: Context,
		attrs: AttributeSet?,
	) : super(context, attrs) {
		init()
	}

	constructor(
		context: Context,
		attrs: AttributeSet?,
		defStyleAttr: Int,
	) : super(context, attrs, defStyleAttr) {
		init()
	}

	constructor(
		context: Context,
		attrs: AttributeSet?,
		defStyleAttr: Int,
		defStyleRes: Int,
	) : super(context, attrs, defStyleAttr, defStyleRes) {
		init()
	}

	private fun init() {
		resultStrokeColor = resources.getColor(R.color.text_default_dark)
	}

	override fun onLayout(
		changed: Boolean,
		left: Int,
		top: Int,
		right: Int,
		bottom: Int,
	) {
		super.onLayout(changed, left, top, right, bottom)
		if (changed && waitForLayout) {
			post {
				resetCount(countPerSide, true)
				if (tempBaseColor != null && tempDiffColor != null) {
					resetColor(tempBaseColor!!, tempDiffColor!!, diffIndex)
				}
			}
		}
	}

	private fun calSpacing(context: Context, countPerSide: Int): Int {
		return if (needSpacing) {
			DisplayUtil.dip2px(context, 3 + 3f / countPerSide)
		} else 0
	}

	/**
	 * 修改正方形的数量
	 * @param countPerSide 每边的正方形的数量
	 */
	@JvmOverloads
	fun resetCount(countPerSide: Int, force: Boolean = false) {
		Log.d(TAG, "resetCount: ${this.countPerSide}, $countPerSide, $force")
		if (this.countPerSide == countPerSide && !force) {
			return
		}
		this.countPerSide = countPerSide
		val viewSideLength = calViewSideLength(countPerSide)
		if (viewSideLength <= 0) {
			waitForLayout = true
			Log.d(TAG, "resetCount: cal side length fail, wait for layout")
			return
		} else {
			waitForLayout = false
			Log.d(TAG, "resetCount: cal side length success:$viewSideLength")
		}
		colorViewMap = Array(countPerSide) { arrayOfNulls(countPerSide) }
		removeAllViews()
		val tempList = ArrayList(viewArrayList)
		viewArrayList.clear()
		var index: Int
		var tempView: CustomItemView?
		for (row in 0 until countPerSide) {
			for (column in 0 until countPerSide) {
				index = row * countPerSide + column
				tempView = if (index >= tempList.size) null else tempList[index]
				tempView = createOrResetColorView(tempView, row, column, viewSideLength)
				colorViewMap[row][column] = tempView
				viewArrayList.add(tempView)
			}
		}
	}

	fun resetColor(baseColor: HSB, diffColor: HSB, index: Int) {
		tempBaseColor = baseColor
		tempDiffColor = diffColor
		if (waitForLayout || viewArrayList.isEmpty()) {
			Log.d(TAG, "resetColor: wait for layout:$waitForLayout - ${viewArrayList.size}")
			return
		}
		Log.d(TAG, "resetColor: $baseColor - $diffColor")
		diffIndex = index
		isShowingResult = false
		for (i in viewArrayList.indices) {
			viewArrayList[i]?.let { customCardView ->
				customCardView.reset()
				if (i == diffIndex) {
					customCardView.setCardBackgroundColor(diffColor.rgbColor)
				} else {
					customCardView.setCardBackgroundColor(baseColor.rgbColor)
				}
			}
		}
	}

	fun setOnDiffColorViewClickListener(onDiffColorViewClickListener: ((view: View?, result: Boolean) -> Unit?)?) {
		this.onDiffColorViewClickListener = onDiffColorViewClickListener
	}

	fun showResult() {
		isShowingResult = true
		val cardView = viewArrayList[diffIndex]
		cardView?.showBorder()
	}

	fun setResultStrokeColor(resultStrokeColor: Int) {
		this.resultStrokeColor = resultStrokeColor
		if (isShowingResult) {
			showResult()
		}
	}

	private fun calViewSideLength(countPerSide: Int): Int {
		val viewWidth = width
		spacing = calSpacing(context, countPerSide)
		return (viewWidth + spacing) / countPerSide - spacing
	}

	private fun createOrResetColorView(
		cardView: CustomItemView?,
		row: Int,
		column: Int,
		viewSideLength: Int,
	): CustomItemView {
		val finalCardView = cardView?.apply { reset() } ?: CustomItemView(context, itemBorderColor)
		finalCardView.needCorner(needSpacing)
		val layoutParams = createLayoutParams(row, column, viewSideLength)
		Log.d(TAG, "createOrResetColorView: $row-$column : ${layoutParams.leftMargin}-${layoutParams.topMargin}")
		this.addView(finalCardView, layoutParams)
		finalCardView.onClick {
			checkColorClick(finalCardView, row, column)
		}
		return finalCardView
	}

	private fun checkColorClick(view: CustomItemView, row: Int, column: Int) {
		if (onDiffColorViewClickListener != null) {
			val result = row * countPerSide + column == diffIndex
			onDiffColorViewClickListener!!.invoke(view, result)
		}
	}

	private fun createLayoutParams(
		row: Int,
		column: Int,
		viewSideLength: Int,
	): LayoutParams {
		Log.d(TAG, "createLayoutParams: $row,$column,$viewSideLength,$spacing")
		val layoutParams = LayoutParams(viewSideLength, viewSideLength)
		layoutParams.topMargin = row * (viewSideLength + spacing)
		layoutParams.leftMargin = column * (viewSideLength + spacing)
		return layoutParams
	}

	private class CustomItemView(context: Context, val itemBorderColor: Int) : FrameLayout(context) {

		companion object {
			private const val TAG = "CustomCardView"
		}
		
		private val drawable = GradientDrawable()

		init {
			isClickable = true
			isFocusable = true
			drawable.cornerRadius = CORNER_RADIUS.dp2px.toFloat()
			this.background = drawable
		}

		fun setCardBackgroundColor(argbColor: Int) {
			drawable.setColor(argbColor)
		}
		
		fun showBorder() {
			drawable.setStroke(DisplayUtil.dip2px(context, 3f), itemBorderColor)
		}
		
		fun reset() {
			drawable.setStroke(0, Color.TRANSPARENT)
		}

		fun needCorner(needSpacing: Boolean) {
			drawable.cornerRadius = if (needSpacing) CORNER_RADIUS.dp2px.toFloat() else 0f
		}
	}
}