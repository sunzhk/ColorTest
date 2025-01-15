package com.sunzk.colortest.findSameColor

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.alibaba.android.arouter.facade.annotation.Route
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.RouteInfo

@Route(path = RouteInfo.PATH_ACTIVITY_FIND_SAME_COLOR, group = RouteInfo.GROUP_GAME)
class FindSameColorActivity: BaseActivity() {
	companion object {
		private const val TAG: String = "FindSameColorActivity"
	}
	
	private val viewModel by viewModels<FindSameColorViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent { Page() }
	}
	
	@Preview
	@Composable
	fun Page() {
		Column { 
			
		}
	}
}