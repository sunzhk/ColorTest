package com.sunzk.colortest.view

import android.util.Log
import androidx.annotation.IntRange
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.entity.HSB.Companion.COLOR_B_MAX
import com.sunzk.colortest.entity.HSB.Companion.COLOR_B_MIN
import com.sunzk.colortest.entity.HSB.Companion.COLOR_H_MAX
import com.sunzk.colortest.entity.HSB.Companion.COLOR_H_MIN
import com.sunzk.colortest.entity.HSB.Companion.COLOR_S_MAX
import com.sunzk.colortest.entity.HSB.Companion.COLOR_S_MIN
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.min

class HBSColorSelectorData() {

    companion object {
        private const val TAG: String = "HBSColorSelectorData"
    }
    
    /**
     * from 0 to 360
     */
    val colorH = MutableStateFlow(0)

    /**
     * from 0 to 100
     */
    val colorS = MutableStateFlow(0)
    
    /**
     * from 0 to 100
     */
    val colorB = MutableStateFlow(0)
    
    fun setColorH(@IntRange(from = COLOR_H_MIN.toLong(), to = COLOR_H_MAX.toLong())h: Int) {
        Log.d(TAG, "setColorH: $h")
        val resultColor = 
            if (h < COLOR_H_MIN) {
                COLOR_H_MIN
            } else {
                min(h, COLOR_H_MAX)
            }
        colorH.emitBy(resultColor)
    }

    fun setColorS(@IntRange(from = COLOR_S_MIN.toLong(), to = COLOR_S_MAX.toLong()) s: Int) {
        Log.d(TAG, "setColorS: $s", Throwable())
        val resultColor =
            if (s < COLOR_S_MIN) {
                COLOR_S_MIN
            } else {
                min(s, COLOR_S_MAX)
            }
        colorS.emitBy(resultColor)
    }

    fun setColorB(@IntRange(from = COLOR_B_MIN.toLong(), to = COLOR_B_MAX.toLong())b: Int) {
        Log.d(TAG, "setColorB: $b")
        val resultColor =
            if (b < COLOR_B_MIN) {
                COLOR_B_MIN
            } else {
                min(b, COLOR_B_MAX)
            }
        colorB.emitBy(resultColor)
    }

}