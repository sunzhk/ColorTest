package com.sunzk.colortest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.BarUtils
import com.sunzk.base.expand.threadInfo
import com.sunzk.colortest.game.GameActivity
import com.sunzk.colortest.update.UpdateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {
	private val TAG: String = "SplashActivity"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		BarUtils.setStatusBarColor(this, resources.getColor(R.color.app_base, null))
		needFloatingWindow = false
		setContent {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.background(colorResource(R.color.app_base)),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center
			) {
				Text(
					text = "Color Test",
					color = colorResource(R.color.splash_title),
					fontSize = 58.sp,
					fontWeight = FontWeight.Bold,
					textAlign = TextAlign.Center,
					modifier = Modifier
				)
				Text(
					text = "for 寂书予&柯基是猫",
					color = colorResource(R.color.splash_hint),
					fontSize = 21.sp,
					textAlign = TextAlign.Center,
					modifier = Modifier
				)
			}
		}

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