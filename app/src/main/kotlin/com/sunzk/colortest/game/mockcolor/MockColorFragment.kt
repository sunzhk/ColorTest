package com.sunzk.colortest.game.mockcolor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ScreenUtils
import com.sunzk.colortest.BaseFragment
import com.sunzk.colortest.R
import com.sunzk.colortest.compose.style.commonButtonStyle
import com.sunzk.colortest.compose.ui.DifficultySelector
import com.sunzk.colortest.dialog.CommonSettlementDialog
import com.sunzk.colortest.tools.ext.onClick
import com.sunzk.colortest.view.colorPicker.MergedColorPicker
import com.sunzk.demo.tools.ext.px2dp
import kotlinx.coroutines.launch
import java.util.Locale

class MockColorFragment: BaseFragment() {
	companion object {
		private const val TAG: String = "MockColorFragment"
	}
	
	private val viewModel by viewModels<MockColorViewModel>()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return ComposeView(requireContext()).apply { 
			setContent { Page() }
		}
	}
	
	@Composable
	private fun Page() = Column(Modifier
		.fillMaxSize()
		.padding(horizontal = 20.dp)) {
		val pageData by viewModel.pageData.collectAsStateWithLifecycle()
		Title(Modifier
			.padding(top = 10.dp)
			.fillMaxWidth()
			.height(dimensionResource(R.dimen.common_title_height)), pageData)
		ColorArea(pageData, Modifier
			.fillMaxWidth()
			.weight(1f))
		GameController(Modifier
			.fillMaxWidth()
			.wrapContentHeight(), pageData)
	}

	// <editor-fold desc="顶部栏">
	@Composable
	private fun Title(modifier: Modifier, pageData: MockColorPageData) {
		var showDifficultyDropdownMenu by remember { mutableStateOf(false) }
		Box(modifier) {
			DifficultySelector(Modifier
				.align(Alignment.CenterStart)
				.wrapContentWidth()
				.fillMaxHeight(), pageData.difficulty, MockColorResult.Difficulty.entries) { difficulty ->
				Log.d(TAG, "MockColorFragment#DifficultySelector- on DifficultyText click")
				showDifficultyDropdownMenu = !showDifficultyDropdownMenu
				viewModel.switchDifficulty(difficulty)
			}
			Box(modifier = Modifier
				.align(Alignment.CenterEnd)
				.width(61.dp)
				.fillMaxHeight()
				.commonButtonStyle()) {
				Text(text = stringResource(R.string.list_history),
					color = colorResource(R.color.common_bt_text),
					fontSize = 19.sp,
					modifier = Modifier
						.wrapContentSize()
						.align(Alignment.Center)
						.onClick {
							context?.let { context -> startActivity(Intent(context, MockColorHistoryActivity::class.java)) }
						})
			}
		}
	}
	
	// </editor-fold>
	
	@Composable
	private fun ColorArea(pageData: MockColorPageData, modifier: Modifier) {
		val pickColor by viewModel.pickColor.collectAsStateWithLifecycle()
		val screenWidth = ScreenUtils.getAppScreenWidth().px2dp.dp
		val spacing = 20.dp
		val cardSideLength = (screenWidth - spacing * 4) / 2
		val radius = cardSideLength / 2
		Box(modifier) {
			Box(modifier = Modifier
//				.padding(start = spacing)
				.width(cardSideLength)
				.height(cardSideLength)
				.align(Alignment.CenterStart)
				.background(pageData.questionHSB.composeColor, RoundedCornerShape(radius)))
			Box(modifier = Modifier
//				.padding(end = spacing)
				.width(cardSideLength)
				.height(cardSideLength)
				.align(Alignment.CenterEnd)
				.background(pickColor.composeColor, RoundedCornerShape(radius)))
		}
	}

	@Composable
	private fun GameController(modifier: Modifier, pageData: MockColorPageData) {
		val score by viewModel.score.collectAsStateWithLifecycle()
		Row(modifier = modifier
			.padding(bottom = 50.dp)
			.fillMaxWidth()
			.wrapContentHeight()) {
			Box(modifier = Modifier
				.wrapContentHeight()
				.weight(1f)
				.commonButtonStyle()
				.padding(10.dp)) {
				Text(text = "太难了，换一题", 
					color = colorResource(R.color.common_bt_text),
					fontSize = 19.sp,
					modifier = Modifier.align(Alignment.Center)
						.onClick {
							viewModel.nextQuestion()
						})
			}
			Box(modifier = Modifier
				.padding(start = 15.dp)
				.wrapContentHeight()
				.weight(1f)
				.commonButtonStyle()
				.padding(10.dp)) {
				Text(text = "就是这个颜色了", 
					color = colorResource(R.color.common_bt_text),
					fontSize = 19.sp,
					modifier = Modifier.align(Alignment.Center)
						.onClick {
							showAnswer()
						})
			}
		}
		MergedColorPicker(Modifier
			.padding(start = 20.dp, end = 20.dp, bottom = 15.dp)
			.fillMaxWidth()
			.wrapContentHeight(),
 			pageData.pickHSB) { hsb ->
			Log.d(TAG, "MockColorFragment#GameController- on color pick $hsb")
			viewModel.updatePickColor(hsb)
		}
		Text(String.format(
			Locale.getDefault(),
			"总计完成测试: %d次",
			score
		),
			fontSize = 13.sp,
			color = colorResource(R.color.theme_txt_standard),
			textAlign = TextAlign.Center,
			modifier = Modifier
				.padding(bottom = 15.dp)
				.fillMaxWidth()
				.wrapContentHeight())
	}
	
	// <editor-fold desc="逻辑">

	private fun showAnswer() {
		val activity = activity ?: return
		val question = viewModel.pageData.value.questionHSB
		val answer = viewModel.pickColor.value
		lifecycleScope.launch {
			viewModel.updateScore(question, answer)
		}

		val isRight = viewModel.pageData.value.difficulty.isRight(question, answer)
		val dialog = CommonSettlementDialog(activity, isRight)
		dialog.onCancelClickListener = {
			parentFragmentManager.popBackStack()
		}
		dialog.onConfirmClickListener = {
			viewModel.nextQuestion()
		}
		dialog.show()
	}

	// </editor-fold>
}