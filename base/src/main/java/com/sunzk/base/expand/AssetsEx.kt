package com.sunzk.base.expand

import android.content.res.AssetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.lang.StringBuilder

/**
 * 将资源文件读取为String
 */
suspend fun AssetManager.readFileAsString(file: String): String? {
	return withContext(Dispatchers.IO) {
		kotlin.runCatching {
			val data = StringBuilder()
			BufferedReader(InputStreamReader(open(file))).use {
				var line: String? = it.readLine()
				while (line != null) {
					data.append(line)
					line = it.readLine()
				}
			}
			data.toString()
		}.getOrNull()
	}
}