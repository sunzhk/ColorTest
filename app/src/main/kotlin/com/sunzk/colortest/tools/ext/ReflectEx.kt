package com.sunzk.colortest.tools.ext

import android.util.Log

private const val TAG: String = "ReflectEx"

fun <T> Any.getField(name: String): T? {
	try {
		val javaClass = this::class.java
		val field = javaClass.getDeclaredField(name)
		field.isAccessible = true
		@Suppress("UNCHECKED_CAST")
		return field.get(this) as T
	} catch (t: Throwable) {
		Log.e(TAG, "getField error", t)
	}
	return null
}