package com.sunzk.colortest.sortColor

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alibaba.android.arouter.facade.annotation.Route
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.entity.HSB

@Route(path = RouteInfo.PATH_ACTIVITY_SORT_COLOR, group = RouteInfo.GROUP_GAME, name = RouteInfo.DESC_ACTIVITY_SORT_COLOR)
class SortColorActivity: BaseActivity() {
	companion object {
		private const val TAG: String = "SortColorActivity"
	}
	
	private val viewModel by viewModels<SortColorViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent { 
			Page()
		}
	}
	
	@Composable
	private fun Page() {
		val colorArray1 by viewModel.colorArray1.collectAsStateWithLifecycle()
		val colorArray2 by viewModel.colorArray2.collectAsStateWithLifecycle()
		ConstraintLayout(modifier = Modifier.fillMaxSize().padding(20.dp).padding(top = 50.dp)) {
			val (sortColorViewLeft, sortColorViewRight) = createRefs()
			SortColorView.SortColorView(
				modifier = Modifier
					.constrainAs(sortColorViewLeft) {
						top.linkTo(parent.top)
						start.linkTo(parent.start)
						end.linkTo(parent.end)
						width = Dimension.percent(0.85f)
						verticalBias = 0f
					},
				colorArray = colorArray1,
				divider = 2.5.dp,
				orientation = SortColorView.Orientation.HORIZONTAL
			)
			SortColorView.SortColorView(
				modifier = Modifier
					.padding(top = 30.dp)
					.constrainAs(sortColorViewRight) {
						top.linkTo(sortColorViewLeft.bottom)
						start.linkTo(sortColorViewLeft.start)
						end.linkTo(sortColorViewLeft.end)
						width = Dimension.fillToConstraints
						verticalBias = 0f
					},
				colorArray = colorArray2,
				divider = 2.5.dp,
				orientation = SortColorView.Orientation.HORIZONTAL
			)
		}
	}
}