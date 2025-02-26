package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData
import com.sunzk.base.expand.emitBy
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * MutableLiveData的扩展，便于进行简单的四则运算
 */

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

fun MutableLiveData<Int>.inc(): MutableLiveData<Int> {
    this.value?.let {
        postValue(it + 1)
    }
    return this
}

fun MutableLiveData<Int>.dec(): MutableLiveData<Int> {
    this.value?.let {
        postValue(it - 1)
    }
    return this
}


fun MutableStateFlow<Int>.inc(): MutableStateFlow<Int> {
    emitBy(this.value + 1)
    return this
}

fun MutableStateFlow<Int>.dec(): MutableStateFlow<Int> {
    emitBy(this.value - 1)
    return this
}