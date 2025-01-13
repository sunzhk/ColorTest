package com.sunzk.colortest.entity

import android.graphics.Color
import androidx.annotation.FloatRange
import com.sunzk.base.utils.ColorUtils

/**
 * 标准色值实体
 */
class HSB {
	companion object {
		const val COLOR_H_MIN = 0
		const val COLOR_H_MAX = 360
		const val COLOR_H_INTERVAL = 1

		const val COLOR_S_MIN = 0
		const val COLOR_S_MAX = 100
		const val COLOR_S_INTERVAL = 1

		const val COLOR_B_MIN = 0
		const val COLOR_B_MAX = 100
		const val COLOR_B_INTERVAL = 1
		
		fun random(minH: Float, minS: Float, minB: Float): HSB {
			return HSB(ColorUtils.randomHSBColor(minH, minS, minB))
		}
	}

	/**
	 * 色相，0~360
	 */
	@FloatRange(from = COLOR_H_MIN.toDouble(), to = COLOR_H_MAX.toDouble())
	var h = 0f

	/**
	 * 饱和度，0~100
	 */
	@FloatRange(from = COLOR_S_MIN.toDouble(), to = COLOR_S_MAX.toDouble())
	var s = 0f

	/**
	 * 明度，0~100
	 */
	@FloatRange(from = COLOR_B_MIN.toDouble(), to = COLOR_B_MAX.toDouble())
	var b = 0f

	constructor() {}
	constructor(colors: FloatArray) {
		h = colors[0]
		s = colors[1] * 100
		b = colors[2] * 100
	}

	constructor(h: Float, s: Float, b: Float) {
		this.h = h
		this.s = s
		this.b = b
	}
	
	operator fun set(index: Int, value: Float) {
		when (index) {
			0 -> { h = value }
			1 -> { s = value }
			2 -> { b = value }
			else -> { throw IllegalArgumentException("index must in 0~2. index=$index") }
		}
	}
	
	operator fun get(index: Int): Float {
		return when (index) {
			0 -> { h }
			1 -> { s }
			2 -> { b }
			else -> { throw IllegalArgumentException("index must in 0~2. index=$index") }
		}
	}

	fun update(h: Float, s: Float, b: Float) {
		this.h = h
		this.s = s
		this.b = b
	}

	fun update(hsb: HSB) {
		this.h = hsb.h
		this.s = hsb.s
		this.b = hsb.b
	}

	/**
	 * RGB色值
	 */
	val rgbColor: Int
		get() = Color.HSVToColor(floatArrayOf(h, s / 100f, b / 100f))

	/**
	 * HSB色值
	 */
	val hsbColor: FloatArray
		get() = floatArrayOf(h, s / 100f, b / 100f)

	override fun toString(): String {
		val sb = StringBuilder("HSB{")
		sb.append("h=").append(h)
		sb.append(", s=").append(s)
		sb.append(", b=").append(b)
		sb.append('}')
		return sb.toString()
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as HSB

		if (h != other.h) return false
		if (s != other.s) return false
		if (b != other.b) return false

		return true
	}

	fun clone(): HSB {
		return HSB(h, s, b)
	}
}