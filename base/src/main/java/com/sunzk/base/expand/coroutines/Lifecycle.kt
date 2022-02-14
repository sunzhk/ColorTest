package com.sunzk.base.expand.coroutines

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 跟随依赖的上下文的生命周期执行协程。
 * 如果控件尚未被添加到视图中，则需要？
 * TODO: 2021/8/6 当无法获取到生命周期时，是应该抛出异常呢还是想办法让他执行下去？
 */
fun View.launch(
	context: CoroutineContext = EmptyCoroutineContext,
	start: CoroutineStart = CoroutineStart.DEFAULT,
	block: suspend CoroutineScope.() -> Unit
): Job {
	return findViewTreeLifecycleOwner()?.lifecycleScope?.launch (context, start, block) ?: CoroutineScope(Dispatchers.Main).launch(context, start, block)
}