package com.sunzk.colortest.entity

import android.graphics.Color
import androidx.annotation.FloatRange

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
}