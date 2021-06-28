package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData

operator fun MutableLiveData<FloatArray>.get(index: Int): Float {
    return value!![index]
}

operator fun MutableLiveData<FloatArray>.iterator(): FloatIterator {
    return ArrayFloatIterator(value!!)
}

operator fun MutableLiveData<FloatArray>.set(index: Int, element: Float): Float {
    value!![index] = element
    postValue(value)
    return element
}

private class ArrayFloatIterator(private val array: FloatArray) : FloatIterator() {
    private var index = 0
    override fun hasNext() = index < array.size
    override fun nextFloat() = try {
        array[index++]
    } catch (e: ArrayIndexOutOfBoundsException) {
        index -= 1; throw NoSuchElementException(e.message)
    }
}