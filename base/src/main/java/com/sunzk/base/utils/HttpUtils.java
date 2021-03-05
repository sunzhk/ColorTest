package com.sunzk.base.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * http 相关工具
 */
public class HttpUtils {

	private static final String TAG = "HttpUtils";

	/**
	 * URL 重编码
	 *
	 * @param url 需要重编码的 URL
	 * @return 重编码后的 URL
	 */
	public static String encodeUrl(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Logger.e(TAG, "HttpUtils#encodeUrl- ", e);
		}
		return url;
	}

	/**
	 * 从 URL 中解析 BaseUrl
	 *
	 * @param url 要解析的url
	 * @return baseUrl：比如 http://ip:port
	 */
	public static String getBaseUrl(String url) {
		String head = "";
		int index = url.indexOf("://");
		if (index != -1) {
			head = url.substring(0, index + 3);
			url = url.substring(index + 3);
		}
		index = url.indexOf("/");
		if (index != -1) {
			url = url.substring(0, index + 1);
		}
		return head + url;
	}

	/**
	 * 在主请求地址的基础上加请求参数获得最终的请求地址(针对GET的请求)
	 *
	 * @param mainUrl
	 * @param requestParams
	 * @return
	 */
	public static String getRequestUrl(String mainUrl, Map requestParams) {
		if (StringUtils.isEmpty(mainUrl) || requestParams == null || requestParams.isEmpty()) {
			Logger.e(TAG, "getRequestUrl##Conditions not suitable!");
			return "";
		}

		//不存在请求参数
		int index = mainUrl.indexOf("?");
		if (index == -1) {
			Iterator iterator = requestParams.keySet().iterator();
			StringBuilder stringBuilder = new StringBuilder();

			String key;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				stringBuilder.append("&").append(key).append("=").append(requestParams.get(key));
			}

			return mainUrl + "?" + stringBuilder.toString().substring(1);
		} else {//存在
			//获取存在的请求参数
			String paramsString = mainUrl.substring(index + 1);
			//实际的不存在请求参数的请求地址
			String actualUrl = mainUrl.substring(0, index);

			StringBuilder stringBuilder = new StringBuilder();
			List<String> existKeys = new ArrayList<>();

			//判断新请求参数是否已经存在，存在就赋最新的值
			String[] paramsStringArray = paramsString.split("&");
			for (int i = 0; i < paramsStringArray.length; i++) {
				Iterator iterator = requestParams.keySet().iterator();
				String key;
				while (iterator.hasNext()) {
					key = (String) iterator.next();
					if (paramsStringArray[i].indexOf(key) != -1 && paramsStringArray[i].startsWith(key + "=")) {
						existKeys.add(key);
						paramsStringArray[i] = key + "=" + requestParams.get(key);
						break;
					}
				}

				stringBuilder.append("&").append(paramsStringArray[i]);
			}

			Iterator iterator = requestParams.keySet().iterator();
			String key;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				if (!existKeys.contains(key)) {
					stringBuilder.append("&").append(key).append("=").append(requestParams.get(key));
				}
			}

			paramsString = stringBuilder.toString().substring(1);
			return actualUrl + "?" + paramsString;
		}
	}

	/**
	 * 请求头的value中如果有非法字符，会导致OkHttp抛出异常。用这个方法处理非法字符
	 * @param value 需要处理的value
	 * @param replaceChar 用于替换非法字符
	 * 
	 * @author liuyj
	 */
	public static String getDefaultHeaderValue(String value, String replaceChar) {
		if (value != null) {
			StringBuilder stringBuilder = new StringBuilder();
			char[] charArray = value.toCharArray();
			for (int i = 0; i < charArray.length; i++) {
				char c = charArray[i];
				if (c <= '\u001f' || c >= '\u007f') {
					stringBuilder.append(replaceChar);
				} else {
					stringBuilder.append(c);
				}
			}
			return stringBuilder.toString();
		}
		return "";
	}
}