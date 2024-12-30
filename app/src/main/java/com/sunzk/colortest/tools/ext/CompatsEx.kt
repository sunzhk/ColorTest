package com.sunzk.demo.tools.ext

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method
import kotlin.properties.Delegates


inline fun <reified T : ViewBinding> Activity.bindView(): Lazy<T> {
	return ViewBinderLazy(T::class.java, this)
}

//fun <T : ViewBinding> Activity.bindView(viewBinding: T): T {
//	setContentView(viewBinding.root)
//	return viewBinding
//}

//inline fun <reified T : ViewBinding> ViewGroup.bindView(): Lazy<T> {
//	return ViewGroupBinderLazy(T::class.java, this)
//}

//fun <T : ViewBinding> ViewGroup.bindView(viewBinding: T): T {
//	addView(viewBinding.root)
//	return viewBinding
//}

class ViewBinderLazy<T : ViewBinding>(private val clazz: Class<T>, val activity: Activity) : Lazy<T> {

	var binder: T? = null

	override val value: T
		get() {
			return binder ?: initValue().also { binder = it }
		}

	override fun isInitialized(): Boolean {
		return binder != null
	}

	private fun initValue(): T {
		val method = clazz.getMethod("inflate", LayoutInflater::class.java)
		return (method.invoke(null, activity.layoutInflater) as T).let {
			activity.setContentView(it.root)
			it
		}
	}
}

//class ViewGroupBinderLazy<T : ViewBinding>(private val clazz: Class<T>, val group: ViewGroup) : Lazy<T> {
//
//	var binder: T? = null
//
//	override val value: T
//		get() {
//			return binder ?: initValue().also { binder = it }
//		}
//
//	override fun isInitialized(): Boolean {
//		return binder != null
//	}
//
//	private fun initValue(): T {
//		var method: Method
//
//		try {
//			method = clazz.getMethod("inflate", LayoutInflater::class.java)
//			return (method.invoke(null, LayoutInflater.from(group.context)) as T).let {
//				group.addView(it.root)
//				it
//			}
//		} catch (t: Throwable) {
//
//		}
//		// 为ViewGroup绑定时，存在merge等情况，需要使用另一个inflate方法
//		method = clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java)
//		return (method.invoke(null, LayoutInflater.from(group.context), group) as T).let {
//			it
//		}
//	}
//}

