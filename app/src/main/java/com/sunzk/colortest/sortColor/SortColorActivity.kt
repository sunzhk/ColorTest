package com.sunzk.colortest.sortColor

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.R
import com.sunzk.colortest.RouteInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
		ConstraintLayout(modifier = Modifier
			.fillMaxSize()
			.padding(start = 20.dp, end = 20.dp, top = 80.dp, bottom = 30.dp)) {
			val (sortColorViewLeft, sortColorViewRight, commit) = createRefs()
			createHorizontalChain(sortColorViewLeft, sortColorViewRight.withChainParams(startMargin = 50.dp), chainStyle = ChainStyle.Packed)
			val showResult = remember { mutableStateOf(false) }
			VerticalSortColorView(sortColorViewLeft, colorArray1, showResult) { from, to ->
				viewModel.onBoxArray1Drag(from, to)
			}
			VerticalSortColorView(sortColorViewRight, colorArray2, showResult) { from, to ->
				viewModel.onBoxArray2Drag(from, to)
			}
			TextButton(
				onClick = {
					lifecycleScope.launch {
						Log.d(TAG, "SortColorActivity#Page- start show result")
						showResult.value = true
					}
					viewModel.checkResult()
				},
				modifier = Modifier.constrainAs(commit) {
					start.linkTo(parent.start)
					end.linkTo(parent.end)
					bottom.linkTo(parent.bottom)
				}) {
				Text(text = "提交", fontSize = 27.sp, color = colorResource(R.color.theme_txt_standard))
			}
		}
	}
	
	@Composable
	private fun ConstraintLayoutScope.VerticalSortColorView(
		ref: ConstrainedLayoutReference,
		colors: Array<SortColorData>,
		showResult: MutableState<Boolean>,
		onBoxDrag: (from: Int, to: Int) -> Unit,
	) {
		SortColorView.SortColorView(
			modifier = Modifier
				.constrainAs(ref) {
					top.linkTo(parent.top)
					height = Dimension.percent(4 / 7f)
					verticalBias = 0f
				},
			colorArray = colors,
			divider = 3.dp,
			orientation = SortColorView.Orientation.VERTICAL,
			showResult = showResult,
			onBoxDrag = onBoxDrag
		)
	}
}