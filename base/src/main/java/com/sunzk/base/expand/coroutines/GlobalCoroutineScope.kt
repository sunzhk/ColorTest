package com.sunzk.base.expand.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 全局IO协程作用域
 */
val GlobalIOScope : CoroutineScope = CoroutineScope(Dispatchers.IO + GlobalCoroutineExceptionHandler)

/**
 * 全局主线程协程作用域
 */
val GlobalMainScope : CoroutineScope = CoroutineScope(Dispatchers.Main + GlobalCoroutineExceptionHandler)

/**
 * 在子线程中执行
 */
fun runOnIOThread(block: () -> Unit) {
	GlobalIOScope.launch {
		block.invoke()
	}
}

/**
 * 在主线程中执行
 */
fun runOnMainThread(block: () -> Unit) {
	GlobalMainScope.launch {
		block.invoke()
	}
}