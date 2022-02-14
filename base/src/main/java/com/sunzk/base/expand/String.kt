package com.sunzk.base.expand

import com.sunzk.base.utils.HttpUtils
import com.sunzk.base.utils.Logger
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

private val TAG: String = "StringExpand"

fun String.urlEncode(charset: String = "UTF-8"): String {
	try {
		return URLEncoder.encode(this, charset)
	} catch (e: UnsupportedEncodingException) {
		Logger.e(TAG, "String.urlEncode error for $this", e)
	}
	return this
}