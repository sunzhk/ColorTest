package com.sunzk.colortest.db.bean

import android.util.Log
import kotlin.math.abs

/**
 * 模拟颜色答题记录
 */
data class MockColorResult(
    val id: Int = 0,
    val date: String? = null,
    val questionH: Float = 0f,
    val questionS: Float = 0f,
    val questionB: Float = 0f,
    val answerH: Float = 0f,
    val answerS: Float = 0f,
    val answerB: Float = 0f) {
    
    companion object {
        private const val TAG: String = "MockColorResult"
    }
    
    val question: FloatArray
        get() = floatArrayOf(questionH, questionS / 100f, questionB / 100f)
    
    val answer: FloatArray
        get() = floatArrayOf(answerH, answerS / 100f, answerB / 100f)

    fun isRight(): Boolean {
        val result = (abs(questionH - answerH) < 5f || abs(360 - questionH - answerH) < 5f) 
                && abs(questionS - answerS) < 5f && abs(questionB - answerB) < 5f
        Log.d(TAG, "MockColorResult#isRight- id: $id, question: $questionH, $questionS, $questionB, answer: $answerH, $answerS, $answerB, result: $result")
        return result
    }
}
