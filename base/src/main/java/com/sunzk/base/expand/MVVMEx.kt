package com.sunzk.base.expand

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method

/**
 * 为Activity绑定ViewBinding
 */
inline fun <reified T : ViewBinding> Activity.bindView(): Lazy<T> {
	return ActivityBinderLazy(T::class.java, this)
}

class ActivityBinderLazy<T : ViewBinding>(private val clazz: Class<T>, val activity: Activity) : Lazy<T> {

	var binder: T? = null

	override val value: T
		get() {
			return binder ?: initValue().also { binder = it }
		}

	override fun isInitialized(): Boolean {
		return binder != null
	}

	@Suppress("UNCHECKED_CAST")
	private fun initValue(): T {
		val method = clazz.getMethod("inflate", LayoutInflater::class.java)
		return (method.invoke(null, activity.layoutInflater) as T).also {
			activity.setContentView(it.root)
		}
	}
}

/**
 * 为ViewGroup绑定ViewBinding
 */
inline fun <reified T : ViewBinding> ViewGroup.bindView(): Lazy<T> {
	return ViewGroupBinderLazy(T::class.java, this)
}

class ViewGroupBinderLazy<T : ViewBinding>(private val clazz: Class<T>, val group: ViewGroup) : Lazy<T> {

	var binder: T? = null

	override val value: T
		get() {
			return binder ?: initValue().also { binder = it }
		}

	override fun isInitialized(): Boolean {
		return binder != null
	}
	@Suppress("UNCHECKED_CAST")
	private fun initValue(): T {
		var method: Method

		try {
			method = clazz.getMethod("inflate", LayoutInflater::class.java)
			return (method.invoke(null, LayoutInflater.from(group.context)) as T).let {
				group.addView(it.root)
				it
			}
		} catch (_: Throwable) {

		}
		// 为ViewGroup绑定时，存在merge等情况，需要使用另一个inflate方法
		method = clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java)
		return (method.invoke(null, LayoutInflater.from(group.context), group) as T)
	}
}

inline fun <reified T : ViewBinding> Fragment.bindView(): Lazy<T> {
	return FragmentBinderLazy(T::class.java, this)
}

class FragmentBinderLazy<T : ViewBinding>(private val clazz: Class<T>, val fragment: Fragment) : Lazy<T> {

	var binder: T? = null

	override val value: T
		get() {
			return binder ?: initValue().also { binder = it }
		}

	override fun isInitialized(): Boolean {
		return binder != null
	}

	@Suppress("UNCHECKED_CAST")
	private fun initValue(): T {
		val method = clazz.getMethod("inflate", LayoutInflater::class.java)
		return (method.invoke(null, fragment.layoutInflater) as T)
	}
}

/**
 * 为Dialog绑定ViewBinding
 */
inline fun <reified T : ViewBinding> Dialog.bindView(): Lazy<T> {
	return DialogBinderLazy(T::class.java, this)
}

class DialogBinderLazy<T : ViewBinding>(private val clazz: Class<T>, val dialog: Dialog) : Lazy<T> {

	var binder: T? = null

	override val value: T
		get() {
			return binder ?: initValue().also { binder = it }
		}

	override fun isInitialized(): Boolean {
		return binder != null
	}

	@Suppress("UNCHECKED_CAST")
	private fun initValue(): T {
		val method = clazz.getMethod("inflate", LayoutInflater::class.java)
		return (method.invoke(null, dialog.layoutInflater) as T).let {
			dialog.setContentView(it.root)
			it
		}
	}
}
