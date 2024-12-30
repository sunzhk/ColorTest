package com.sunzk.demo.tools.coroutine

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * 协程的全局异常处理，敏感的协程操作记得用这个捕获
 */
object GlobalCoroutineExceptionHandler : CoroutineExceptionHandler {

	private const val TAG: String = "GlobalCoroutineExceptionHandler"
	
	var exceptionLoudspeaker: ((context: CoroutineContext, exception: Throwable) -> Unit?)? = null

	override val key: CoroutineContext.Key<*>
		get() = CoroutineExceptionHandler

	override fun handleException(context: CoroutineContext, exception: Throwable) {
		Log.d(TAG, "GlobalCoroutineExceptionHandler#handleException- $context", exception)
		exceptionLoudspeaker?.invoke(context, exception)
	}
}