package com.sunzk.base.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@ExperimentalCoroutinesApi
suspend fun okhttp3.Call.waitResponse(): okhttp3.Response {

	return suspendCancellableCoroutine { cancelable ->

		cancelable.invokeOnCancellation {
			//当协程被取消的时候，取消网络请求
			cancel()
		}

		enqueue(object : okhttp3.Callback {
			override fun onFailure(call: okhttp3.Call, e: IOException) {
				cancelable.resumeWithException(e)
			}

			override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
				cancelable.resume(response)
			}
		})
	}
}