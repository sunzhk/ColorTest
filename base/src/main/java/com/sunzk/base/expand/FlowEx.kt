package com.sunzk.base.expand

import com.sunzk.base.expand.coroutines.GlobalCoroutineExceptionHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> FlowCollector<T>.emitBy(value: T, context: CoroutineContext = Dispatchers.IO) {
	val collector: FlowCollector<T> = this
	CoroutineScope(context + GlobalCoroutineExceptionHandler).launch {
		collector.emit(value)
	}
}

fun <T> FlowCollector<T>.emitIn(value: T, scope: CoroutineScope = CoroutineScope(Dispatchers.IO + GlobalCoroutineExceptionHandler)) {
	val collector: FlowCollector<T> = this
	scope.launch {
		collector.emit(value)
	}
}

fun <T> Flow<T>.collect(scope: CoroutineScope, action: suspend (value: T) -> Unit): Job {
	return scope.launch(GlobalCoroutineExceptionHandler) {
		collect(action)
	}
}

fun <T> Flow<T>.collect(scope: CoroutineScope, context: CoroutineContext = EmptyCoroutineContext, action: suspend (value: T) -> Unit) {
	scope.launch(GlobalCoroutineExceptionHandler) { 
		withContext(context) {
			collect(action)
		}
	}
}

/**
 * 使用Flow实现一个倒计时功能
 */

const val ADD_DEVICE_CHECK_TIMEOUT = 180000L

fun countDownByFlow(
	max: Int,
	scope: CoroutineScope,
	onTick: ((Int) -> Unit)? = null,
	onFinish: (() -> Unit)? = null,
): Job {
	return flow {
		for (num in max downTo 0) {
			emit(num)
			if (num != 0) delay(1000)
		}
	}.flowOn(Dispatchers.Main)
		.onEach { onTick?.invoke(it) }
		.onCompletion { cause -> if (cause == null) onFinish?.invoke() }
		.launchIn(scope) //保证在一个协程中执行
}