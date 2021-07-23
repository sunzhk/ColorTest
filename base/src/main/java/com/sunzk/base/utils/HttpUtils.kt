package com.sunzk.base.utils

import com.sunzk.base.utils.Logger.e
import com.sunzk.base.utils.StringUtils.isEmpty
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

/**
 * http 相关工具
 */
object HttpUtils {
    private const val TAG = "HttpUtils"

    /**
     * URL 重编码
     *
     * @param url 需要重编码的 URL
     * @return 重编码后的 URL
     */
    fun encodeUrl(url: String): String {
        try {
            return URLEncoder.encode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e(TAG, "HttpUtils#encodeUrl- ", e)
        }
        return url
    }

    /**
     * 从 URL 中解析 BaseUrl
     *
     * @param url 要解析的url
     * @return baseUrl：比如 http://ip:port
     */
    fun getBaseUrl(url: String): String {
        var url = url
        var head = ""
        var index = url.indexOf("://")
        if (index != -1) {
            head = url.substring(0, index + 3)
            url = url.substring(index + 3)
        }
        index = url.indexOf("/")
        if (index != -1) {
            url = url.substring(0, index + 1)
        }
        return head + url
    }

    /**
     * 在主请求地址的基础上加请求参数获得最终的请求地址(针对GET的请求)
     *
     * @param mainUrl
     * @param requestParams
     * @return
     */
    fun getRequestUrl(mainUrl: String, requestParams: Map<*, *>?): String {
        if (isEmpty(mainUrl) || requestParams == null || requestParams.isEmpty()) {
            e(TAG, "getRequestUrl##Conditions not suitable!")
            return ""
        }

        //不存在请求参数
        val index = mainUrl.indexOf("?")
        return if (index == -1) {
            val iterator = requestParams.keys.iterator()
            val stringBuilder = StringBuilder()
            var key: String?
            while (iterator.hasNext()) {
                key = iterator.next() as String?
                stringBuilder.append("&").append(key).append("=").append(requestParams[key])
            }
            mainUrl + "?" + stringBuilder.toString().substring(1)
        } else { //存在
            //获取存在的请求参数
            var paramsString = mainUrl.substring(index + 1)
            //实际的不存在请求参数的请求地址
            val actualUrl = mainUrl.substring(0, index)
            val stringBuilder = StringBuilder()
            val existKeys: MutableList<String> =
                ArrayList()

            //判断新请求参数是否已经存在，存在就赋最新的值
            val paramsStringArray = paramsString.split("&").toTypedArray()
            for (i in paramsStringArray.indices) {
                val iterator = requestParams.keys.iterator()
                var key: String
                while (iterator.hasNext()) {
                    key = iterator.next() as String
                    if (paramsStringArray[i].indexOf(key) != -1 && paramsStringArray[i].startsWith("$key=")) {
                        existKeys.add(key)
                        paramsStringArray[i] = key + "=" + requestParams[key]
                        break
                    }
                }
                stringBuilder.append("&").append(paramsStringArray[i])
            }
            val iterator = requestParams.keys.iterator()
            var key: String?
            while (iterator.hasNext()) {
                key = iterator.next() as String?
                if (!existKeys.contains(key)) {
                    stringBuilder.append("&").append(key).append("=").append(requestParams[key])
                }
            }
            paramsString = stringBuilder.toString().substring(1)
            "$actualUrl?$paramsString"
        }
    }

    /**
     * 请求头的value中如果有非法字符，会导致OkHttp抛出异常。用这个方法处理非法字符
     * @param value 需要处理的value
     * @param replaceChar 用于替换非法字符
     *
     * @author liuyj
     */
    fun getDefaultHeaderValue(value: String?, replaceChar: String?): String {
        if (value != null) {
            val stringBuilder = StringBuilder()
            val charArray = value.toCharArray()
            for (i in charArray.indices) {
                val c = charArray[i]
                if (c <= '\u001f' || c >= '\u007f') {
                    stringBuilder.append(replaceChar)
                } else {
                    stringBuilder.append(c)
                }
            }
            return stringBuilder.toString()
        }
        return ""
    }
}