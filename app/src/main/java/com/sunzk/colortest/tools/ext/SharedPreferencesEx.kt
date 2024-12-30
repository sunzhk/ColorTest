package com.sunzk.demo.tools.ext

import android.content.Context
import android.content.SharedPreferences

fun sharedPreferences(context: Context, name: String, mode: Int = Context.MODE_PRIVATE): Lazy<SharedPreferences> {
	return object: Lazy<SharedPreferences> {
		
		var sp: SharedPreferences? = null
		
		override val value: SharedPreferences
			get() {
				return sp ?: context.getSharedPreferences(name, mode).also { sp = it }
			}
		
		override fun isInitialized(): Boolean = sp != null

	}
}