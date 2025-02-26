package com.sunzk.colortest.intermediateColor

import com.sunzk.colortest.db.bean.IntermediateColorResult
import com.sunzk.colortest.entity.HSB

/**
 * @param difficulty 当前难度
 * @param questionLeftColor 题目：左侧颜色
 * @param questionRightColor 题目：右侧颜色
 * @param answerColor 用户答案
 * @param randomIndex 左颜色与右颜色的差异点。0=色相，1=饱和度，2=明度,其他值无意义
 */
data class IntermediateColorPageData(
    val difficulty: IntermediateColorResult.Difficulty,
    val questionLeftColor: HSB,
    val questionRightColor: HSB,
    val answerColor: HSB,
    val randomIndex: Int,
    val hsbBarLock: BooleanArray
)