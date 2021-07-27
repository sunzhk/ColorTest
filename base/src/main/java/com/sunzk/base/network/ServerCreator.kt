package com.sunzk.base.network

/**
 * 便于创建和使用协程网络请求工具的方法，模仿了ActivityViewModelLazyKt.class
 * TODO 后续可以搭配上服务管理什么的，相同配置的服务搞成单例就好了
 */
public inline fun <reified T> serverCreator(
	baseUrl: String = "",
	noinline settingProducer: (() -> ServerSetting?)? = null
): Lazy<T> {
	val serverInf = T::class.java
	return object : Lazy<T> {
		private var cached: T? = null

		override val value: T
			get() {
				return if (cached == null) {
					CoroutineServer(baseUrl)
						.also { coroutineServer ->
							if (settingProducer != null) {
								settingProducer.invoke()?.let { setting ->
									coroutineServer.applySetting(setting)
								}
							}
						}
						.create(serverInf)
						.also { server ->
							cached = server
						}
				} else {
					cached!!
				}
			}

		override fun isInitialized(): Boolean {
			return cached != null
		}

	}
}
