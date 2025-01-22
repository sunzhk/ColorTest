package com.sunzk.colortest.sortColor

import android.graphics.fonts.FontStyle
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alibaba.android.arouter.facade.annotation.Route
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.R
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
		Box(Modifier.fillMaxSize()) { 
			GameArea()
			ResultAnimation()
		}
		
	}

	@Composable
	private fun GameArea() {
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
			val enableCommit by viewModel.canTouch.collectAsStateWithLifecycle()
			var buttonType by remember { mutableStateOf(BottomButtonType.Commit) }
			TextButton(
				enabled = enableCommit,
				onClick = {
					when (buttonType) {
						BottomButtonType.Commit -> { showResult.value = true }
						BottomButtonType.NextQuestion -> {
							showResult.value = false
							viewModel.nextQuestion()
						}
					}
					buttonType = BottomButtonType.entries.let { entries ->
						entries[(entries.indexOf(buttonType) + 1) % entries.size]
					}
				},
				modifier = Modifier
					.constrainAs(commit) {
						start.linkTo(parent.start)
						end.linkTo(parent.end)
						bottom.linkTo(parent.bottom)
					}
					.padding(bottom = 10.dp)) {
				Text(text = buttonType.text, fontSize = 27.sp, color = colorResource(R.color.theme_txt_standard))
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
			onBoxDrag = onBoxDrag,
			onShowResultAnim = { finish -> onShowResultAnim(finish) }
		)
	}
	
	@Composable
	private fun BoxScope.ResultAnimation() {
		val success by viewModel.resultAnim.collectAsStateWithLifecycle()
		Log.d(TAG, "SortColorActivity#ResultAnimation- success=$success")
		if (success == null) {
			return
		}
		val text: String
		if (success == true) {
			text = "答对啦~"
			val lottieAnimRes = rememberLottieComposition(LottieCompositionSpec.Asset("lottie/lottie_game_success_congratulation.json"))
			Log.d(TAG, "SortColorActivity#ResultAnimation- lottieAnimRes isSuccess=${lottieAnimRes.isSuccess}, isLoading=${lottieAnimRes.isLoading}, isComplete=${lottieAnimRes.isComplete}", lottieAnimRes.error)
			val progress by animateLottieCompositionAsState(lottieAnimRes.value, clipSpec = LottieClipSpec.Progress(0f, 0.73f), iterations = Int.MAX_VALUE)
			LottieAnimation(lottieAnimRes.value, progress = { progress }, alignment = Alignment.TopCenter)
		} else {
			text = "答错咯~"
			val lottieAnimRes = rememberLottieComposition(LottieCompositionSpec.Asset("lottie/lottie_game_failure.json"))
			Log.d(TAG, "SortColorActivity#ResultAnimation- lottieAnimRes isSuccess=${lottieAnimRes.isSuccess}, isLoading=${lottieAnimRes.isLoading}, isComplete=${lottieAnimRes.isComplete}", lottieAnimRes.error)
			val progress by animateLottieCompositionAsState(lottieAnimRes.value, clipSpec = LottieClipSpec.Progress(0f, 1f), iterations = Int.MAX_VALUE)
			LottieAnimation(lottieAnimRes.value, progress = { progress }, alignment = Alignment.Center)
		}
		Box(modifier = Modifier
			.align(Alignment.Center)
			.wrapContentWidth()
			.height(60.dp)
			.alpha(0.8f)
			.background(colorResource(R.color.theme_txt_standard), RoundedCornerShape(30.dp))
			.padding(start = 25.dp, end = 25.dp)) {
			Text(text,
				fontSize = 37.sp,
				color = colorResource(R.color.common_bt_bg),
				fontWeight = FontWeight.Bold,
				textAlign = TextAlign.Center,
				modifier = Modifier.align(Alignment.Center))
		}
	}
	
	private fun onShowResultAnim(isFinished: Boolean) {
		viewModel.setCanTouch(isFinished)
		if (isFinished) {
			viewModel.checkResult()
		}
	}
	
	private enum class BottomButtonType(val text: String) {
		Commit("提交"), NextQuestion("下一题")
	}
}