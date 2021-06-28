package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData

/**
 * MutableLiveData的扩展，便于进行简单的四则运算
 */
object DoubleEx {

    /**
     * 加法
     */
    operator fun MutableLiveData<Double>.plus(other: Int): MutableLiveData<Double> {
        this.value?.let {
            postValue(it + other)
        }
        return this
    }
    /**
     * 加法
     */
    operator fun MutableLiveData<Double>.plus(other: Float): MutableLiveData<Double> {
        this.value?.let {
            postValue(it + other)
        }
        return this
    }
    /**
     * 加法
     */
    operator fun MutableLiveData<Double>.plus(other: Long): MutableLiveData<Double> {
        this.value?.let {
            postValue(it + other)
        }
        return this
    }
    /**
     * 加法
     */
    operator fun MutableLiveData<Double>.plus(other: Double): MutableLiveData<Double> {
        this.value?.let {
            postValue(it + other)
        }
        return this
    }

    /**
     * 减法
     */
    operator fun MutableLiveData<Double>.minus(other: Int): MutableLiveData<Double> {
        this.value?.let {
            postValue(it - other)
        }
        return this
    }
    /**
     * 减法
     */
    operator fun MutableLiveData<Double>.minus(other: Float): MutableLiveData<Double> {
        this.value?.let {
            postValue(it - other)
        }
        return this
    }
    /**
     * 减法
     */
    operator fun MutableLiveData<Double>.minus(other: Long): MutableLiveData<Double> {
        this.value?.let {
            postValue(it - other)
        }
        return this
    }
    /**
     * 减法
     */
    operator fun MutableLiveData<Double>.minus(other: Double): MutableLiveData<Double> {
        this.value?.let {
            postValue(it - other)
        }
        return this
    }

    /**
     * 乘法
     */
    operator fun MutableLiveData<Double>.times(other: Int): MutableLiveData<Double> {
        this.value?.let {
            postValue(it * other)
        }
        return this
    }
    /**
     * 乘法
     */
    operator fun MutableLiveData<Double>.times(other: Float): MutableLiveData<Double> {
        this.value?.let {
            postValue(it * other)
        }
        return this
    }
    /**
     * 乘法
     */
    operator fun MutableLiveData<Double>.times(other: Long): MutableLiveData<Double> {
        this.value?.let {
            postValue(it * other)
        }
        return this
    }
    /**
     * 乘法
     */
    operator fun MutableLiveData<Double>.times(other: Double): MutableLiveData<Double> {
        this.value?.let {
            postValue(it * other)
        }
        return this
    }

    /**
     * 除法
     */
    operator fun MutableLiveData<Double>.div(other: Int): MutableLiveData<Double> {
        this.value?.let {
            postValue(it / other)
        }
        return this
    }
    /**
     * 除法
     */
    operator fun MutableLiveData<Double>.div(other: Float): MutableLiveData<Double> {
        this.value?.let {
            postValue(it / other)
        }
        return this
    }
    /**
     * 除法
     */
    operator fun MutableLiveData<Double>.div(other: Long): MutableLiveData<Double> {
        this.value?.let {
            postValue(it / other)
        }
        return this
    }
    /**
     * 除法
     */
    operator fun MutableLiveData<Double>.div(other: Double): MutableLiveData<Double> {
        this.value?.let {
            postValue(it / other)
        }
        return this
    }
}