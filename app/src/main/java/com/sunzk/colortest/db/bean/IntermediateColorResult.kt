package com.sunzk.colortest.db.bean

import android.util.Log
import kotlin.math.abs

/**
 * 找中间色的结果实体类
 */
data class IntermediateColorResult(
    val id: Int = 0,
    val date: String? = null,
    val difficulty: Difficulty,
    val questionLeftH: Float = 0f,
    val questionLeftS: Float = 0f,
    val questionLeftB: Float = 0f,
    val questionRightH: Float = 0f,
    val questionRightS: Float = 0f,
    val questionRightB: Float = 0f,
    val answerH: Float = 0f,
    val answerS: Float = 0f,
    val answerB: Float = 0f,
) {
    companion object {
        private const val TAG: String = "IntermediateColorResult"
    }

    val questionLeft
        get() = floatArrayOf(questionLeftH, questionLeftS / 100f, questionLeftB / 100f)
    val questionRight
        get() = floatArrayOf(questionRightH, questionRightS / 100f, questionRightB / 100f)
    val answer
        get() =  floatArrayOf(answerH, answerS / 100f, answerB / 100f)

    fun isRight(): Boolean {
        return difficulty.isRight(
            floatArrayOf(questionLeftH, questionLeftS, questionLeftB),
            floatArrayOf(questionRightH, questionRightS, questionRightB),
            floatArrayOf(answerH, answerS, answerB)
        )
    }

    enum class Difficulty(val text: String, 
                          val fixedNumberOfParameters: Int,
                          val colorHDifferencePercent: Int,
                          val colorSDifferencePercent: Int,
                          val colorBDifferencePercent: Int,
                          val minSBPercent: Int,
                          val allowDeviation: Int) {
        Easy("中杯", 2, 40, 35, 35, 60, 5),
        Normal("大杯", 1, 30, 30, 30, 40, 8),
        Hard("超大杯", 0, 15, 25, 25, 20, 10);

        fun isRight(leftColor: FloatArray, rightColor: FloatArray, answer: FloatArray): Boolean {
            val centerColor = floatArrayOf(
                (leftColor[0] + rightColor[0]) / 2.0f,
                (leftColor[1] + rightColor[1]) / 2.0f,
                (leftColor[2] + rightColor[2]) / 2.0f)
            val difH = abs(answer[0] - centerColor[0]) * 100f / 360f
            val difS = abs(answer[1] - centerColor[1])
            val difB = abs(answer[2] - centerColor[2])
            val isRight = difH <= allowDeviation && difS <= allowDeviation && difB <= allowDeviation
            Log.d(TAG, "Difficulty#isRight- isRight: $isRight, difH: $difH, difS: $difS, difB: $difB, allowDeviation: $allowDeviation")
            return isRight
        }
    }
}
