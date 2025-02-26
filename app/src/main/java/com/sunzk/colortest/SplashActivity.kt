package com.sunzk.colortest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.BarUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunzk.base.expand.readFileAsString
import com.sunzk.base.expand.threadInfo
import com.sunzk.colortest.activity.ModeSelectActivity
import com.sunzk.colortest.entity.ModeEntity
import kotlinx.coroutines.*

class SplashActivity : BaseActivity() {
	private val TAG: String = "MainActivity"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		BarUtils.setStatusBarColor(this, resources.getColor(R.color.app_base, null))
		needFloatingWindow = false
		setContentView(R.layout.activity_main)

		Log.d(TAG, "onCreate: thread=${threadInfo()}")

		lifecycleScope.launch(Dispatchers.Main) {
			coroutineScope {
				launch(Dispatchers.IO) {
					Log.d(TAG, "onCreate: 调用requestModeList")
					try {
						requestModeList()
					} catch (t: Throwable) {
						Log.e(TAG, "onCreate: 请求数据失败", t)
					}
					Log.d(TAG, "onCreate: 调用requestModeList结束")
				}
				launch(Dispatchers.IO) {
					delay(2000)
					Log.d(TAG, "onCreate: 等待了2秒")
				}
				Log.d(TAG, "onCreate: coroutineScope算是跑完了")
			}
			Log.d(TAG, "onCreate: 跳转")
			goToModeSelectActivity()
		}
	}

	private suspend fun requestModeList() {
		val modeList = withContext(Dispatchers.IO) {
			assets.readFileAsString("ModeList.json")?.runCatching {
				val type = object : TypeToken<MutableList<ModeEntity>>() {}.type
				val entity = Gson().fromJson<MutableList<ModeEntity>>(this, type)
				entity
			}?.getOrNull() ?: arrayListOf()
		}
		Runtime.modeList = modeList
		Log.d(TAG, "requestModeList: 结束，模式数量${Runtime.modeList.size}")
	}

	private fun goToModeSelectActivity() {
		Log.d(TAG, "goToTestActivity")
		val intent = Intent(this, ModeSelectActivity::class.java)
		startActivity(intent)
		finish()
	}
}