package com.sunzk.demo.service

import com.sunzk.base.network.waitResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class BaseService(init: ((clientBuilder: OkHttpClient.Builder) -> Unit?)? = null) {

	private val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

	private val client: OkHttpClient

	init {
		init?.invoke(clientBuilder)
		client = clientBuilder.build()
	}

	@ExperimentalCoroutinesApi
	suspend fun request(url: String): Response = withContext(Dispatchers.IO) {
		val request = Request.Builder()
			.url(url)
			.build()
		request(request)
	}

	@ExperimentalCoroutinesApi
	suspend fun request(request: Request): Response = withContext(Dispatchers.IO)  {
		val call = client.newCall(request)
		call.waitResponse()
	}


//	
//	/**
//	 * 设置连接超时时长。单位：毫秒
//	 */
//	fun setConnectTimeout(timeout: Long) {
//		clientBuilder.connectTimeout(timeout, TimeUnit.MILLISECONDS)
//	}
//
//	/**
//	 * 设置读超时时长。单位：毫秒
//	 */
//	fun setReadTimeout(timeout: Long) {
//		clientBuilder.readTimeout(timeout, TimeUnit.MILLISECONDS)
//	}
//
//	/**
//	 * 设置写超时时长。单位：毫秒
//	 */
//	fun setWriteTimeout(timeout: Long) {
//		clientBuilder.writeTimeout(timeout, TimeUnit.MILLISECONDS)
//	}
//
//	/**
//	 * 设置重试拦截器
//	 */
//	fun setRetryConfig(mainHost: String, retryCountMainHost: Int = NetworkConstant.DEFAULT_RETRY_COUNT) {
//		this.setRetryConfig(mainHost = mainHost, backupHost = null, retryCountMainHost = retryCountMainHost)
//	}
//
//	/**
//	 * 设置重试拦截器
//	 */
//	fun setRetryConfig(mainHost: String,
//	                   backupHost: String?,
//	                   retryCountMainHost: Int = NetworkConstant.DEFAULT_RETRY_COUNT,
//	                   retryCountBackupHost: Int = NetworkConstant.DEFAULT_RETRY_COUNT,
//	                   retryIntervalEvaluator: RetryInterceptor.RetryIntervalEvaluator = RetryInterceptor.SimpleRetryIntervalEvaluator()
//	) {
//		val retryInterceptor = RetryInterceptor(mainHost, backupHost, retryCountMainHost, retryCountBackupHost, retryIntervalEvaluator)
//		clientBuilder.addInterceptor(retryInterceptor)
//	}
//
//	/**
//	 * 设置拦截器
//	 */
//	fun addInterceptor(interceptor: Interceptor) {
//		clientBuilder.addInterceptor(interceptor)
//	}
}