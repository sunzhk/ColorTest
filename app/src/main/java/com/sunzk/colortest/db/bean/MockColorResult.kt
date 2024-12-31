package com.sunzk.colortest.db.bean

import android.util.Log
import kotlin.math.abs

/**
 * 模拟颜色答题记录
 */
data class MockColorResult(
    val id: Int = 0,
    val date: String? = null,
    val difficulty: Difficulty,
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
        return difficulty.isRight(floatArrayOf(questionH, questionS, questionB), floatArrayOf(answerH, answerS, answerB))
    }
    
    enum class Difficulty(val text: String, val hOffset: Float, val sOffset: Float, val bOffset: Float) {
        Easy("入门", 15f, 15f, 15f),
        Normal("熟练", 9f, 9f, 9f),
        Hard("精通", 5f, 5f, 5f);
        
        fun isRight(question: FloatArray, answer: FloatArray): Boolean {
            val result = (abs(question[0] - answer[0]) < hOffset || abs(360 - question[0] - answer[0]) < hOffset) 
                    && abs(question[1] - answer[1]) < sOffset && abs(question[2] - answer[2]) < bOffset
            Log.d(TAG, "MockColorResult.Difficulty#isRight- $this, question: ${question[0]}, ${question[1]}, ${question[2]}, answer: ${answer[0]}, ${answer[1]}, ${answer[2]}, result: $result")
            return result
        }
    }
}
