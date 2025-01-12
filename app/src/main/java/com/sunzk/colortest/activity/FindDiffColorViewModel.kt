package com.sunzk.colortest.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FindDiffColorViewModel: ViewModel() {

    companion object {
        const val MIN_LEVEL = 1
        const val DEFAULT_LEVEL = 3
        const val MAX_LEVEL = 10
    }
    
    val currentLevel = MutableLiveData(DEFAULT_LEVEL)

}