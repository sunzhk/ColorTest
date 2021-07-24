package com.sunzk.colortest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunzk.colortest.activity.ModeSelectActivity
import com.sunzk.colortest.entity.ModeEntity
import com.sunzk.demo.service.BaseService
import kotlinx.coroutines.*

class MainActivity : BaseActivity() {
	private val TAG: String = "MainActivity"
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		setContentView(R.layout.activity_main)

		CoroutineScope(Dispatchers.Main).launch {
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
			goToTestActivity()
		}
	}

	private suspend fun requestModeList() {
		val url = "${Constant.BASE_URL_MOCK}data/SimpleModeList.json"
		Log.d(TAG, "requestModeList: 开始 with $url")
		val response = BaseService().request(url)
		val modeList = withContext(Dispatchers.IO) {
			val type = object : TypeToken<ArrayList<ModeEntity>>() {}.type
			val json = response.body?.string()
//			Log.d(TAG, "requestModeList: $json")
			val list: ArrayList<ModeEntity> = Gson().fromJson(json, type)
			list
		}
		Runtime.modeList = modeList
		Log.d(TAG, "requestModeList: 结束，模式数量${Runtime.modeList.size}")
	}

	private fun goToTestActivity() {
		Log.d(TAG, "goToTestActivity")
		val intent = Intent(this, ModeSelectActivity::class.java)
		startActivity(intent)
		finish()
	}
}