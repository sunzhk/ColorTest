package com.sunzk.colortest.entity

import android.graphics.Color

class HSB {
    var h = 0f
    var s = 0f
    var b = 0f

    constructor() {}
    constructor(colors: FloatArray) {
        h = colors[0]
        s = colors[1]
        b = colors[2]
    }

    constructor(h: Float, s: Float, b: Float) {
        this.h = h
        this.s = s
        this.b = b
    }

    val rgbColor: Int
        get() = Color.HSVToColor(floatArrayOf(h, s, b))

    override fun toString(): String {
        val sb = StringBuilder("HSB{")
        sb.append("h=").append(h)
        sb.append(", s=").append(s)
        sb.append(", b=").append(b)
        sb.append('}')
        return sb.toString()
    }
}