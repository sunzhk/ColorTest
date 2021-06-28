package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData

/**
 * 对MutableLiveData的扩展，便于取反
 */
object BooleanEx {
    /*    not 取反    */
    operator fun MutableLiveData<Boolean>.not(): MutableLiveData<Boolean> {
        this.value?.let {
            postValue(!it)
        }
        return this
    }
}