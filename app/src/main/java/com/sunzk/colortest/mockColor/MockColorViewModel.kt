package com.sunzk.colortest.mockColor

import androidx.lifecycle.ViewModel
import com.sunzk.base.expand.emitBy
import com.sunzk.base.utils.ColorUtils
import com.sunzk.colortest.db.bean.MockColorResult
import com.sunzk.colortest.entity.HSB
import kotlinx.coroutines.flow.MutableStateFlow

class MockColorViewModel: ViewModel() {
	companion object {
		private const val TAG: String = "MockColorViewModel"
	}

	/**
	 * 当前问题的颜色
	 */
    var currentQuestionHSB: MutableStateFlow<FloatArray> = MutableStateFlow(ColorUtils.randomHSBColor(0f, 0.2f, 0.2f))

    /**
     * 难度
     */
    var currentDifficulty = MutableStateFlow(MockColorResult.Difficulty.Normal)

	val currentPickHSB = HSB(180f, 50f ,50f)
    
    fun nextQuestion() {
	    currentPickHSB.update(180f, 50f ,50f)
        currentQuestionHSB.emitBy(ColorUtils.randomHSBColor(0f, 0.2f, 0.2f))
    }
}