package com.sunzk.colortest.findSameColor

import android.util.Range
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunzk.base.expand.emitBy
import com.sunzk.base.expand.livedata.inc
import com.sunzk.colortest.entity.HSB
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class FindSameColorViewModel: ViewModel() {
	companion object {
		private const val TAG: String = "FindSameColorViewModel"
		private const val GAME_TIME = 15_000L
	}
	
	private val _pageData = MutableStateFlow(newPageData(FindSameColorResult.Difficulty.Normal))
	val pageData: StateFlow<FindSameColorPageData> = _pageData

	val gameCountDown = MutableStateFlow(GAME_TIME)

	/**
	 * 游戏状态
	 */
	private val _gameState = MutableStateFlow(GameState.Ready)
	val gameState: StateFlow<GameState> = _gameState

	/**
	 * 点击次数
	 */
	val clickCount = MutableStateFlow(0)

	/**
	 * 正确次数
	 */
	val rightCount = MutableStateFlow(0)

	val difficulty: FindSameColorResult.Difficulty
		get() = pageData.value.difficulty
	
	val exampleColor: HSB
		get() = pageData.value.exampleColor

	private var wrongClickCount = 0
	private val _wrongClickAlert = MutableStateFlow(false)
	val wrongClickAlert : StateFlow<Boolean> = _wrongClickAlert
	
	private var countTimeJob: Job? = null
	
	fun prepareGame() {
		resetWrongClick()
		_gameState.emitBy(GameState.Ready)
		countTimeJob?.cancel()
		gameCountDown.emitBy(GAME_TIME)
		_wrongClickAlert.emitBy(false)
	}
	
	fun startNewGame(difficulty: FindSameColorResult.Difficulty) {
		resetWrongClick()
		clickCount.emitBy(0)
		rightCount.emitBy(0)
		nextQuestion(difficulty)
		startPlayGame()
	}
	
	fun nextQuestion(difficulty: FindSameColorResult.Difficulty = _pageData.value.difficulty) {
		wrongClickCount = 0
		_pageData.emitBy(newPageData(difficulty))
	}

	/**
	 * 按给定的难度生成一个新的页面数据
	 */
	private fun newPageData(difficulty: FindSameColorResult.Difficulty): FindSameColorPageData {
		val exampleColor = HSB.random(0f, 360f, 30f, 90f, 30f, 90f)
		return FindSameColorPageData(
			difficulty = difficulty,
			exampleColor = exampleColor,
			boxColors = randomBoxColors(difficulty, exampleColor)
		)
	}

	/**
	 * 随机一个列表出来
	 */
	private fun randomBoxColors(difficulty: FindSameColorResult.Difficulty, exampleColor: HSB): List<HSB> {
		val list = ArrayList<HSB>()
		// 先把正确的颜色加进去
		list.add(exampleColor.clone())
		var temp: HSB
		repeat(difficulty.boxNumber - 1) {
			temp = exampleColor.clone()
			do {
				if (Random.nextBoolean()) {
					temp.h = randomValue(exampleColor.h.toInt(), difficulty.hDiffRange, HSB.COLOR_H_MIN, HSB.COLOR_H_MAX).toFloat()
				}
				if (Random.nextBoolean()) {
					temp.s = randomValue(exampleColor.s.toInt(), difficulty.sDiffRange, HSB.COLOR_S_MIN, HSB.COLOR_S_MAX).toFloat()
				}
				if (Random.nextBoolean()) {
					temp.b = randomValue(exampleColor.b.toInt(), difficulty.bDiffRange, HSB.COLOR_B_MIN, HSB.COLOR_B_MAX).toFloat()
				}
			} while (list.contains(temp))
			list.add(temp)
		}
		// 打乱list的顺序
		list.shuffle()
		return list
	}

	/**
	 * 基于难度指定的颜色参数差异，随机生成一个新的颜色参数
	 */
	private fun randomValue(start: Int, range: Range<Int>, min: Int, max: Int): Int {
		val randomValue = Random.nextInt(range.lower, range.upper)
		return if (start + randomValue > max) {
			start - randomValue
		} else if (start - randomValue < min) {
			start + randomValue
		} else {
			if (Random.nextBoolean()) {
				start + randomValue
			} else {
				start - randomValue
			}
		}
	}

	private fun startPlayGame() {
		countTimeJob = viewModelScope.launch {
			resetWrongClick()
			_gameState.emitBy(GameState.Playing)
			var lastTime = GAME_TIME
			do {
				gameCountDown.emitBy(lastTime)
				delay(999)
				lastTime -= 1000
			} while (gameCountDown.value > 0)
			resetWrongClick()
			_gameState.emitBy(GameState.Over)
		}
	}
	
	/**
	 * 记一下点错了几次，瞎点我可就要骂人了
	 */
	fun onColorClick(isRight: Boolean) {
		clickCount.inc()
		if (isRight) {
			rightCount.inc()
		} else {
			wrongClickCount++
		}
		if (wrongClickCount >= (difficulty.boxNumber / 2)) {
			wrongClickCount = 0
//			改成计时计分模式了，就不要纠结连点了几次了吧
//			_wrongClickAlert.emitBy(true)
		}
	}
	
	fun resetWrongClick() {
		wrongClickCount = 0
		_wrongClickAlert.emitBy(false)
	}

	/**
	 * 计算一下正确率
	 */
	fun correctRatePercent(): Float {
		if (clickCount.value <= 0) {
			return 0f
		}
		return rightCount.value.toFloat() * 100f / clickCount.value.toFloat()
	}

	enum class GameState {
		Ready, Playing, Over
	}
}