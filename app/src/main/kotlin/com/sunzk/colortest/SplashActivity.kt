package com.sunzk.colortest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.BarUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunzk.base.expand.readFileAsString
import com.sunzk.base.expand.threadInfo
import com.sunzk.colortest.entity.ModeEntity
import com.sunzk.colortest.game.GameActivity
import com.sunzk.colortest.update.UpdateManager
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
			delay(1500)
			goToModeSelectActivity()
		}
		onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				// do nothing
			}
		})
		UpdateManager.checkAppUpdate()
	}

	private fun goToModeSelectActivity() {
		Log.d(TAG, "goToTestActivity")
		val intent = Intent(this, GameActivity::class.java)
		startActivity(intent)
		finish()
	}
}