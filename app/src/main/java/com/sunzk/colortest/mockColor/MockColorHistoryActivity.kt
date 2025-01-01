package com.sunzk.colortest.mockColor

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ScreenUtils
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.R
import com.sunzk.colortest.db.MockColorResultTable
import com.sunzk.colortest.db.bean.MockColorResult
import com.sunzk.base.expand.coroutines.GlobalDispatchers
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.entity.StatisticsData
import com.sunzk.demo.tools.ext.px
import com.sunzk.demo.tools.ext.toLimitedString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MockColorHistoryActivity: BaseActivity() {
	companion object {
		private const val TAG: String = "MockColorHistoryActivity"
	}
	
	private val mHistoryList = MutableStateFlow<List<MockColorResult>>(ArrayList<MockColorResult>())
	
	private val difficultyList = arrayOf<Pair<String, MockColorResult.Difficulty?>>("全部" to null) + MockColorResult.Difficulty.entries.map { it.text to it }.toTypedArray()
	
	private val statisticsMap = mutableMapOf<MockColorResult.Difficulty?, StatisticsData>()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent { Page() }
		initData()
	}

	private fun initData() {
		lifecycleScope.launch(GlobalDispatchers.IO) {
			MockColorResultTable.queryAll()?.let { queriedData ->
				Log.d(TAG, "MockColorHistoryActivity#initData- ${queriedData.size}")
				mHistoryList.emitBy(queriedData)
				// 更新一下统计数据
				statisticsMap.clear()
				queriedData.forEach { historyItem ->
					val data = statisticsMap.getOrPut(historyItem.difficulty, { StatisticsData(0, 0, 0) })
					data.total++
					if (historyItem.isRight()) {
						data.right++
					} else {
						data.wrong++
					}
				}
				val rightSum = statisticsMap.values.sumOf { it.right }
				val wrongSum = statisticsMap.values.sumOf { it.wrong }
				statisticsMap[null] = StatisticsData(queriedData.size, rightSum, wrongSum)
			}
		}
	}
	
	@Composable
	@Preview
	fun Page() {
		val showStatisticsDialog = remember { mutableStateOf(false) }
		Column(modifier = Modifier
			.fillMaxWidth()
			.fillMaxHeight()
			.background(Color(0xFFF0F0F0))) {
			Box(modifier = Modifier
				.fillMaxWidth()
				.padding(top = 5.dp)
				.height(44.dp)
			) {
				Text(text = "答题记录",
					fontSize = 22.sp,
					color = colorResource(R.color.theme_txt_standard),
					modifier = Modifier.align(Alignment.Center))
				Text(text = "统计",
					fontSize = 18.sp,
					color = colorResource(R.color.theme_txt_standard),
					modifier = Modifier
						.align(Alignment.CenterEnd)
						.padding(end = 15.dp)
						.clickable {
							showStatisticsDialog.value = true
						})
			}
			val historyList = mHistoryList.collectAsStateWithLifecycle()
			if (historyList.value.isNotEmpty()) {
				HistoryList()
			} else {
				Text(text = "还没有答题记录哦~\n快回去答题吧",
					fontSize = 17.sp,
					color = colorResource(R.color.theme_txt_disable),
					textAlign = TextAlign.Center,
					modifier = Modifier
						.padding(top = 20.dp)
						.fillMaxWidth())
			}
		}
		if (showStatisticsDialog.value) {
			StatisticsDialog(showStatisticsDialog)
		}
	}
	
	@Composable
	fun HistoryList() {
		val historyList by mHistoryList.collectAsStateWithLifecycle()
		LazyColumn(modifier = Modifier
			.fillMaxWidth()
			.fillMaxHeight()) {
			items(historyList, key = { it.id }) { item ->
				HistoryItem(item)
			}
		}
	}
	
	@Composable
	fun HistoryItem(history: MockColorResult) {
		var expanded by remember { mutableStateOf<Boolean>(false) }
		val itemShape = RoundedCornerShape(10.dp)
		Column(Modifier
			.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
			.border(1.5.dp, colorResource(R.color.theme_txt_disable), shape = itemShape)
			.background(Color.White, shape = itemShape)
			.padding(8.dp)
			.clickable { expanded = expanded.not() }) {
			HistoryItemTitle(history)
			Row(modifier = Modifier
				.padding(top = 5.dp)
				.height(35.dp)) {
				Canvas(modifier = Modifier
					.fillMaxHeight()
					.weight(1f)) {
					drawQuestion(history.question)
					drawAnswer(history.answer)
				}
				Image(painter = painterResource(if (history.isRight()) R.mipmap.icon_result_right else R.mipmap.icon_result_wrong),
					contentDescription = null, 
					modifier = Modifier
						.padding(start = 10.dp, end = 8.dp)
						.size(30.dp, 30.dp)
						.align(Alignment.CenterVertically))
			}
			if (expanded) {
				HistoryValue(history)
			}
		}
		
	}

	@Composable
	private fun HistoryItemTitle(history: MockColorResult) {
		Row(modifier = Modifier.fillMaxWidth()) {
//			Text(text = "${history.id}.", fontSize = 15.sp, color = colorResource(R.color.theme_txt_standard), modifier = Modifier)
			Text(text = history.date ?: "",
				fontSize = 15.sp,
				color = colorResource(R.color.theme_txt_standard),
				modifier = Modifier
					.align(Alignment.CenterVertically)
					.weight(1f))
			Text(text = "难度：${history.difficulty.text}",
				fontSize = 15.sp,
				color = colorResource(R.color.theme_txt_standard),
				modifier = Modifier
					.align(Alignment.CenterVertically))
		}
	}

	@Composable
	private fun HistoryValue(history: MockColorResult) {
		Row(modifier = Modifier
			.padding(top = 5.dp)) {
			Text(fontSize = 15.sp,
				text = "示例颜色：\nH: ${history.questionH.toInt()}, S: ${history.questionS.toInt()}%, B: ${history.questionB.toInt()}%",
				textAlign = TextAlign.Start,
				modifier = Modifier.weight(1f))
			Text(fontSize = 15.sp,
				text = "你的答案：\nH:${history.answerH.toInt()}, S: ${history.answerS.toInt()}%, B: ${history.answerB.toInt()}%",
				textAlign = TextAlign.Start,
				modifier = Modifier.weight(1f))
		}
	}
	
	@Composable
	private fun StatisticsDialog(showStatisticsDialog: MutableState<Boolean>) {
		Dialog(onDismissRequest = {}) {
			Card(modifier = Modifier.width(ScreenUtils.getScreenWidth().px - 40.dp)) {
				Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
					val statisticsDiff = remember { mutableStateOf<MockColorResult.Difficulty?>(null) }
					Text(text = "统计", fontSize = 21.sp, textAlign =  TextAlign.Center, color = colorResource(R.color.theme_txt_standard), modifier = Modifier
						.padding(top = 20.dp)
						.fillMaxWidth())
					// 难度选择
					StatisticsDialogDiffSelector(statisticsDiff)
					// 统计数据
					StatisticsDialogContent(statisticsDiff)
					// 关闭按钮
					Box(modifier = Modifier.padding(top = 20.dp).fillMaxWidth().height(1.dp).background(colorResource(R.color.theme_txt_disable)))
					Text(text = "关闭", fontSize = 18.sp, color = colorResource(R.color.theme_txt_standard), textAlign = TextAlign.Center, modifier = Modifier
						.padding(10.dp)
						.fillMaxWidth()
						.clickable {
							showStatisticsDialog.value = false
						})
				}
			}
		}
	}

	@Composable
	private fun StatisticsDialogDiffSelector(statisticsDiff: MutableState<MockColorResult.Difficulty?>) {
		Row(Modifier.padding(top = 10.dp)) {
			difficultyList.forEach { diffItem ->
				Text(text = diffItem.first,
					fontSize = 15.sp,
					color = if (statisticsDiff.value == diffItem.second) Color.White else colorResource(R.color.theme_txt_standard),
					textAlign = TextAlign.Center,
					modifier = Modifier
						.padding(start = 5.dp, end = 5.dp)
						.weight(1f)
						.border(1.dp, colorResource(R.color.theme_txt_disable), RoundedCornerShape(5.dp))
						.background(if (statisticsDiff.value == diffItem.second) colorResource(R.color.app_base) else Color.Transparent,
							shape = RoundedCornerShape(5.dp))
						.padding(5.dp)
						.clickable {
							statisticsDiff.value = diffItem.second
						}
				)
			}
		}
	}

	@Composable
	private fun StatisticsDialogContent(statisticsDiff: MutableState<MockColorResult.Difficulty?>) {
		StatisticsLine("答题总数：${statisticsMap[statisticsDiff.value]?.total ?: "Nan"}")
		StatisticsLine("答对题目：${statisticsMap[statisticsDiff.value]?.right ?: "Nan"}")
		StatisticsLine("答错题目：${statisticsMap[statisticsDiff.value]?.wrong ?: "Nan"}")
		StatisticsLine("正确率：${statisticsMap[statisticsDiff.value]?.rightRate?.let { "${(it * 100).toLimitedString(1)}%"} ?: "Nan"}")
	}
	
	@Composable
	private fun StatisticsLine(text: String) {
		Text(text = text, fontSize = 16.sp, color = colorResource(R.color.theme_txt_standard), modifier = Modifier.padding(top = 20.dp))
	}
	
	// <editor-fold desc="颜色对比绘制">
	
	private var trapezoidDivider = 0.5.dp
	private var trapezoidWidth = 0f
	private var trapezoidSlope = 2f
	private val trapezoidRadius = 5.dp
	private val questionRoundPath = Path()
	private val questionTrapezoidPath = Path()
	private val answerRoundPath = Path()
	private val answerTrapezoidPath = Path()

	private fun DrawScope.drawQuestion(question: FloatArray) {
		trapezoidWidth = (size.width / 2) + (size.height * trapezoidSlope / 2) - trapezoidDivider.toPx()
		// 圆角 path
		questionRoundPath.reset()
		questionRoundPath.apply {
			addRoundRect(
				RoundRect(
					left = 0F,
					top = 0F,
					right = trapezoidWidth,
					bottom = size.height,
					topLeftCornerRadius = CornerRadius(trapezoidRadius.toPx(), trapezoidRadius.toPx()),
					bottomLeftCornerRadius = CornerRadius(trapezoidRadius.toPx(), trapezoidRadius.toPx())
				)
			)
		}
		// 梯形 path
		questionTrapezoidPath.reset()
		questionTrapezoidPath.apply {
			moveTo(0F, 0F)
			lineTo(0F, size.height)
			lineTo(trapezoidWidth - size.height * trapezoidSlope, size.height)
			lineTo(trapezoidWidth, 0F)
			lineTo(0F, 0F)
		}
		// 取 path 交集
		questionRoundPath.op(questionRoundPath, questionTrapezoidPath, PathOperation.Intersect)
		drawPath(path = questionRoundPath, color = Color(android.graphics.Color.HSVToColor(question)))
	}

	private fun DrawScope.drawAnswer(answer: FloatArray) {
		trapezoidWidth = (size.width / 2) + (size.height * trapezoidSlope / 2) - trapezoidDivider.toPx()
		// 圆角 path
		answerRoundPath.reset()
		answerRoundPath.apply {
			addRoundRect(
				RoundRect(
					left = size.width - trapezoidWidth,
					top = 0F,
					right = size.width,
					bottom = size.height,
					topRightCornerRadius = CornerRadius(trapezoidRadius.toPx(), trapezoidRadius.toPx()),
					bottomRightCornerRadius = CornerRadius(trapezoidRadius.toPx(), trapezoidRadius.toPx())
				)
			)
		}
		// 梯形 path
		answerTrapezoidPath.reset()
		answerTrapezoidPath.apply {
			moveTo(size.width, 0F)
			lineTo(size.width, size.height)
			lineTo(size.width - trapezoidWidth, size.height)
			lineTo(size.width - trapezoidWidth + size.height * trapezoidSlope, 0F)
			lineTo(size.width, 0F)
		}
		// 取 path 交集
		answerRoundPath.op(answerRoundPath, answerTrapezoidPath, PathOperation.Intersect)
		drawPath(path = answerRoundPath, color = Color(android.graphics.Color.HSVToColor(answer)))
	}

	// </editor-fold>
}