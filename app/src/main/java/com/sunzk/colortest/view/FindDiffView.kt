package com.sunzk.colortest.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import com.sunzk.base.utils.DisplayUtil
import com.sunzk.colortest.R
import com.sunzk.colortest.entity.HSB
import java.util.*

class FindDiffView : FrameLayout {
    private var spacing = 0
    private lateinit var colorViewMap: Array<Array<CustomCardView?>>
    private val viewArrayList =
        ArrayList<CustomCardView?>()
    private var countPerSide = -1
    private var diffIndex = 0
    private var onDiffColorViewClickListener: ((view: View?, result: Boolean) -> Unit?)? = null
    private var waitForLayout = false
    private var tempBaseColor: HSB? = null
    private var tempDiffColor: HSB? = null
    private var resultStrokeColor = 0
    private var isShowingResult = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
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
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed && waitForLayout) {
            post {
                resetCount(countPerSide, true)
                if (tempBaseColor != null && tempDiffColor != null) {
                    resetColor(tempBaseColor!!, tempDiffColor!!)
                }
            }
        }
    }

    private fun calSpacing(context: Context, countPerSide: Int): Int {
        return DisplayUtil.dip2px(context, 3 + 3 * 1.0f / countPerSide)
    }

    /**
     * 修改正方形的数量
     * @param countPerSide 每边的正方形的数量
     */
    @JvmOverloads
    fun resetCount(countPerSide: Int, force: Boolean = false) {
        Log.d(
            TAG,
            "resetCount: " + this.countPerSide + " , " + countPerSide + " , " + force
        )
        if (this.countPerSide == countPerSide && !force) {
            return
        }
        this.countPerSide = countPerSide
        val viewSideLength = calViewSideLength(countPerSide)
        if (viewSideLength <= 0) {
            waitForLayout = true
            Log.d(
                TAG,
                "resetCount: cal side length fail, wait for layout"
            )
            return
        } else {
            waitForLayout = false
            Log.d(
                TAG,
                "resetCount: cal side length success:$viewSideLength"
            )
        }
        colorViewMap = Array(
            countPerSide
        ) { arrayOfNulls<CustomCardView>(countPerSide) }
        removeAllViews()
        val tempList =
            ArrayList(viewArrayList)
        viewArrayList.clear()
        var index: Int
        var tempView: CustomCardView?
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

    fun resetColor(baseColor: HSB, diffColor: HSB) {
        tempBaseColor = baseColor
        tempDiffColor = diffColor
        if (waitForLayout || viewArrayList.isEmpty()) {
            Log.d(
                TAG,
                "resetColor: wait for layout:" + waitForLayout + " - " + viewArrayList.size
            )
            return
        }
        Log.d(
            TAG,
            "resetColor: $baseColor - $diffColor"
        )
        diffIndex = Random().nextInt(viewArrayList.size)
        for (i in viewArrayList.indices) {
            val customCardView = viewArrayList[i]
            customCardView!!.background = null
            customCardView.cardView!!.visibility = View.VISIBLE
            isShowingResult = false
            if (i == diffIndex) {
                customCardView.cardView!!.setCardBackgroundColor(diffColor.rgbColor)
            } else {
                customCardView.cardView!!.setCardBackgroundColor(baseColor.rgbColor)
            }
        }
    }

    fun setOnDiffColorViewClickListener(onDiffColorViewClickListener: ((view: View?, result: Boolean) -> Unit?)?) {
        this.onDiffColorViewClickListener = onDiffColorViewClickListener
    }

    fun showResult() {
        isShowingResult = true
        val cardView = viewArrayList[diffIndex]
        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = CORNER_RADIUS.toFloat()
        gradientDrawable.setStroke(DisplayUtil.dip2px(context, 3f), resultStrokeColor)
        gradientDrawable.color = cardView!!.cardView!!.cardBackgroundColor
        cardView.cardView!!.visibility = View.INVISIBLE
        cardView.background = gradientDrawable
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
        cardView: CustomCardView?,
        row: Int,
        column: Int,
        viewSideLength: Int
    ): CustomCardView {
        var cardView = cardView
        val layoutParams = createLayoutParams(row, column, viewSideLength)
        Log.d(
            TAG,
            "createOrResetColorView: " + row + "-" + column + " : " + layoutParams.leftMargin + "-" + layoutParams.topMargin
        )
        if (cardView == null) {
            cardView = CustomCardView(context)
            cardView.cardView!!.radius = CORNER_RADIUS.toFloat()
            cardView.isClickable = true
            cardView.isFocusable = true
        }
        this.addView(cardView, layoutParams)
        val finalCardView: CustomCardView = cardView
        cardView.setOnClickListener(OnClickListener { v: View? ->
            checkColorClick(
                finalCardView,
                row,
                column
            )
        })
        return cardView
    }

    private fun checkColorClick(view: CustomCardView, row: Int, column: Int) {
        if (onDiffColorViewClickListener != null) {
            val result = row * countPerSide + column == diffIndex
            onDiffColorViewClickListener!!.invoke(view, result)
        }
    }

    private fun createLayoutParams(
        row: Int,
        column: Int,
        viewSideLength: Int
    ): LayoutParams {
        Log.d(
            TAG,
            "createLayoutParams: $row,$column,$viewSideLength,$spacing"
        )
        val layoutParams =
            LayoutParams(viewSideLength, viewSideLength)
        layoutParams.topMargin = row * (viewSideLength + spacing)
        layoutParams.leftMargin = column * (viewSideLength + spacing)
        return layoutParams
    }

    private class CustomCardView : FrameLayout {
        var cardView: CardView? = null
            private set

        constructor(context: Context) : super(context) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet?) : super(
            context,
            attrs
        ) {
            init()
        }

        constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int
        ) : super(context, attrs, defStyleAttr) {
            init()
        }

        constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
        ) : super(context, attrs, defStyleAttr, defStyleRes) {
            init()
        }

        private fun init() {
            cardView = CardView(context)
            addView(
                cardView,
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        }

        companion object {
            private const val TAG = "CustomCardView"
        }
    }

    companion object {
        private const val TAG = "FindDiffView"
        private const val CORNER_RADIUS = 5
    }
}