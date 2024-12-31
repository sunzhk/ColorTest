package com.sunzk.base.expand.coroutines

import kotlinx.coroutines.Dispatchers

object GlobalDispatchers {
	/**
	 * 全局IO线程调度
	 */
	@JvmStatic
	val IO
		get() = Dispatchers.IO + GlobalCoroutineExceptionHandler

	/**
	 * 全局主线程调度
	 */
	@JvmStatic
	val Main
		get() = Dispatchers.Main + GlobalCoroutineExceptionHandler
}