package com.sunzk.demo.tools.ext

import android.util.Log
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method

fun Any.invoke(methodName: String, params: Array<Any?>?, parameterTypes: Array<Class<*>>?): Any? {
	
	val TAG = "Any.invoke"

	val method: Method = try {
		if (parameterTypes == null || parameterTypes.isEmpty()) {
			this.javaClass.getMethod(methodName)
		} else {
			this.javaClass.getMethod(methodName, *parameterTypes)
		}
	} catch (e: Exception) {
		Log.w(TAG, "invoke: find method fail", e)
		null
	} ?: try {
		if (parameterTypes == null || parameterTypes.isEmpty()) {
			this.javaClass.getDeclaredMethod(methodName)
		} else {
			this.javaClass.getDeclaredMethod(methodName, *parameterTypes)
		}
	} catch (e: Exception) {
		Log.w(TAG, "invoke: find declared method fail", e)
		null
	} ?: throw NoSuchMethodException("找不到方法$methodName")

	method.isAccessible = true
	return if (params == null || params.isEmpty()) method.invoke(this) else method.invoke(this, params)
}