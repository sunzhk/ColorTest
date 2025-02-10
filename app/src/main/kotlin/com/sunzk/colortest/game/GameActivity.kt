package com.sunzk.colortest.game

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment
import com.arcsoft.closeli.utils.takeIfIs
import com.sunzk.base.expand.bindView
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.databinding.ActivityGameBinding

class GameActivity: BaseActivity() {

	companion object {
		private const val TAG: String = "GameActivity"
	}
	
	private val viewBinding by bindView<ActivityGameBinding>()
	private var navController: NavController? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initView()
		initNavigation()
	}
	
	private fun initView() = with(viewBinding) {
		Log.d(TAG, "GameActivity#initView- $navHostFragment")
	}
	
	private fun initNavigation() {
		navController = supportFragmentManager.findFragmentById(viewBinding.navHostFragment.id)
			?.takeIfIs<NavHostFragment>()
			?.navController
		Log.d(TAG, "GameActivity#initNavigation- $navController")
	}

	override fun onSupportNavigateUp(): Boolean {
		return (navController?.navigateUp() ?: false) || super.onSupportNavigateUp()
	}
}