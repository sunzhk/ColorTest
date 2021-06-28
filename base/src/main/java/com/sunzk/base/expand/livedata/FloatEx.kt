package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData

/**
 * MutableLiveData的扩展，便于进行简单的四则运算
 */
object FloatEx {

    /**
     * 加法
     */
    operator fun MutableLiveData<Float>.plus(other: Int): MutableLiveData<Float> {
        this.value?.let {
            postValue(it + other)
        }
        return this
    }

    /**
     * 加法
     */
    operator fun MutableLiveData<Float>.plus(other: Float): MutableLiveData<Float> {
        this.value?.let {
            postValue(it + other)
        }
        return this
    }

    /**
     * 加法
     */
    operator fun MutableLiveData<Float>.plus(other: Long): MutableLiveData<Float> {
        this.value?.let {
            postValue(it + other)
        }
        return this
    }

    /**
     * 减法
     */
    operator fun MutableLiveData<Float>.minus(other: Int): MutableLiveData<Float> {
        this.value?.let {
            postValue(it - other)
        }
        return this
    }

    /**
     * 减法
     */
    operator fun MutableLiveData<Float>.minus(other: Float): MutableLiveData<Float> {
        this.value?.let {
            postValue(it - other)
        }
        return this
    }

    /**
     * 减法
     */
    operator fun MutableLiveData<Float>.minus(other: Long): MutableLiveData<Float> {
        this.value?.let {
            postValue(it - other)
        }
        return this
    }

    /**
     * 乘法
     */
    operator fun MutableLiveData<Float>.times(other: Int): MutableLiveData<Float> {
        this.value?.let {
            postValue(it * other)
        }
        return this
    }

    /**
     * 乘法
     */
    operator fun MutableLiveData<Float>.times(other: Float): MutableLiveData<Float> {
        this.value?.let {
            postValue(it * other)
        }
        return this
    }

    /**
     * 乘法
     */
    operator fun MutableLiveData<Float>.times(other: Long): MutableLiveData<Float> {
        this.value?.let {
            postValue(it * other)
        }
        return this
    }

    /**
     * 除法
     */
    operator fun MutableLiveData<Float>.div(other: Int): MutableLiveData<Float> {
        this.value?.let {
            postValue(it / other)
        }
        return this
    }

    /**
     * 除法
     */
    operator fun MutableLiveData<Float>.div(other: Float): MutableLiveData<Float> {
        this.value?.let {
            postValue(it / other)
        }
        return this
    }

    /**
     * 除法
     */
    operator fun MutableLiveData<Float>.div(other: Long): MutableLiveData<Float> {
        this.value?.let {
            postValue(it / other)
        }
        return this
    }

}