package com.sunzk.base.expand.livedata

import androidx.lifecycle.MutableLiveData

operator fun <T: MutableList<U>, U> MutableLiveData<T>.get(i: Int): Any? {
    return value!![i]
}

operator fun <T: MutableList<U>, U> MutableLiveData<T>.set(index: Int, element: U) {
    value!![index] = element
    postValue(value)
}