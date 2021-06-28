package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData

operator fun MutableLiveData<IntArray>.get(index: Int): Int {
    return value!![index]
}

operator fun MutableLiveData<IntArray>.iterator(): IntIterator {
    return ArrayIntIterator(value!!)
}

operator fun MutableLiveData<IntArray>.set(index: Int, element: Int): Int {
    value!![index] = element
    postValue(value)
    return element
}

private class ArrayIntIterator(private val array: IntArray) : IntIterator() {
    private var index = 0
    override fun hasNext() = index < array.size
    override fun nextInt() = try {
        array[index++]
    } catch (e: ArrayIndexOutOfBoundsException) {
        index -= 1; throw NoSuchElementException(e.message)
    }
}