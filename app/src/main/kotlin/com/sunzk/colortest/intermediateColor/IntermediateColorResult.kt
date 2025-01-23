package com.sunzk.colortest.intermediateColor

import android.util.Log
import com.sunzk.colortest.entity.HSB
import kotlin.math.abs

/**
 * 找中间色的结果实体类
 */
data class IntermediateColorResult(
	val id: Int = 0,
	val date: String? = null,
	val difficulty: Difficulty,
	val questionLeft: HSB,
	val questionRight: HSB,
	val answer: HSB,
) {
	companion object {
		private const val TAG: String = "IntermediateColorResult"
	}

	val questionLeftH: Float
		get() = questionLeft.h
	val questionLeftS: Float
		get() = questionLeft.s
	val questionLeftB: Float
		get() = questionLeft.b
	val questionRightH: Float
		get() = questionRight.h
	val questionRightS: Float
		get() = questionRight.s
	val questionRightB: Float
		get() = questionRight.b
	val answerH: Float
		get() = answer.h
	val answerS: Float
		get() = answer.s
	val answerB: Float
		get() = answer.b

	fun isRight(): Boolean {
		return difficulty.isRight(questionLeft, questionRight, answer)
	}

	enum class Difficulty(
		val text: String,
		val fixedNumberOfParameters: Int,
		val colorHDifferencePercent: Int,
		val colorSDifferencePercent: Int,
		val colorBDifferencePercent: Int,
		val minSBPercent: Int,
		val allowDeviation: Int,
	) {
		Easy("中杯", 2, 40, 35, 35, 60, 5),
		Normal("大杯", 1, 30, 30, 30, 40, 8),
		Hard("超大杯", 0, 15, 25, 25, 20, 10);

		fun isRight(leftColor: HSB, rightColor: HSB, answer: HSB): Boolean {
			val centerColor = HSB(
				(leftColor[0] + rightColor[0]) / 2.0f,
				(leftColor[1] + rightColor[1]) / 2.0f,
				(leftColor[2] + rightColor[2]) / 2.0f
			)
			val difH = abs(answer[0] - centerColor[0]) * 100f / 360f
			val difS = abs(answer[1] - centerColor[1])
			val difB = abs(answer[2] - centerColor[2])
			val isRight = difH <= allowDeviation && difS <= allowDeviation && difB <= allowDeviation
			Log.d(TAG, "Difficulty#isRight- isRight: $isRight, difH: $difH, difS: $difS, difB: $difB, allowDeviation: $allowDeviation")
			return isRight
		}
	}
}
