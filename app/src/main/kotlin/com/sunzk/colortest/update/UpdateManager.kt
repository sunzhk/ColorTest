package com.sunzk.colortest.update

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.SimpleCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.BuildConfig
import com.sunzk.colortest.MyApplication
import com.sunzk.colortest.update.bean.Asset
import com.sunzk.colortest.update.bean.VersionInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import kotlin.jvm.Throws
import kotlin.math.max
import androidx.core.net.toUri

/**
 * 更新管理器
 */
object UpdateManager {
	
	private const val TAG: String = "UpdateManager"
	
	private val updateScope = CoroutineScope(Dispatchers.IO)

	/**
	 * 从Github获取最新的版本信息
	 */
	private val UPDATE_URL = "https://api.github.com/repos/sunzhk/ColorTest/releases"

	/**
	 * 版本名称前缀
	 */
	private val VERSION_NAME_PREFIX = "release_v"
	private val client by lazy { OkHttpClient() }
	private val gson by lazy { Gson() }
	
	// <editor-fold desc="检查升级">
	
	private val _updateVersion =  MutableStateFlow<VersionInfo?>(null)
	val updateVersion: StateFlow<VersionInfo?> = _updateVersion

	/**
	 * 检查APP版本更新
	 */
	fun checkAppUpdate() = updateScope.launch{
		val version = getVersions()?.let { releaseList ->
			val latestRelease = findLatestRelease(releaseList) ?: return@let null
			if (checkUpdate(latestRelease)) {
				latestRelease
			} else {
				null
			}
		}
		Log.d(TAG, "UpdateManager#checkAppUpdate- find update $version")
		_updateVersion.emitBy(version)
	}

	/**
	 * 获取版本列表
	 */
	private suspend fun getVersions(): Array<VersionInfo>? = withContext(Dispatchers.IO) {
		val request = Request.Builder()
			.url(UPDATE_URL)
			.addHeader("accept", "application/vnd.github+json")
			.build()
		return@withContext client.runCatching {
			newCall(request).execute().use { response ->
				val listType = object : TypeToken<Array<VersionInfo>>() {}.type
				gson.fromJson<Array<VersionInfo>>(response.body?.string(), listType)
			}
		}.getOrNull().also { Log.d(TAG, "UpdateManager#getVersions- $it") }
	}

	/**
	 * 找出最新的release版本
	 */
	private fun findLatestRelease(releaseList: Array<VersionInfo>): VersionInfo? {
		return releaseList.firstOrNull { it.name.startsWith(VERSION_NAME_PREFIX) }.also { Log.d(TAG, "UpdateManager#findLatestRelease- $it") }
	}

	/**
	 * 对比版本号，检查更新
	 */
	private fun checkUpdate(latestRelease: VersionInfo): Boolean {
		val latestVersionName = latestRelease.name.substring(VERSION_NAME_PREFIX.length).split(".")
		val currentVersionName = BuildConfig.VERSION_NAME.split(".")
		Log.d(TAG, "UpdateManager#checkUpdate- compare latest:$latestVersionName to current:$currentVersionName")
		repeat(max(latestVersionName.size, currentVersionName.size)) { index ->
			val latestVersion = latestVersionName.getOrNull(index)?.toIntOrNull() ?: -1
			val currentVersion = currentVersionName.getOrNull(index)?.toIntOrNull() ?: -1
			if (latestVersion > currentVersion) {
				return true
			} else if (latestVersion < currentVersion) {
				return false
			}
		}
		return false
	}

	// </editor-fold>
	
	// <editor-fold desc="下载和安装">

	var isUpdating = false
		private set

	/**
	 * 下载并更新
	 */
	suspend fun downloadAndUpdate(versionInfo: VersionInfo, downloadProgress: MutableStateFlow<Int>?): Boolean = withContext(Dispatchers.IO) {
		if (isUpdating) {
			Log.d(TAG, "UpdateManager#downloadAndUpdate- isUpdating!")
			return@withContext false
		}
		isUpdating = true
		val downloadAsset = versionInfo.assets.find { it.name.endsWith(".apk") }
		if (downloadAsset == null) {
			Log.d(TAG, "UpdateManager#downloadAndUpdate- no available asset")
			isUpdating = false
			return@withContext false
		}
		try {
			// 使用地址downloadAsset.browser_download_url下载apk并安装
			val downloadDir = PathUtils.getCachePathExternalFirst()
			val downloadFile = File(downloadDir, "${downloadAsset.id}_${downloadAsset.name}")
			val downloadResult = if (checkApkExist(downloadFile, downloadAsset)) {
				Log.d(TAG, "UpdateManager#downloadAndUpdate- file already exists: ${downloadFile.absolutePath}")
				true
			} else {
				downloadFile.createNewFile()
				Log.d(TAG, "UpdateManager#downloadAndUpdate- start to download $downloadAsset.browser_download_url")
				downloadWithOkHttp(downloadAsset.browser_download_url, downloadFile, downloadProgress)
			}
			if (downloadResult) {
				// 调用系统安装
				Log.d(TAG, "UpdateManager#downloadAndUpdate - 下载完成，准备安装: ${downloadFile.absolutePath}")
				withContext(Dispatchers.Main) {
					tryToInstallApk(downloadFile)
				}
			}
			isUpdating = false
			return@withContext downloadResult
		} catch (t: Throwable) {
			isUpdating = false
			Log.e(TAG, "UpdateManager#downloadAndUpdate error", t)
			return@withContext false
		}
	}

	private fun checkApkExist(downloadFile: File, downloadAsset: Asset): Boolean {
		Log.d(TAG, "UpdateManager#checkApkExist- ${downloadFile.absolutePath} exists: ${downloadFile.exists()}, size: ${downloadFile.length()}")
		return !downloadFile.exists() || downloadFile.length().toInt() != downloadAsset.size
	}

	@WorkerThread
	@Throws
	private fun downloadWithOkHttp(path: String, targetFile: File, downloadProgress: MutableStateFlow<Int>?): Boolean {
		val request = Request.Builder()
			.url(path)
			.get()
			.build()
		var result = false
		client.newCall(request).execute().use { response ->
			val body = response.body ?: run {
				Log.e(TAG, "UpdateManager#downloadWithOkHttp - 响应体为空")
				result = false
				return@use
			}
			if (!response.isSuccessful) {
				Log.e(TAG, "UpdateManager#downloadWithOkHttp - 下载失败: ${response.code}")
				result = false
				return@use
			}
			val buffer = ByteArray(8192)
			var totalBytesRead = 0L
			body.byteStream().use { inputStream ->
				targetFile.outputStream().use { outputStream ->
					var bytesRead: Int
					while (inputStream.read(buffer).also { bytesRead = it } != -1) {
						outputStream.write(buffer, 0, bytesRead)
						totalBytesRead += bytesRead
						Log.v(TAG, "UpdateManager#downloadWithOkHttp: write to file $totalBytesRead/${body.contentLength()}")
						val progressPercent = (totalBytesRead * 100 / body.contentLength()).toInt()
						downloadProgress?.emitBy(progressPercent)
					}
				}
			}
			// 读写完成，检查写入的总数对不对
			if (totalBytesRead == body.contentLength()) {
				result = true
			} else {
				Log.d(TAG, "UpdateManager#downloadAndUpdate- check file length fail!")
				result = false
			}
		}
		return result
	}

	@MainThread
	private fun tryToInstallApk(downloadFile: File) {
		val context = MyApplication.instance ?: return
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
			!context.packageManager.canRequestPackageInstalls()) {
			// 跳转系统设置页让用户手动开启
			val intent = Intent(
				Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
				"package:${context.packageName}".toUri()
			)
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			context.startActivity(intent)
			return
		}
		val apkUri = androidx.core.content.FileProvider.getUriForFile(
			MyApplication.instance!!,
			"${BuildConfig.APPLICATION_ID}.fileprovider",
			downloadFile
		)
		val installIntent = Intent(Intent.ACTION_VIEW).apply {
			setDataAndType(apkUri, "application/vnd.android.package-archive")
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
		}
		context.startActivity(installIntent)
	}

	// </editor-fold>
	
	// <editor-fold desc="跳转浏览器下载安装">
	fun updateByBrowser(versionInfo: VersionInfo, proxy: ((String) -> String)?) {
		val downloadAsset = versionInfo.assets.find { it.name.endsWith(".apk") }
		if (downloadAsset == null) {
			Log.d(TAG, "UpdateManager#updateByBrowser- no available asset")
			return
		}
		val url = proxy?.invoke(downloadAsset.browser_download_url) ?: downloadAsset.browser_download_url
		val intent = Intent(Intent.ACTION_VIEW).apply {
			data = url.toUri()
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		}
		MyApplication.instance?.startActivity(intent)
	}
	// </editor-fold>

}