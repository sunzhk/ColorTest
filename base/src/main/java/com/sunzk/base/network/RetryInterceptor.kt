package com.sunzk.demo.service

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @param mainHost  主访问地址
 * @param backupHost    备用访问地址
 * @retryCountMainHost  主访问地址的重试次数
 * @retryCountBackupHost    备用访问地址的重试次数
 * @retryIntervalEvaluator   重试时间间隔插值器
 */
class RetryInterceptor(
	val mainHost: String,
	val backupHost: String?,
	val retryCountMainHost: Int = NetworkConstant.DEFAULT_RETRY_COUNT,
	val retryCountBackupHost: Int = NetworkConstant.DEFAULT_RETRY_COUNT,
	val retryIntervalEvaluator: RetryIntervalEvaluator
) : Interceptor {

	constructor(host: String, retryCount: Int = NetworkConstant.DEFAULT_RETRY_COUNT) : this(
		mainHost = host,
		backupHost = null,
		retryCountMainHost = retryCount,
		retryIntervalEvaluator = SimpleRetryIntervalEvaluator()
	)

	override fun intercept(chain: Interceptor.Chain): Response {
		TODO("Not yet implemented")
	}

	interface RetryIntervalEvaluator {
		/**
		 * @param retryCount
		 */
		fun getRetryInterval(retryCount: Int, changedHost: Boolean): Int
	}

	class SimpleRetryIntervalEvaluator(val interval: Int = NetworkConstant.DEFAULT_RETRY_INTERVEL) : RetryIntervalEvaluator {
		override fun getRetryInterval(retryCount: Int, changedHost: Boolean): Int {
			return interval
		}
	}

}