package com.sunzk.colortest.mockColor

import android.util.Log
import androidx.lifecycle.ViewModel
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.db.bean.IntermediateColorResult
import com.sunzk.colortest.db.bean.MockColorResult
import com.sunzk.colortest.intermediateColor.IntermediateColorViewModel
import com.sunzk.colortest.intermediateColor.IntermediateColorViewModel.Companion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockColorViewModel: ViewModel() {
	companion object {
		private const val TAG: String = "MockColorViewModel"
	}

	private val _pageData = MutableStateFlow(MockColorPageData.default())
	/**
	 * 页面数据
	 */
	val pageData : StateFlow<MockColorPageData> = _pageData

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
}