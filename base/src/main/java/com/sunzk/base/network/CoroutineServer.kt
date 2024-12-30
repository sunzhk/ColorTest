package com.sunzk.base.network

import android.util.Log
import com.google.gson.Gson
import com.sunzk.base.expand.invoke
import com.sunzk.base.network.annotations.Get
import com.sunzk.base.network.annotations.Post
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStreamReader
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation

/**
 * 协程+网络请求类
 * @param baseUrl 服务器地址
 */
class CoroutineServer(private val baseUrl: String) {

	private val TAG: String = "CoroutineServer"

	private val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

	private lateinit var client: OkHttpClient

	/**
	 * 设置超时时长
	 */
	fun setTimeout(
		callTimeout: Long = 0,
		connectTimeout: Long = 0,
		readTimeout: Long = 0,
		writeTimeout: Long = 0
	) {
		clientBuilder.callTimeout(callTimeout, TimeUnit.MILLISECONDS)
		clientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
		clientBuilder.readTimeout(readTimeout, TimeUnit.MILLISECONDS)
		clientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
	}

	/**
	 * 依据传入的接口类，实现网络服务实例
	 */
	fun <T> create(clazz: Class<T>): T {
		Log.d(TAG, "create: ${clazz.name}")
		client = clientBuilder.build()
		return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz), object : InvocationHandler {
			override fun invoke(proxy: Any, method: Method, params: Array<Any>?): Any? {
				Log.d(TAG, "invoke: ${clazz.name}-${method.name}")
				var result: Any? = null
				// TODO: 2021/7/28 这边的协程怎么用还要再想想
				runBlocking {
					withContext(Dispatchers.IO) {
						// 判断是Get请求还是Post请求。其他复杂的东西回头再加
						val getRequest = method.getAnnotation(Get::class.java)
						val postRequest = method.getAnnotation(Post::class.java)
						Log.d(TAG, "invoke: getRequest=$getRequest, postRequest=$postRequest")
						// 获取该方法真正的返回值类型，以便反序列化数据
						val returnType = findRealReturnType(method)
						Log.d(TAG, "invoke: 所以最终的返回值类型为$returnType")
						result = when {
							getRequest != null -> {
								Log.d(TAG, "invoke: do get!")
								handleAsGetRequest(getRequest, method, returnType, params)
							}
							postRequest != null -> {
								Log.d(TAG, "invoke: do post!")
								handleAsPostRequest(postRequest, method, params)
							}
							else -> {
								throw IllegalArgumentException("错误的请求类型")
							}
						}
					}
				}
				return result
			}
		}
		) as T
	}

	/**
	 * 处理Get请求
	 */
	private suspend fun handleAsGetRequest(
		getRequest: Get?,
		method: Method,
		returnType: Type?,
		params: Array<Any>?
	): Any? {
		val newCall =
			client.newCall(Request.Builder().url(spliceUrl(baseUrl, getRequest?.path).also { Log.d(TAG, "handleAsGetRequest: url=$it") }).build())
		val response = newCall.waitResponse()
		Log.d(TAG, "handleAsGetRequest: return type=$returnType")
		return Gson().fromJson(InputStreamReader(response.body?.byteStream()), returnType)
	}

	/**
	 * 处理Post请求
	 */
	private fun handleAsPostRequest(postRequest: Post?, method: Method, params: Array<Any>?): Any? {
		Log.v(TAG, "handleAsPostRequest: ${postRequest?.path}")
		return null
	}


	/**
	 * 拼接URL，先简单的拼一下，不考虑Retrofit那种复杂的方式
	 */
	private fun spliceUrl(baseUrl: String, path: String?, vararg params: Pair<String, String>?): String {
		return baseUrl + path
	}

	/**
	 * 获取方法的返回值类型，以便反序列化数据<br>
	 *
	 * 目标方法带有suspend关键词时，
	 * 生成的字节码中方法的返回值会变为Object，
	 * 并在方法参数的最后增加参数Continuation，
	 * 其中保存了方法真正的返回值类型
	 */
	private fun findRealReturnType(method: Method): Type? {
		Log.v(TAG, "findRealReturnType: $method")
		if (method.returnType != Object::class.java) {
			return method.genericReturnType
		}

		Log.v(TAG, "findRealReturnType: 疑似带有suspend关键词，需要读取参数进行二次判断")
		if (method.genericParameterTypes.isEmpty()) {
			Log.v(TAG, "findRealReturnType: 无参数，与suspend关键词无关")
			return method.genericReturnType
		}
		method.genericParameterTypes[method.genericParameterTypes.size - 1]?.let { type: Type ->
			Log.v(TAG, "handleAsGetRequest: last param is $type")
			if (type !is ParameterizedType) {
				Log.v(TAG, "findRealReturnType: 最后一个参数的Type不是ParameterizedType，无法判断")
				return method.genericReturnType
			}
			if (type.rawType != Continuation::class.java) {
				Log.v(TAG, "findRealReturnType: 最后一个参数的类型并不是Continuation，与suspend无关")
				return method.genericReturnType
			}
			type.actualTypeArguments.let { actualTypeArguments ->
				if (actualTypeArguments.isEmpty()) {
					Log.v(TAG, "findRealReturnType: Continuation没有携带泛型，无返回值")
					return null
				}
				actualTypeArguments[0].let { realType ->
					Log.v(TAG,
						"handleAsGetRequest: real return type: $realType is ${realType.javaClass.name}")
					when {
						// 常规情况，直接拿到ParameterizedType
						realType is ParameterizedType -> {
							Log.v(TAG, "findRealReturnType: 是ParameterizedType，很顺利")
							return realType.rawType
						}
						// 特殊情况，拿到了libcore.reflect.WildcardTypeImpl
						"libcore.reflect.WildcardTypeImpl" == realType.javaClass.name -> {
							Log.v(TAG, "findRealReturnType: 是坑爹的libcore.reflect.WildcardTypeImpl")
							val lowerBounds: Array<Type>? = realType.invoke("getLowerBounds", null, null) as Array<Type>?
							Log.v(TAG,"findRealReturnType: 尝试拿到了lowerBounds=${Arrays.toString(lowerBounds)}")
							val lowerBoundType = lowerBounds?.get(0)
							if (lowerBoundType != null) {
								return lowerBoundType
							}
						}
						// 未知情况，遇到了再说吧
						else -> {
							Log.i(TAG, "findRealReturnType: 无法取得真实的Class，抛出异常，以下是方法列表")
							realType.javaClass.methods.forEach {
								Log.v(TAG, "findRealReturnType: $it")
							}
							realType.javaClass.declaredMethods.forEach {
								Log.v(TAG, "findRealReturnType: $it")
							}
						}
					}
				}
			}
		}
		throw IllegalArgumentException("无法获得接口的返回值，请联系开发者")
	}

	/**
	 * 设置网络请求各项参数，必须在调用create方法前设置
	 */
	fun applySetting(setting: ServerSetting) {
		setTimeout(setting.callTimeout,
			setting.connectTimeout,
			setting.readTimeout,
			setting.writeTimeout)
	}

}
