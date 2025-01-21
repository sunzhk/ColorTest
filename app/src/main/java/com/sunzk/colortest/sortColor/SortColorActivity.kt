package com.sunzk.colortest.sortColor

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
		ConstraintLayout(modifier = Modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, top = 70.dp, bottom = 30.dp)) {
			val (sortColorViewLeft, sortColorViewRight, commit) = createRefs()
			SortColorView.SortColorView(
				modifier = Modifier
					.constrainAs(sortColorViewLeft) {
						top.linkTo(parent.top)
						start.linkTo(parent.start)
						end.linkTo(sortColorViewRight.start)
						
						verticalBias = 0f
					},
				colorArray = colorArray1,
				divider = 2.5.dp,
				orientation = SortColorView.Orientation.HORIZONTAL,
				onBoxDrag = { from, to ->
					viewModel.onBoxArray1Drag(from, to)
				}
			)
			SortColorView.SortColorView(
				modifier = Modifier
					.padding(top = 30.dp)
					.constrainAs(sortColorViewRight) {
						top.linkTo(parent.top)
						start.linkTo(sortColorViewLeft.end)
						end.linkTo(parent.end)
						height = Dimension.fillToConstraints
						verticalBias = 0f
					},
				colorArray = colorArray2,
				divider = 2.5.dp,
				orientation = SortColorView.Orientation.HORIZONTAL,
				onBoxDrag = { from, to ->
					viewModel.onBoxArray2Drag(from, to)
				}
			)
		}
	}
}