package com.sunzk.demo.tools.ext

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ConvertUtils
import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.toLimitedString(numberOfDigits: Int): String {
	return String.format("%.${numberOfDigits}f", this)
}

fun Long.toHexInt(): Int {
	return if (this > Int.MAX_VALUE) {
		(this - Int.MAX_VALUE - 1 + Int.MIN_VALUE).also { println(it) }.toInt()
	} else {
		this.toInt()
	}
}

fun Double.roundTo(decimals: Int): Double {
	val factor = 10.0.pow(decimals.toDouble())
	return (this * factor).roundToInt() / factor
}

/**
 * dp -> px
 */
fun Int.toPx():Float{
	return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)
}

/**
 * px -> dp
 */
fun Float.toDp():Int{
	return (this / Resources.getSystem().displayMetrics.density + 0.5f).toInt()
}

/**
 * 向上取整
 */
fun Float.ceil(): Int {
	return kotlin.math.ceil(this).toInt()
}

/**
 * 向上取整
 */
fun Double.ceil(): Int {
	return kotlin.math.ceil(this).toInt()
}

/**
 * 向下取整
 */
fun Float.floor(): Int {
	return kotlin.math.floor(this).toInt()
}

/**
 * 向下取整
 */
fun Double.floor(): Int {
	return kotlin.math.floor(this).toInt()
}

/**
 * 四舍五入
 */
fun Float.round(): Int {
	return kotlin.math.round(this).toInt()
}

/**
 * 四舍五入
 */
fun Double.round(): Int {
	return kotlin.math.round(this).toInt()
}

/**
 * 平方
 */
fun square(x: Int): Int {
	return x * x
}

/**
 * 平方
 */
fun square(x: Float): Float {
	return x * x
}

/**
 * 平方
 */
fun square(x: Double): Double {
	return x * x
}

inline val Int.dp2px: Int
	get() = ConvertUtils.dp2px(this.toFloat())

inline val Float.dp2px: Int
	get() = ConvertUtils.dp2px(this)

inline val Int.sp2px: Int
	get() = ConvertUtils.sp2px(this.toFloat())

inline val Float.sp2px: Float
	get() = ConvertUtils.sp2px(this).toFloat()

inline val Int.px2dp: Int
	get() = ConvertUtils.px2dp(this.toFloat())
inline val Float.px2dp: Float
	get() = ConvertUtils.px2dp(this).toFloat()

inline val Int.px: Dp get() = this.px2dp.dp

@Composable
fun Dp.toPx(): Float {
	val density = LocalDensity.current
	return with(density) { this@toPx.toPx() }
}