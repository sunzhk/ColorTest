package com.sunzk.colortest.game.mockcolor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunzk.base.expand.coroutines.GlobalDispatchers
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.db.MockColorResultTable
import com.sunzk.colortest.entity.HSB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MockColorViewModel: ViewModel() {
	companion object {
		private const val TAG: String = "MockColorViewModel"
	}

	private val _pageData = MutableStateFlow(MockColorPageData.default())
	/**
	 * 页面数据
	 */
	val pageData : StateFlow<MockColorPageData> = _pageData
	
	private val _pickColor = MutableStateFlow(_pageData.value.pickHSB.clone())
	val pickColor: StateFlow<HSB> = _pickColor
	
	private val _score = MutableStateFlow(Runtime.testResultNumber)
	val score: StateFlow<Int> = _score
	
	init {
		viewModelScope.launch(GlobalDispatchers.IO) {
			Log.d(TAG, "init: try to read db data")
			val testResult = MockColorResultTable.queryAll()
			withContext(GlobalDispatchers.Main) {
				Log.d(TAG, "onNext: " + testResult?.size)
				Runtime.testResultNumber = testResult?.size ?: 0
				_score.emitBy(Runtime.testResultNumber)
			}
		}
	}

	fun switchDifficulty(difficulty: MockColorResult.Difficulty) {
		if (difficulty == _pageData.value.difficulty) {
			return
		}
		Log.d(TAG, "MockColorViewModel#switchDifficulty- diff=$difficulty")
		nextQuestion(difficulty)
	}

    fun nextQuestion(difficulty: MockColorResult.Difficulty = _pageData.value.difficulty) {
	    _pageData.emitBy(MockColorPageData.default(difficulty))
    }
	
	fun updatePickColor(hsb: HSB) {
		Log.d(TAG, "MockColorViewModel#updatePickColor- $hsb")
		_pickColor.emitBy(hsb.clone())
		_pageData.value.pickHSB.update(hsb)
	}

	suspend fun updateScore(question: HSB, answer: HSB) = withContext(GlobalDispatchers.IO) {
		val result = MockColorResultTable.add(MockColorResult(
			difficulty = pageData.value.difficulty,
			question = question,
			answer = answer
		))
		Log.d(TAG, "MockColorViewModel#updateScore- $result")
		_score.emitBy(Runtime.testResultNumber)
	}
}