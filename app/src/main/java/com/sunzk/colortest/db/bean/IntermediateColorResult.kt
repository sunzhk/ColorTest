package com.sunzk.colortest.db.bean

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

    enum class Difficulty(val text: String, 
                          val colorHDifferencePercent: Int,
                          val colorSDifferencePercent: Int,
                          val colorBDifferencePercent: Int,
                          val minSBPercent: Int,
                          val allowDeviation: Int) {
        Easy("入门", 40, 35, 35, 60, 15),
        Normal("熟练", 30, 30, 30, 40, 10),
        Hard("精通", 15, 25, 25, 20, 5);

        fun isRight(leftColor: FloatArray, rightColor: FloatArray, answer: FloatArray): Boolean {
            val centerColor = floatArrayOf(
                (leftColor[0] + rightColor[0]) / 2.0f,
                (leftColor[1] + rightColor[1]) / 2.0f,
                (leftColor[2] + rightColor[2]) / 2.0f)
            val difH = abs(answer[0] - centerColor[0]) * 100f / 360f
            val difS = abs(answer[1] - centerColor[1]) * 100f
            val difB = abs(answer[2] - centerColor[2]) * 100f
            return difH <= allowDeviation && difS <= allowDeviation && difB <= allowDeviation
        }
    }
}
