package com.sunzk.colortest.intermediateColor

import androidx.lifecycle.ViewModel
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.db.bean.IntermediateColorResult
import kotlinx.coroutines.flow.MutableStateFlow

class IntermediateColorViewModel : ViewModel() {

    /**
     * 当前难度
     */
    var currentDifficulty = MutableStateFlow(IntermediateColorResult.Difficulty.Normal)

    /**
     * 题目：左侧颜色
     */
    val questionLeftColor = MutableStateFlow(FloatArray(3))

    /**
     * 题目：右侧颜色
     */
    val questionRightColor = MutableStateFlow(FloatArray(3))

    /**
     * 用户答案
     */
    val answerColor = MutableStateFlow(FloatArray(3))
    
}