package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData

operator fun MutableLiveData<DoubleArray>.get(index: Int): Double {
    return value!![index]
}

operator fun MutableLiveData<DoubleArray>.iterator(): DoubleIterator {
    return ArrayDoubleIterator(value!!)
}

operator fun MutableLiveData<DoubleArray>.set(index: Int, element: Double): Double {
    value!![index] = element
    postValue(value)
    return element
}

private class ArrayDoubleIterator(private val array: DoubleArray) : DoubleIterator() {
    private var index = 0
    override fun hasNext() = index < array.size
    override fun nextDouble() = try {
        array[index++]
    } catch (e: ArrayIndexOutOfBoundsException) {
        index -= 1; throw NoSuchElementException(e.message)
    }
}