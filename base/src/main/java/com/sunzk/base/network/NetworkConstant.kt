package com.sunzk.demo.service

object NetworkConstant {

	/**
	 * 默认重试次数
	 * @see com.sunzk.rxhttp.utils.RetryInterceptor
	 */
	val DEFAULT_RETRY_COUNT: Int = 3
	/**
	 * 默认重试时间间隔
	 * @see com.sunzk.rxhttp.utils.RetryInterceptor.SimpleRetryIntervalEvaluator
	 */
	val DEFAULT_RETRY_INTERVEL: Int = 1000
}