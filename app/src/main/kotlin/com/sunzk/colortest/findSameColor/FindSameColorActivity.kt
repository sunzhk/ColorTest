package com.sunzk.colortest.findSameColor

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.TimeUtils
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.R
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.entity.HSB
import com.sunzk.colortest.findSameColor.FindSameColorViewModel.GameState
import com.sunzk.colortest.tools.ext.get
import com.sunzk.colortest.tools.ext.onClick
import com.sunzk.demo.tools.ext.px2dp
import com.sunzk.demo.tools.ext.toLimitedString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class FindSameColorActivity : BaseActivity() {
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
		val pageData by viewModel.pageData.collectAsStateWithLifecycle()
		Column(verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally, 
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight()
				.background(color = colorResource(R.color.common_bg))) {
			val count = sqrt(pageData.boxColors.size.toFloat()).toInt()
			Log.d(TAG, "FindSameColorActivity#ColorBoxArea- count=$count")
			val boxWidth = (ScreenUtils.getScreenWidth() * 4 / 7).px2dp.dp
			val exampleBoxWidth = boxWidth / count
			val gameState by viewModel.gameState.collectAsStateWithLifecycle()
			when (gameState) {
				GameState.Ready -> { GameController(Modifier.padding(top = 50.dp)) }
				GameState.Playing -> { GameContent(Modifier.padding(top = 50.dp), pageData, count, boxWidth, exampleBoxWidth)}
				GameState.Over -> { GameOver(Modifier.padding(top = 50.dp)) }
			}
		}
		PausingAlertDialog()
	}
	
	// <editor-fold desc="难度选择">

	@Composable
	fun ColumnScope.GameController(modifier: Modifier) {
		Box(modifier = Modifier.fillMaxSize()) {
			Text("请选择游戏难度", fontSize = 30.sp, color = colorResource(R.color.theme_txt_standard), textAlign = TextAlign.Center,
				modifier = modifier.then(Modifier
					.padding(bottom = 30.dp)
					.fillMaxWidth()))
			ConstraintLayout(modifier = Modifier
				.fillMaxSize()) {
				Column(modifier
					.constrainAs(createRefs()[0]) {
						start.linkTo(parent.start)
						end.linkTo(parent.end)
						top.linkTo(parent.top)
						bottom.linkTo(parent.bottom)
						verticalBias = 0.45f
					}
					.fillMaxWidth()
					.wrapContentHeight()) {
					FindSameColorResult.Difficulty.entries.forEach { difficulty ->
						GameDiffItem(difficulty)
					}
				}
			}
		}
	}

	@Composable
	fun ColumnScope.GameDiffItem(difficulty: FindSameColorResult.Difficulty) {
		Text(text = difficulty.text,
			fontSize = 23.sp,
			color = colorResource(R.color.theme_txt_standard),
			textAlign = TextAlign.Center,
			modifier = Modifier
				.align(Alignment.CenterHorizontally)
				.padding(10.dp)
				.width((ScreenUtils.getScreenWidth() / 2).px2dp.dp)
				.background(color = colorResource(R.color.common_bt_bg), shape = RoundedCornerShape(5.dp))
				.border(width = 1.dp, color = colorResource(R.color.common_bt_stroke), shape = RoundedCornerShape(5.dp))
				.padding(10.dp)
				.clickable {
					viewModel.startNewGame(difficulty)
				})
	}
	
	// </editor-fold>
	
	// <editor-fold desc="游戏内容UI">
	@Composable
	fun ColumnScope.GameContent(modifier: Modifier, pageData: FindSameColorPageData, count: Int, boxWidth: Dp, exampleBoxWidth: Dp) {
		Image(painter = painterResource(R.mipmap.icon_common_pause), contentDescription = "",
			modifier = Modifier
				.padding(top = 20.dp, end = 20.dp)
				.size(30.dp)
				.align(Alignment.End)
				.onClick { viewModel.pauseGame() }
		)
		CountDownTimer(Modifier.padding(top = 25.dp))
		Scoreboard(Modifier
			.padding(top = 25.dp))
		ColorBoxArea(Modifier
			.padding(top = 25.dp)
			.align(Alignment.CenterHorizontally), pageData.boxColors, count, boxWidth)
		ExampleColorBox(Modifier
			.padding(top = 65.dp)
			.align(Alignment.CenterHorizontally), pageData.exampleColor, exampleBoxWidth)
		Box(modifier = Modifier.weight(1f))
	}

	/**
	 * 顶部倒计时
	 */
	@Composable
	fun CountDownTimer(modifier: Modifier) {
		val time by viewModel.gameCountDown.collectAsStateWithLifecycle()
		Text(text = time.let {
			val string = TimeUtils.millis2String(time, "mm:ss")
			Log.d(TAG, "FindSameColorActivity#CountDownTimer- $it -> $string")
			string
		}, fontSize = 31.sp, color = colorResource(R.color.theme_txt_standard), textAlign = TextAlign.Center,
			modifier = modifier.then(Modifier
				.fillMaxWidth()
				.wrapContentHeight()))
	}

	/**
	 * 颜色方块区域
	 */
	@Composable
	fun ColorBoxArea(modifier: Modifier, boxColors: List<HSB>, count: Int, width: Dp) {
		val boxCoverStateMap = HashMap<Int, MutableIntState>()
		Column(modifier = modifier.then(Modifier.size(width, width))) {
			repeat(count) { columnIndex ->
				Row(modifier = Modifier
					.fillMaxWidth()
					.weight(1f)) {
					repeat(count) { rowIndex ->
						Log.d(TAG, "FindSameColorActivity#ColorBoxArea- column=$columnIndex, row=$rowIndex")
						val index = columnIndex * count + rowIndex
						SelectableBox(Modifier
							.fillMaxHeight()
							.weight(1f), index, boxColors[index],
							boxCoverStateMap)
					}
				}
			}
		}
	}

	@Composable
	fun SelectableBox(modifier: Modifier, index: Int, color: HSB, coverStateMap: HashMap<Int, MutableIntState>) {
		val boxCoverState = coverStateMap.getOrPut(index) { mutableIntStateOf(0) }
		ConstraintLayout(modifier = modifier.then(Modifier
			.background(color = color.composeColor)
			.let {
				if (boxCoverState.intValue == 1) {
					it.border(4.dp, colorResource(R.color.white_50))
				} else {
					it
				}
			}
			.onClick(300) {
				Log.d(TAG, "FindSameColorActivity#SelectableBox- color=$color")
				if (boxCoverState.intValue > 0 || coverStateMap.any { (_, value) -> value.intValue == 1 }) {
					return@onClick
				}
				val isRight = (color == viewModel.exampleColor)
				viewModel.onColorClick(isRight)
				if (isRight) {
					lifecycleScope.launch {
						boxCoverState.intValue = 1
						delay(500)
						coverStateMap.values.forEach { it.intValue = 0 }
					}
				} else {
					boxCoverState.intValue = 2
				}
			})) {
			if (boxCoverState.intValue == 2) {
				Image(painter = painterResource(R.mipmap.icon_find_same_wrong),
					contentDescription = "",
					modifier = Modifier
						.constrainAs(createRefs()[0]) {
							start.linkTo(parent.start)
							end.linkTo(parent.end)
							top.linkTo(parent.top)
							bottom.linkTo(parent.bottom)
							height = Dimension.percent(0.8f)
							width = Dimension.percent(0.8f)
						}
						.alpha(0.8f))
			}
		}
	}

	/**
	 * 示例颜色
	 */
	@Composable
	fun ExampleColorBox(modifier: Modifier, exampleColor: HSB, boxWidth: Dp) {
		Box(modifier = modifier
			.then(Modifier.size(boxWidth, boxWidth))
			.background(color = Color(exampleColor.rgbColor))) { }
	}
	
	// </editor-fold>
	
	// <editor-fold desc="结算界面">
	@Composable
	private fun ColumnScope.GameOver(modifier: Modifier) {
		Column(verticalArrangement = Arrangement.Top,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = modifier.then(Modifier
				.fillMaxWidth()
				.fillMaxHeight())) {
			Text("游戏结束", fontSize = 35.sp, color = colorResource(R.color.theme_txt_standard), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 60.dp))
			Scoreboard(Modifier.padding(top = 40.dp))
			CorrectRateHint()
			Row(modifier = Modifier
				.padding(top = 50.dp)
				.fillMaxWidth()) {
				Button({
					viewModel.prepareGame()
				}, modifier = Modifier
					.weight(1f)
					.padding(start = 20.dp, end = 10.dp)) {
					Text(text = "换个难度", fontSize = 20.sp)
				}
				Button({
					viewModel.startNewGame(viewModel.difficulty)
				}, modifier = Modifier
					.weight(1f)
					.padding(start = 10.dp, end = 20.dp)) {
					Text(text = "再来一次", fontSize = 20.sp)
				}
			}
		}
	}
	
	@Composable
	private fun CorrectRateHint() {
		val correctRate = viewModel.correctRatePercent()
		val text = when {
			correctRate > 99.99 -> "完美！你太厉害了"
			correctRate > 90 -> "干得漂亮！你超棒的"
			correctRate > 80 -> "不错不错，还有提升空间"
			correctRate > 70 -> "还行，继续努力"
			correctRate > 60 -> "勉强及格，再接再厉吧"
			correctRate > 40 -> "菜，就多练"
			correctRate > 10 -> "去医院看看眼睛吧"
			else -> "真可怜，你是没长眼睛吗"
		}
		Text("正确率${correctRate.toLimitedString(1)}%\n$text",
			fontSize = 21.sp,
			color = colorResource(R.color.theme_txt_standard),
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 30.dp))
	}
	// </editor-fold>

	/**
	 * 计分板
	 */
	@Composable
	fun Scoreboard(modifier: Modifier = Modifier) {
		Row(horizontalArrangement = Arrangement.Center,
			modifier = modifier.then(Modifier
				.fillMaxWidth()
				.wrapContentHeight())) {
			val clickCount by viewModel.roundClickCount.collectAsStateWithLifecycle()
			val rightCount by viewModel.rightCount.collectAsStateWithLifecycle()
			Box(modifier = Modifier.weight(1f))
			Text("点击次数\n$clickCount", fontSize = 21.sp, color = colorResource(R.color.theme_txt_standard), textAlign = TextAlign.Center)
			Box(modifier = Modifier.weight(1f))
			Text("正确次数\n$rightCount", fontSize = 21.sp, color = colorResource(R.color.theme_txt_standard), textAlign = TextAlign.Center)
			Box(modifier = Modifier.weight(1f))
		}
		Box(modifier = Modifier
			.padding(top = 30.dp)
			.wrapContentWidth()) {
			Text("总分 ${viewModel.score}", fontSize = 21.sp, color = colorResource(R.color.theme_txt_standard), textAlign = TextAlign.Center, modifier = Modifier
				.wrapContentSize())
			FloatingNumberAnimation()
		}
	}

	@Composable
	fun BoxScope.FloatingNumberAnimation() {
		val addScore by viewModel.addScoreEvent.collectAsStateWithLifecycle(0L to -1)
		var isVisible by remember { mutableStateOf(false) }
		val alphaAnim by animateFloatAsState(
			animationSpec = tween(350), 
			targetValue = if (isVisible) 1f else 0f,
			label = "alpha"
		)
		val translateYAnim by animateFloatAsState(
			animationSpec = tween(320),
			targetValue = if (isVisible) -100f else 0f,
			label = "translateY"
		)
		LaunchedEffect(addScore) {
			if (addScore.second >= 0) {
				isVisible = true
				delay(250)
				isVisible = false
			}
		}
		Text(
			text = "+${addScore.second}",
			fontSize = 23.sp, 
			color = colorResource(R.color.theme_txt_standard), 
			textAlign = TextAlign.Center,
			modifier = Modifier
				.align(Alignment.BottomEnd)
				.graphicsLayer {
					alpha = alphaAnim
					translationY = translateYAnim
				},
//				style = MaterialTheme.typography.headlineLarge
		)
	}
	
	@Composable
	fun PausingAlertDialog() {
		val pause by viewModel.pausingAlert.collectAsStateWithLifecycle()
		if (pause) {
			Dialog({
				viewModel.continueGame()
			}, properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {
				Column(modifier = Modifier
					.width((ScreenUtils.getScreenWidth() * 2 / 3).dp)
					.background(colorResource(R.color.common_dialog_bg), shape = RoundedCornerShape(10.dp))
					.padding(top = 30.dp, bottom = 20.dp, start = 10.dp, end = 10.dp)) {
					Text(text = "- 暂停 -",
						fontSize = 30.sp,
						textAlign = TextAlign.Center,
						color = colorResource(R.color.theme_txt_standard),
						modifier = Modifier.align(Alignment.CenterHorizontally))
					Row(modifier = Modifier
						.padding(top = 30.dp)
						.align(Alignment.CenterHorizontally)) {
						TextButton({
							viewModel.continueGame()
							viewModel.prepareGame()
						}, modifier = Modifier) {
							Text(text = "换个难度", fontSize = 20.sp)
						}
						TextButton({
							viewModel.continueGame()
						}, modifier = Modifier) {
							Text(text = "继续游戏", fontSize = 20.sp)
						}
					}
				}
			}
		}
	}
}