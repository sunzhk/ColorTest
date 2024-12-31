package com.sunzk.colortest.activity.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.R
import com.sunzk.colortest.db.MockColorResultTable
import com.sunzk.colortest.db.bean.MockColorResult
import com.sunzk.base.expand.coroutines.GlobalDispatchers
import com.sunzk.base.expand.emitBy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MockColorHistoryActivity: BaseActivity() {
	companion object {
		private const val TAG: String = "MockColorHistoryActivity"
	}
	
	private val history = MutableStateFlow<List<MockColorResult>>(ArrayList<MockColorResult>())

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent { Page() }
		initData()
	}

	private fun initData() {
		lifecycleScope.launch(GlobalDispatchers.IO) {
			MockColorResultTable.queryAll()?.let {
				Log.d(TAG, "MockColorHistoryActivity#initData- ${it.size}")
				history.emitBy(it)
			}
		}
	}
	
	@Composable
	@Preview
	fun Page() {
		Column(modifier = Modifier
			.fillMaxWidth()
			.fillMaxHeight()
			.background(colorResource(R.color.gray_dd))) {
			Box(modifier = Modifier
				.fillMaxWidth()
				.padding(top = 5.dp)
				.height(44.dp)
			) {
				Text(text = "答题记录",
					fontSize = 22.sp,
					color = colorResource(R.color.theme_txt_standard),
					modifier = Modifier.align(Alignment.Center))
			}
			HistoryList()
		}
	}
	
	@Composable
	fun HistoryList() {
		val historyList by history.collectAsStateWithLifecycle()
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
		val expanded = remember { mutableStateOf(false) }
		val itemShape = RoundedCornerShape(10.dp)
		Column(Modifier
			.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
			.border(1.5.dp, colorResource(R.color.theme_txt_disable), shape = itemShape)
			.background(Color.White, shape = itemShape)
			.padding(8.dp)
			.clickable { expanded.value = expanded.value.not() }) {
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
			if (expanded.value) {
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

	// <editor-fold desc="颜色对比绘制">
	
	private var trapezoidDivider = 2.dp
	private var trapezoidWidth = 0f
	private val trapezoidRadius = 5.dp
	private val questionRoundPath = Path()
	private val questionTrapezoidPath = Path()
	private val answerRoundPath = Path()
	private val answerTrapezoidPath = Path()

	private fun DrawScope.drawQuestion(question: FloatArray) {
		trapezoidWidth = (size.width / 2) + (size.height / 2) - trapezoidDivider.toPx()
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
			lineTo(trapezoidWidth - size.height, size.height)
			lineTo(trapezoidWidth, 0F)
			lineTo(0F, 0F)
		}
		// 取 path 交集
		questionRoundPath.op(questionRoundPath, questionTrapezoidPath, PathOperation.Intersect)
		drawPath(path = questionRoundPath, color = Color(android.graphics.Color.HSVToColor(question)))
	}

	private fun DrawScope.drawAnswer(answer: FloatArray) {
		trapezoidWidth = (size.width / 2) + (size.height / 2) - trapezoidDivider.toPx()
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
			lineTo(size.width - trapezoidWidth + size.height, 0F)
			lineTo(size.width, 0F)
		}
		// 取 path 交集
		answerRoundPath.op(answerRoundPath, answerTrapezoidPath, PathOperation.Intersect)
		drawPath(path = answerRoundPath, color = Color(android.graphics.Color.HSVToColor(answer)))
	}

	// </editor-fold>
}