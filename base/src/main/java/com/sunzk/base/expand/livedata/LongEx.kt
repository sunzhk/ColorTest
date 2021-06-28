package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData

/**
 * MutableLiveData的扩展，便于进行简单的四则运算
 */
object LongEx {

    /**
     * 加法
     */
    operator fun MutableLiveData<Long>.plus(other: Int): MutableLiveData<Long> {
        this.value?.let {
            postValue(it + other)
        }
        return this
    }

    /**
     * 加法
     */
    operator fun MutableLiveData<Long>.plus(other: Long): MutableLiveData<Long> {
        this.value?.let {
            postValue(it + other)
        }
        return this
    }

    /**
     * 减法
     */
    operator fun MutableLiveData<Long>.minus(other: Int): MutableLiveData<Long> {
        this.value?.let {
            postValue(it - other)
        }
        return this
    }

    /**
     * 减法
     */
    operator fun MutableLiveData<Long>.minus(other: Long): MutableLiveData<Long> {
        this.value?.let {
            postValue(it - other)
        }
        return this
    }

    /**
     * 乘法
     */
    operator fun MutableLiveData<Long>.times(other: Int): MutableLiveData<Long> {
        this.value?.let {
            postValue(it * other)
        }
        return this
    }

    /**
     * 乘法
     */
    operator fun MutableLiveData<Long>.times(other: Long): MutableLiveData<Long> {
        this.value?.let {
            postValue(it * other)
        }
        return this
    }

    /**
     * 除法
     */
    operator fun MutableLiveData<Long>.div(other: Int): MutableLiveData<Long> {
        this.value?.let {
            postValue(it / other)
        }
        return this
    }

    /**
     * 除法
     */
    operator fun MutableLiveData<Long>.div(other: Long): MutableLiveData<Long> {
        this.value?.let {
            postValue(it / other)
        }
        return this
    }
}