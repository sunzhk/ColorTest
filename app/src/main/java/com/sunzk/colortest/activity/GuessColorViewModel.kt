package com.sunzk.colortest.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GuessColorViewModel : ViewModel() {

    var difficulty = GuessColorActivity.Difficulty.EASY
    val leftColor = MutableLiveData<FloatArray>(FloatArray(3))
    val rightColor = MutableLiveData<FloatArray>(FloatArray(3))
    val centerColor = MutableLiveData<FloatArray>(FloatArray(3))
    
    val showAnswerFlag = MutableLiveData<Boolean>(false)

    fun setShowAnswer(show: Boolean) {
        showAnswerFlag.postValue(show)
    }
    
}