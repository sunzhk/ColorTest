package com.sunzk.colortest.view.colorPicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.FontMetrics
import android.graphics.Point
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.UiThread
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import com.arcsoft.closeli.utils.takeIfIs
import com.blankj.utilcode.util.ScreenUtils
import com.sunzk.base.utils.ColorUtils
import com.sunzk.colortest.R
import com.sunzk.colortest.entity.HSB
import com.sunzk.demo.tools.ext.dp2px
import com.sunzk.demo.tools.ext.sp2px
import com.sunzk.demo.tools.ext.square
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt


/**
 * 色轮取色器
 */
class ColorWheelColorPicker(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : View(context,
	attrs,
	defStyleAttr,
	defStyleRes), IColorPicker {

	companion object {
		private const val TAG: String = "ColorWheelColorPicker"
	}

	// <editor-fold desc="构造函数">
	constructor(context: Context?): this(context, null, 0, 0)
	constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0, 0)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
	// </editor-fold>

	override val pickerView: View
		get() = this
	
	override val hsb: HSB
		get() = HSB(hValue, sPercent, bPercent)

	override var onColorPick: ((HSB) -> Unit)? = null

	// <editor-fold desc="核心参数/函数">
	// HSB的值。H取0~360；S和B取0~100
	private var hValue = 45f
	private var sPercent = 50f
	private var bPercent = 50f
	
	@UiThread
	fun updateH(h: Float) {
		hValue = h
		onHueChange()
		invalidate()
		onColorPick?.invoke(hsb)
	}
	@UiThread
	fun updateS(s: Float) {
		sPercent = s
		updateSquareIndicatorParams()
		onSaturationChange()
		invalidate()
		onColorPick?.invoke(hsb)
	}
	@UiThread
	fun updateB(b: Float) {
		bPercent = b
		updateSquareIndicatorParams()
		onBrightnessChange()
		invalidate()
		onColorPick?.invoke(hsb)
	}
	@UiThread
	fun updateSB(s: Float, b: Float) {
		sPercent = s
		bPercent = b
		updateSquareIndicatorParams()
		onSaturationChange()
		onBrightnessChange()
		invalidate()
		onColorPick?.invoke(hsb)
	}
	@UiThread
	override fun updateHSB(h: Float, s: Float, b: Float) {
		hValue = h
		sPercent = s
		bPercent = b
		updateSquareIndicatorParams()
		onHueChange()
		onSaturationChange()
		onBrightnessChange()
		invalidate()
		onColorPick?.invoke(hsb)
	}
	// </editor-fold>

	// <editor-fold desc="宽高计算">

	private var widthSpecMode: Int = 0
	private var heightSpecMode: Int = 0
	private var widthSpecSize = 0
	private var heightSpecSize = 0

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
		heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
		widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
		heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
		val spacing = paddingLeft + paddingRight + (layoutParams?.takeIfIs<MarginLayoutParams>()?.let { marginStart + marginEnd } ?: 0)
		when (widthSpecMode) {
			MeasureSpec.UNSPECIFIED, MeasureSpec.AT_MOST -> {
				heightSpecSize = min(ScreenUtils.getAppScreenWidth(), ScreenUtils.getAppScreenHeight()) - spacing
			}
			MeasureSpec.EXACTLY -> {
				heightSpecSize = min(widthSpecSize, heightSpecSize) - spacing
			}
		}
		widthSpecSize = if (showHintText) {
			(heightSpecSize - hintTextHeight - 4.dp2px).toInt()
		} else {
			heightSpecSize
		}
		Log.d(TAG, "ColorWheelColorPicker#onMeasure- UNSPECIFIED=${MeasureSpec.UNSPECIFIED}, AT_MOST=${MeasureSpec.AT_MOST}, EXACTLY=${MeasureSpec.EXACTLY}")
		Log.d(TAG, "ColorWheelColorPicker#onMeasure- widthSpecMode=$widthSpecMode, heightSpecMode=$heightSpecMode, widthSpecSize=$widthSpecSize, heightSpecSize=$heightSpecSize")
		setMeasuredDimension(widthSpecSize, heightSpecSize)
	}

	// </editor-fold>

	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		super.onLayout(changed, left, top, right, bottom)
		drawWidth= right - left
		drawHeight = bottom - top
		updateDrawParams()
		updateColorPickerParams()
		updateHintTextParams()
	}

	// <editor-fold desc="基础绘制参数及更新">
	/**
	 * 绘制区域宽度
 	 */
	private var drawWidth: Int = 0
		get() {
			if (field == 0 && width > 0) {
				field = width
				updateDrawParams()
			}
			return field
		}
	/**
	 * 绘制区域高度
	 */
	private var drawHeight: Int = 0
		get() {
			if (field == 0 && height > 0) {
				field = height
				updateDrawParams()
			}
			return field
		}
	/**
	 * 绘制内容的边长
	 */
	private var drawSideLength: Int = 0
	/**
	 * 环的圆心
	 */
	private var ringCenterPoint = Point(0, 0)
	/**
	 * 圆的半径(在圆环二分之一宽度位置的圆，要以此来定义圆环绘制区域)
	 */
	private var ringRadius = 0f
	/**
	 * 圆环的绘制区域：一个正方形，使圆环二分之一宽度位置的圆为正方形的内切圆
	 */
	private var ringRect = RectF()
	/**
	 * 圆环的宽度
	 */
	var ringWidth: Float = 21.dp2px.toFloat()
		set(value) {
			field = value
			updateDrawParams()
			postInvalidate()
		}

	/**
	 * 正方形的边长
	 */
	private var squareSideLength = 0f
	/**
	 * 正方形的绘制区域
	 */
	private var squareRect = RectF()

	/**
	 * 更新绘制参数
	 */
	private fun updateDrawParams() {
		// 绘制区域的边长
		drawSideLength = min(drawWidth, drawHeight)
		// 环的参数 - 圆心
		ringCenterPoint.x = drawWidth / 2
		ringCenterPoint.y = drawSideLength / 2
		// 环的参数 - 半径
		ringRadius = (drawSideLength - ringWidth) / 2f
		// 环的参数 - 区域
		ringRect.top = ringCenterPoint.y - ringRadius
		ringRect.bottom = ringCenterPoint.y + ringRadius
		ringRect.left = ringCenterPoint.x - ringRadius
		ringRect.right = ringCenterPoint.x + ringRadius
		// 环的画笔参数 - 渐变色
		ringPaint.shader = SweepGradient(
			ringCenterPoint.x.toFloat(),
			ringCenterPoint.y.toFloat(),
			ColorUtils.HUE_COLOR_LIST,
			null
		)
		// 正方形边长
		squareSideLength = sqrt(2f * square(ringRadius - ringWidth * 2 / 3))
		// 正方形绘制区域
		squareRect.top = ringCenterPoint.y - squareSideLength / 2
		squareRect.bottom = ringCenterPoint.y + squareSideLength / 2
		squareRect.left = ringCenterPoint.x - squareSideLength / 2
		squareRect.right = ringCenterPoint.x + squareSideLength / 2
	}

	/**
	 * 是否要显示HSB锁的提示文字
	 */
	var showHintText = true
		set(value) {
			field = value
			requestLayout()
		}
	/**
	 * 提示文字
	 */
	private var hintText = ArrayList<Pair<String, PointF>>()

	/**
	 * 更新提示文字相关参数
	 */
	private fun updateHintTextParams() {
		hintText.clear()
		if (!showHintText) {
			return
		}
		val y = drawHeight - 2f
		val hText = "色相=${hValue.toInt()}"
		val sText = "饱和度=${sPercent.toInt()}"
		val sWidth = FloatArray(sText.length).also { hintTextPaint.getTextWidths(sText, it) }.sum()
		val bText = "明度=${bPercent.toInt()}"
		val bWidth = FloatArray(bText.length).also { hintTextPaint.getTextWidths(bText, it) }.sum()

		hintText.add(hText to PointF(0f, y))
		hintText.add(sText to PointF(((drawWidth - sWidth) / 2), y))
		hintText.add(bText to PointF(drawWidth - bWidth, y))
	}

	private fun usableText(b: Boolean): String {
		return if (b) "禁用" else "启用"
	}
	// </editor-fold>

	// <editor-fold desc="用户取色相关参数及更新">

	/**
	 * 色相指示器的边框宽度
	 */
	var hueIndicatorBorderWidth = 3f.dp2px.toFloat()
		set(value) {
			field = value
			hueIndicatorPaint.strokeWidth = value
			updateHueIndicatorRect()
			postInvalidate()
		}
	var hueIndicatorInnerWidth = 12.dp2px.toFloat()
		set(value) {
			field = value
			updateHueIndicatorRect()
			postInvalidate()
		}

	/**
	 * 色相指示器内圈绘制区域
	 */
	private val hueIndicatorInnerRect = RectF()
	/**
	 * 色相指示器外圈绘制区域
	 */
	private val hueIndicatorOuterRect = RectF()

	/**
	 * 方块取色器 - 指示器中心点
	 */
	private var squareIndicatorCenterPoint = Point()
	private var squareIndicatorInnerRingWidth = 4.dp2px.toFloat()
	private var squareIndicatorOuterRingWidth = 5.dp2px.toFloat()

	/**
	 * 更新取色器参数变更
	 */
	private fun updateColorPickerParams() {
		// 色相指示器的绘制区域
		updateHueIndicatorRect()
		// 方块取色器的指示器绘制区域
		updateSquareIndicatorParams()
		// 有长宽变更时，颜色参数也要跟着刷新
		onHueChange()
		onSaturationChange()
		onBrightnessChange()
	}

	/**
	 * 色相指示器绘制区域不跟随色相值变化而刷新；只在layout或其他参数变化时更新一下
	 * 色相指示器的绘制方式是旋转画布到对应色相上绘制，无需更新绘制区域
	 */
	private fun updateHueIndicatorRect() {
		hueIndicatorInnerRect.top = ringCenterPoint.y - ringRadius - ringWidth / 2 + hueIndicatorBorderWidth / 2
		hueIndicatorInnerRect.bottom = ringCenterPoint.y - ringRadius + ringWidth / 2 - hueIndicatorBorderWidth / 2
		hueIndicatorInnerRect.left = ringCenterPoint.x - hueIndicatorInnerWidth / 2
		hueIndicatorInnerRect.right = ringCenterPoint.x + hueIndicatorBorderWidth / 2
		hueIndicatorOuterRect.top = hueIndicatorInnerRect.top - hueIndicatorBorderWidth + 2.dp2px
		hueIndicatorOuterRect.bottom = hueIndicatorInnerRect.bottom + hueIndicatorBorderWidth - 2.dp2px
		hueIndicatorOuterRect.left = hueIndicatorInnerRect.left - hueIndicatorBorderWidth + 2.dp2px
		hueIndicatorOuterRect.right = hueIndicatorInnerRect.right + hueIndicatorBorderWidth - 2.dp2px
	}

	/**
	 * 饱和度、明度变化时更新指示器的绘制区域
	 */
	private fun updateSquareIndicatorParams() {
		// 确定中心点
		squareIndicatorCenterPoint.x = (squareRect.left + (sPercent * squareSideLength / 100f)).toInt()
		squareIndicatorCenterPoint.y = (squareRect.bottom - (bPercent * squareSideLength / 100f)).toInt()
	}

	/**
	 * 色相变化时逻辑
	 */
	private fun onHueChange() {
		// 色相变化时，也要更新正方形取色器的颜色
		squarePaint.shader = ComposeShader(
			LinearGradient(
				squareRect.left, squareRect.top, 
				squareRect.right, squareRect.top,
				intArrayOf(
					Color.HSVToColor(floatArrayOf(hValue.toFloat(), 0f, 1f)),
					Color.HSVToColor(floatArrayOf(hValue.toFloat(), 1f, 1f))
				),
				null,
				Shader.TileMode.CLAMP
			),
			LinearGradient(
				squareRect.left, squareRect.bottom,
				squareRect.left, squareRect.top,
				intArrayOf(
					Color.HSVToColor(floatArrayOf(hValue.toFloat(), 0f, 0f)),
					Color.HSVToColor(floatArrayOf(hValue.toFloat(), 0f, 1f))
				),
				null,
				Shader.TileMode.CLAMP
			),
			PorterDuff.Mode.MULTIPLY
		)
		// 更新提示文字
		updateHintTextParams()
	}
	
	private fun onSaturationChange() {}
	
	private fun onBrightnessChange() {}

	// </editor-fold>
	
	// <editor-fold desc="画笔">
	/**
	 * 色相环的画笔
	 */
	private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
		it.strokeWidth = ringWidth
		it.style = Paint.Style.STROKE
	}

	/**
	 * 饱和度&明度正方形取色器的画笔
	 */
	private val squarePaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
		it.color = Color.RED
		it.style = Paint.Style.FILL
	}

	/**
	 * 色相指示器的画笔 - 内圈白框
	 */
	private val hueIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
		it.color = Color.WHITE
		it.style = Paint.Style.STROKE
		it.strokeWidth = hueIndicatorBorderWidth
	}
	/**
	 * 色相指示器的画笔 - 外圈黑框
	 */
	private val hueIndicatorOutPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
		it.color = Color.BLACK
		it.style = Paint.Style.STROKE
		it.strokeWidth = 1.dp2px.toFloat()
	}

	/**
	 * 矩形取色器的指示器 - 内圈白环
	 */
	private val squareIndicatorInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
		it.color = Color.WHITE
		it.style = Paint.Style.STROKE
		it.strokeWidth = 2.dp2px.toFloat()
	}
	/**
	 * 矩形取色器的指示器 - 外圈黑环
	 */
	private val squareIndicatorOuterPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
		it.color = Color.BLACK
		it.style = Paint.Style.STROKE
		it.strokeWidth = 1.dp2px.toFloat()
	}

	/**
	 * 提示文字的画笔
	 */
	private val hintTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).also { 
		it.color = resources.getColor(R.color.theme_txt_standard, null)
		it.textSize = 14f.sp2px
		val fm: FontMetrics = it.getFontMetrics()
		hintTextHeight = fm.descent - fm.ascent // 文字总高度
	}

	/**
	 * 提示文字的单行高度
	 */
	var hintTextHeight = 0f
	// </editor-fold>
	
	// <editor-fold desc="触摸逻辑">
	
	private enum class TouchMode {
		None,
		Hue,
		Square
	}


	/**
	 * 色值锁，禁止/允许用户修改HSB中的一项
	 */
	private val hsbLock = booleanArrayOf(false, false, false)

	@UiThread
	override fun setLock(index: Int, lock: Boolean) {
		hsbLock[index] = lock
		updateHintTextParams()
		invalidate()
	}

	private var currentTouchMode = TouchMode.None

	override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
		Log.d(TAG, "ColorWheelColorPicker#dispatchTouchEvent- ${event?.action}, ${event?.x}, ${event?.y}")
		if (event == null || event.pointerCount != 1) {
			return false
		}
		when (currentTouchMode) {
			TouchMode.None -> {
				if (event.action == MotionEvent.ACTION_DOWN) {
					// 当前无触摸模式，且只有一个手指按下时，判断触摸位置，确定触摸模式
					currentTouchMode = checkTouchArea(event)
				}
			}
			TouchMode.Hue -> { handleRingTouch(event) }
			TouchMode.Square -> { handleSquareTouch(event) }
		}
		return true
	}

	/**
	 * 检查触摸区域，确定初始触摸模式
	 */
	private fun checkTouchArea(event: MotionEvent): TouchMode {
		return if (event.x > squareRect.left && event.x < squareRect.right && event.y > squareRect.top && event.y < squareRect.bottom) {
			// 正方形区域的判断比较简单，先做
			handleSquareTouch(event)
			TouchMode.Square
		} else {
			// 圆环区域的判断麻烦点，要算一下距离圆心的距离
			val distance = sqrt(square(event.x - ringCenterPoint.x.toFloat()) + square(event.y - ringCenterPoint.y.toFloat()))
			if (distance > ringRadius - ringWidth / 2 && distance < ringRadius + ringWidth / 2) {
				handleRingTouch(event)
				TouchMode.Hue
			} else {
				TouchMode.None
			}
		}
	}

	/**
	 * 触摸模式为Hue，手指在控件上滑动时判断角度
	 */
	private fun handleRingTouch(event: MotionEvent) {
		if (!hsbLock[0]) {
			// 计算点x,y与ringCenterPoint的夹角
			val angle = Math.toDegrees(atan2((event.y - ringCenterPoint.y).toDouble(), (event.x - ringCenterPoint.x).toDouble()))
			var h = angle.toFloat() + 135
			if (h < 0) {
				h += 360
			}
			updateH(h)
			Log.d(TAG, "ColorWheelColorPicker#handleRingTouchMove- angle=$angle, hValue=$hValue")
		}
		if (event.action == MotionEvent.ACTION_UP) {
			currentTouchMode = TouchMode.None
		}
	}

	/**
	 * 触摸模式为Square，手指在控件上滑动时判断位置
	 */
	private fun handleSquareTouch(event: MotionEvent) {
		val s = if (hsbLock[1]) {
			sPercent
		} else {
			if (event.x <= squareRect.left) {
				0f
			} else if (event.x >= squareRect.right) {
				100f
			} else {
				(event.x - squareRect.left) * 100f / squareSideLength
			}
		}
		val b = if (hsbLock[2]) {
			bPercent
		} else {
			if (event.y <= squareRect.top) {
				100f
			} else if (event.y >= squareRect.bottom) {
				0f
			} else {
				(squareRect.bottom - event.y) * 100f / squareSideLength
			}
		}
		updateSB(s, b)
		if (event.action == MotionEvent.ACTION_UP) {
			currentTouchMode = TouchMode.None
		}
	}
	// </editor-fold>
	
	// <editor-fold desc="绘制过程">
	
	override fun onDraw(canvas: Canvas) {
		if (canDraw()) {
			drawRing(canvas)
			drawSquare(canvas)
			drawHint(canvas)
		}
	}

	private fun drawRing(canvas: Canvas) {
		canvas.save()
		// 转一下，让红色绘制在圆环的左上角
		canvas.rotate(-135f, ringCenterPoint.x.toFloat(), ringCenterPoint.y.toFloat())
		canvas.drawArc(ringRect, 0f, 360f, false, ringPaint)
		// 再转一下，把选取的色相转到正上方，方便绘制指示器
		canvas.rotate(90f + hValue, ringCenterPoint.x.toFloat(), ringCenterPoint.y.toFloat())
		// 先画指示器的白框
		canvas.drawRect(hueIndicatorInnerRect, hueIndicatorPaint)
		// 再在白框外沿画一道黑框，提高辨识度
		canvas.drawRect(hueIndicatorOuterRect, hueIndicatorOutPaint)
		canvas.restore()
	}
	
	private fun drawSquare(canvas: Canvas) {
		// 先绘制矩形
		canvas.drawRect(squareRect, squarePaint)
		// 然后绘制指示器
		canvas.drawCircle(squareIndicatorCenterPoint.x.toFloat(), squareIndicatorCenterPoint.y.toFloat(), squareIndicatorInnerRingWidth, squareIndicatorInnerPaint)
		canvas.drawCircle(squareIndicatorCenterPoint.x.toFloat(), squareIndicatorCenterPoint.y.toFloat(), squareIndicatorOuterRingWidth, squareIndicatorOuterPaint)
	}
	
	private fun drawHint(canvas: Canvas) {
		if (!showHintText) {
			return
		}
		hintText.forEachIndexed { index, (text, point) ->
			canvas.drawText(text, point.x, point.y, hintTextPaint)
		}
	}
	
	private fun canDraw(): Boolean {
		return drawWidth > 0 && drawHeight > 0
	}

	// </editor-fold>

}