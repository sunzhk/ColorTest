package com.sunzk.base.expand.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

/**
 * 由自定义线程池处理
 */
fun Dispatchers.executorServiceDispatcher(pool: ExecutorService): CoroutineContext {
	return object : CoroutineDispatcher() {
		override fun dispatch(context: CoroutineContext, block: Runnable) {
			pool.execute(block)
		}
	}
}

/**
 * 由Executors.newSingleThreadExecutor()提供的全局单线程处理
 */
fun Dispatchers.singleThreadDispatcher(): CoroutineContext {
	return object : CoroutineDispatcher() {
		val pool: ExecutorService by lazy {
			Executors.newSingleThreadExecutor()
		}

		override fun dispatch(context: CoroutineContext, block: Runnable) {
			pool.execute(block)
		}
	}
}

/**
 * 由一个新的Executors.newSingleThreadExecutor()的单线程处理
 */
fun Dispatchers.newSingleThreadDispatcher(): CoroutineContext {
	return object : CoroutineDispatcher() {
		val pool: ExecutorService = Executors.newSingleThreadExecutor()
		override fun dispatch(context: CoroutineContext, block: Runnable) {
			pool.execute(block)
		}
	}
}