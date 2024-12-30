package com.sunzk.colortest.view

import android.util.Log
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.lifecycle.MutableLiveData
import kotlin.math.min

class HBSColorSelectorData {
    
    private val TAG: String = "HBSColorSelectorData"

    /**
     * from 0 to 360
     */
    val colorH: MutableLiveData<Int> = MutableLiveData(0)

    /**
     * from 0 to 1
     */
    val colorS: MutableLiveData<Float> = MutableLiveData(0f)
    
    /**
     * from 0 to 1
     */
    val colorB: MutableLiveData<Float> = MutableLiveData(0f)
    
    fun setColorH(@IntRange(from = COLOR_H_MIN.toLong(), to = COLOR_H_MAX.toLong())h: Int) {
        Log.d(TAG, "setColorH: $h")
        val resultColor = 
            if (h < COLOR_H_MIN)
                COLOR_H_MIN
            else
                min(h, COLOR_H_MAX)
        
        if (colorH.value == resultColor) {
            return
        }
        colorH.postValue(resultColor)
    }

    fun setColorS(@FloatRange(from = COLOR_S_MIN.toDouble(), to = COLOR_S_MAX.toDouble()) s: Float) {
        Log.d(TAG, "setColorS: $s")
        val resultColor =
            if (s < COLOR_S_MIN)
                COLOR_S_MIN
            else
                min(s, COLOR_S_MAX)

        if (colorS.value == resultColor) {
            return
        }
        colorS.postValue(resultColor)
    }

    fun setColorB(@FloatRange(from = COLOR_B_MIN.toDouble(), to = COLOR_B_MAX.toDouble())b: Float) {
        Log.d(TAG, "setColorB: $b")
        val resultColor =
            if (b < COLOR_B_MIN)
                COLOR_B_MIN
            else
                min(b, COLOR_B_MAX)

        if (colorB.value == resultColor) {
            return
        }
        colorB.postValue(resultColor)
    }

    companion object {
        const val COLOR_H_MIN = 0
        const val COLOR_H_MAX = 360
        const val COLOR_H_INTERVAL = 1
        
        const val COLOR_S_MIN = 0f
        const val COLOR_S_MAX = 1.0f
        const val COLOR_S_INTERVAL = 0.01f
        
        const val COLOR_B_MIN = 0f
        const val COLOR_B_MAX = 1.0f
        const val COLOR_B_INTERVAL = 0.01f
    }

}