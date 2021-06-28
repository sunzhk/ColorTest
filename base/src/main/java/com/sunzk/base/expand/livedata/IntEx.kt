package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData

/**
 * MutableLiveData的扩展，便于进行简单的四则运算
 */
object IntEx {

    /**
     * 加法
     */
    operator fun MutableLiveData<Int>.plus(other: Int): MutableLiveData<Int> {
        this.value?.let {
            postValue(it + other)
        }
        return this
    }

    /**
     * 减法
     */
    operator fun MutableLiveData<Int>.minus(other: Int): MutableLiveData<Int> {
        this.value?.let {
            postValue(it - other)
        }
        return this
    }

    /**
     * 乘法
     */
    operator fun MutableLiveData<Int>.times(other: Int): MutableLiveData<Int> {
        this.value?.let {
            postValue(it * other)
        }
        return this
    }

    /**
     * 除法
     */
    operator fun MutableLiveData<Int>.div(other: Int): MutableLiveData<Int> {
        this.value?.let {
            postValue(it / other)
        }
        return this
    }
}