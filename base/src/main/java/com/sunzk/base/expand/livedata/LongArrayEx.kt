package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData

operator fun MutableLiveData<LongArray>.get(index: Int): Long {
    return value!![index]
}

operator fun MutableLiveData<LongArray>.iterator(): LongIterator {
    return ArrayLongIterator(value!!)
}

operator fun MutableLiveData<LongArray>.set(index: Int, element: Long): Long {
    value!![index] = element
    postValue(value)
    return element
}

private class ArrayLongIterator(private val array: LongArray) : LongIterator() {
    private var index = 0
    override fun hasNext() = index < array.size
    override fun nextLong() = try {
        array[index++]
    } catch (e: ArrayIndexOutOfBoundsException) {
        index -= 1; throw NoSuchElementException(e.message)
    }
}