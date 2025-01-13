package com.sunzk.colortest.intermediateColor

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.R
import com.sunzk.colortest.db.bean.IntermediateColorResult
import com.sunzk.base.expand.coroutines.GlobalDispatchers
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.compose.ui.HistoryPageCommon
import com.sunzk.colortest.compose.ui.HistoryPageCommon.drawColorContrast
import com.sunzk.colortest.db.IntermediateColorResultTable
import com.sunzk.colortest.entity.StatisticsData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 找中间色模式历史记录页
 */
class IntermediateColorHistoryActivity: BaseActivity() {
	companion object {
		private const val TAG: String = "IntermediateColorHistoryActivity"
	}

	private val mHistoryList = MutableStateFlow<List<IntermediateColorResult>>(ArrayList())

	private val difficultyList = arrayOf<Pair<String, IntermediateColorResult.Difficulty?>>("全部" to null) + IntermediateColorResult.Difficulty.entries.map { it.text to it }.toTypedArray()

	private val statisticsMap = mutableMapOf<IntermediateColorResult.Difficulty?, StatisticsData>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent { Page() }
		initData()
	}

	override fun needBGM(): Boolean {
		return true
	}

	private fun initData() {
		lifecycleScope.launch(GlobalDispatchers.IO) {
			IntermediateColorResultTable.queryAll()?.let { queriedData ->
				Log.d(TAG, "IntermediateColorHistoryActivity#initData- ${queriedData.size}")
				mHistoryList.emitBy(queriedData)
				// 更新一下统计数据
				statisticsMap.clear()
				queriedData.forEach { historyItem ->
					val data = statisticsMap.getOrPut(historyItem.difficulty) { StatisticsData(0, 0, 0) }
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
		HistoryPageCommon.HistoryPageCommon {
			HistoryPageCommon.HistoryCommonTitle { showStatisticsDialog.value = true }
			val historyList = mHistoryList.collectAsStateWithLifecycle()
			if (historyList.value.isNotEmpty()) {
				HistoryList()
			} else {
				HistoryPageCommon.HistoryCommonEmptyHint()
			}
		}
		if (showStatisticsDialog.value) {
			HistoryPageCommon.StatisticsDialog<IntermediateColorResult.Difficulty>(
				showStatisticsDialog,
				difficultyList.toList()
			) { statisticsMap[it.value] }
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
	fun HistoryItem(history: IntermediateColorResult) {
		var expanded by remember { mutableStateOf(false) }
		val itemShape = RoundedCornerShape(10.dp)
		Column(Modifier
			.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
			.border(1.5.dp, colorResource(R.color.common_bt_stroke), shape = itemShape)
			.background(colorResource(R.color.common_bt_bg), shape = itemShape)
			.padding(8.dp)
			.clickable { expanded = expanded.not() }) {
			HistoryItemTitle(history)
			Row(modifier = Modifier
				.padding(top = 5.dp)
				.height(35.dp)) {
				Canvas(modifier = Modifier
					.fillMaxHeight()
					.weight(1f)) {
					drawColorContrast(history.questionLeft, history.answer, history.questionRight)
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
	private fun HistoryItemTitle(history: IntermediateColorResult) {
		Row(modifier = Modifier.fillMaxWidth()) {
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
	private fun HistoryValue(history: IntermediateColorResult) {
		Row(modifier = Modifier
			.padding(top = 5.dp)) {
			Text(fontSize = 11.sp,
				text = "示例颜色1：\nH: ${history.questionLeftH.toInt()}, S: ${history.questionLeftS.toInt()}%, B: ${history.questionLeftB.toInt()}%",
				textAlign = TextAlign.Start,
				modifier = Modifier.weight(1f))
			Text(fontSize = 11.sp,
				text = "你的答案：\nH:${history.answerH.toInt()}, S: ${history.answerS.toInt()}%, B: ${history.answerB.toInt()}%",
				textAlign = TextAlign.Start,
				modifier = Modifier.weight(1f))
			Text(fontSize = 11.sp,
				text = "示例颜色2：\nH: ${history.questionRightH.toInt()}, S: ${history.questionRightS.toInt()}%, B: ${history.questionRightB.toInt()}%",
				textAlign = TextAlign.Start,
				modifier = Modifier.weight(1f))
		}
	}
}