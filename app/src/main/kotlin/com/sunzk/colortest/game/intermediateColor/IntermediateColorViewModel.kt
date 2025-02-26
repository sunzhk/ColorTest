package com.sunzk.colortest.game.intermediateColor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.sunzk.base.expand.coroutines.GlobalDispatchers
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.db.IntermediateColorResultTable
import com.sunzk.colortest.entity.HSB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Random

class IntermediateColorViewModel : ViewModel() {
    
    companion object {
        private const val TAG: String = "IntermediateColorViewModel"
        private val DEFAULT_DIFFICULTY = IntermediateColorResult.Difficulty.Normal
    }

    private val _pageData = MutableStateFlow(createQuestion())
    /**
     * 页面数据
     */
    val pageData : StateFlow<IntermediateColorPageData> = _pageData

    private val _pickColor = MutableStateFlow(_pageData.value.answerColor.clone())
    val pickColor: StateFlow<HSB> = _pickColor

    fun switchDifficulty(difficulty: IntermediateColorResult.Difficulty) {
        if (difficulty == _pageData.value.difficulty) {
            return
        }
        Log.d(TAG, "IntermediateColorViewModel#switchDifficulty- diff=$difficulty")
        nextQuestion(difficulty)
    }

    fun nextQuestion(difficulty: IntermediateColorResult.Difficulty = _pageData.value.difficulty) {
        Log.d(TAG, "IntermediateColorViewModel#nextQuestion- diff=$difficulty")
        _pageData.emitBy(createQuestion(difficulty))
    }

    private fun createQuestion(difficulty: IntermediateColorResult.Difficulty = DEFAULT_DIFFICULTY): IntermediateColorPageData {
        Log.d(TAG, "IntermediateColorViewModel#randomRightColor- diff=$difficulty")
        // 先随机个左边的初始色
        val nextLeftColor = HSB.random(0f, difficulty.minSBPercent.toFloat(), difficulty.minSBPercent.toFloat())
        // 基于左边的颜色，生成一个右侧的颜色
        val randomIndex = Random().nextInt(3)
        val nextRightColor = randomRightColor(difficulty, nextLeftColor, randomIndex)
        val answerColor = HSB(180f, 50f, 50f)
        val hsbBarLock = randomHSBBarLock(difficulty, randomIndex, nextLeftColor, answerColor)
        Log.d(TAG, "IntermediateColorViewModel#createQuestion- next left: $nextLeftColor")
        Log.d(TAG, "IntermediateColorViewModel#createQuestion- next right: $nextRightColor")
        Log.d(TAG, "IntermediateColorViewModel#createQuestion- answer: $answerColor")
        Log.d(TAG, "IntermediateColorViewModel#createQuestion- randomIndex=$randomIndex, hsbBarLock=${hsbBarLock.contentToString()}")
        return IntermediateColorPageData(
            difficulty,
            nextLeftColor,
            nextRightColor,
            answerColor,
            randomIndex,
            hsbBarLock
        )
    }

    /**
     * 基于左侧颜色，随机一个右侧颜色出来
     */
    private fun randomRightColor(difficulty: IntermediateColorResult.Difficulty, nextLeftColor: HSB, randomIndex: Int): HSB {
        val colorDifferencePercent: Float
        val nextColor: Float
        val nextRightColor = nextLeftColor.clone()
        when (randomIndex) {
            0 -> {
                colorDifferencePercent = difficulty.colorHDifferencePercent * 1.0f
                nextColor = nextLeftColor.h + colorDifferencePercent * 360 / 100f
                nextRightColor.h = if (nextColor > 360) {
                    nextLeftColor.h - colorDifferencePercent * 360 / 100f
                } else {
                    nextColor
                }
                Log.d(TAG, "IntermediateColorViewModel#randomRightColor- send randomIndex=0")
            }
            1 -> {
                colorDifferencePercent = difficulty.colorSDifferencePercent * 1.0f
                nextColor = nextLeftColor.s + colorDifferencePercent
                nextRightColor.s = if (nextColor > 1) {
                    nextLeftColor.s - colorDifferencePercent
                } else {
                    nextColor
                }
                Log.d(TAG, "IntermediateColorViewModel#randomRightColor- send randomIndex=1")
            }
            2 -> {
                colorDifferencePercent = difficulty.colorBDifferencePercent * 1.0f
                nextColor = nextLeftColor.b + colorDifferencePercent
                nextRightColor.b = if (nextColor > 1) {
                    nextLeftColor.b - colorDifferencePercent
                } else {
                    nextColor
                }
                Log.d(TAG, "IntermediateColorViewModel#randomRightColor- send randomIndex=2")
            }
            else -> {
                Log.d(TAG, "IntermediateColorViewModel#randomRightColor- send randomIndex=-1")
            }
        }
        return nextRightColor
    }

    /**
     * 基于当前难度和随机的差异点，锁定HSB选择器
     */
    private fun randomHSBBarLock(difficulty: IntermediateColorResult.Difficulty,
                                 randomIndex: Int,
                                 nextLeftColor: HSB,
                                 answerColor: HSB): BooleanArray {
        val scope = arrayListOf(0, 1, 2).also { it.removeAt(randomIndex) }
        Log.d(TAG, "IntermediateColorViewModel#lockHSBSelector- scope=${scope.joinToString()}, difficulty=$difficulty")
        val lock = booleanArrayOf(false, false, false)
        repeat(difficulty.fixedNumberOfParameters) {
            if (scope.isEmpty()) {
                return@repeat
            }
            val index = scope.removeAt(Random().nextInt(scope.size))
            lock[index] = true
            answerColor[index] = nextLeftColor[index]
        }
        return lock
    }

    fun updatePickColor(hsbColor: HSB) {
        _pickColor.emitBy(hsbColor.clone())
        _pageData.value.answerColor.update(hsbColor)
    }

    fun saveToDB() {
        val leftColor = pageData.value.questionLeftColor
        val rightColor = pageData.value.questionRightColor
        val answerColor = pageData.value.answerColor
        viewModelScope.launch(GlobalDispatchers.IO) {
            IntermediateColorResultTable.add(IntermediateColorResult(
                difficulty = pageData.value.difficulty,
                questionLeft = leftColor,
                questionRight = rightColor,
                answer = answerColor
            ))
        }
    }
}