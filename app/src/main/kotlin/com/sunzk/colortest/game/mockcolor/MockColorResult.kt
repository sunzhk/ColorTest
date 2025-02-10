package com.sunzk.colortest.game.mockcolor

import android.util.Log
import com.sunzk.colortest.entity.HSB
import com.sunzk.colortest.game.IDifficulty
import kotlin.enums.EnumEntries
import kotlin.math.abs

/**
 * 模拟颜色答题记录
 */
data class MockColorResult(
	val id: Int = 0,
	val date: String? = null,
	val difficulty: Difficulty,
	val question: HSB,
	val answer: HSB) {
    
    companion object {
        private const val TAG: String = "MockColorResult"
    }

    val questionH: Float
        get() = question.h
    val questionS: Float
        get() = question.s
    val questionB: Float
        get() = question.b
    val answerH: Float
        get() = answer.h
    val answerS: Float
        get() = answer.s
    val answerB: Float
        get() = answer.b

    fun isRight(): Boolean {
        return difficulty.isRight(question, answer)
    }
    
    enum class Difficulty(override val text: String, val hOffset: Float, val sOffset: Float, val bOffset: Float): IDifficulty {
        Easy("入门", 15f, 15f, 15f),
        Normal("熟练", 9f, 9f, 9f),
        Hard("精通", 5f, 5f, 5f);

        fun isRight(question: HSB, answer: HSB): Boolean {
            val result = (abs(question[0] - answer[0]) < hOffset || abs(360 - question[0] - answer[0]) < hOffset) 
                    && abs(question[1] - answer[1]) < sOffset && abs(question[2] - answer[2]) < bOffset
            Log.d(TAG, "MockColorResult.Difficulty#isRight- $this, question: ${question[0]}, ${question[1]}, ${question[2]}, answer: ${answer[0]}, ${answer[1]}, ${answer[2]}, result: $result")
            return result
        }
    }
}
