package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData

operator fun <T> MutableLiveData<Array<T>>.iterator(): ArrayIterator<T> {
    return ArrayIterator(value!!)
}

operator fun <T> MutableLiveData<Array<T>>.get(i: Int): Any? {
    return value!![i]
}

operator fun <T> MutableLiveData<Array<T>>.set(index: Int, element: T) {
    value!![index] = element
    postValue(value)
}

class ArrayIterator<T>(private val array: Array<T>) : Iterator<T> {
    private var index = 0
    override fun hasNext() = index < array.size
    override fun next() = try {
        array[index++]
    } catch (e: ArrayIndexOutOfBoundsException) {
        index -= 1; throw NoSuchElementException(e.message)
    }
}