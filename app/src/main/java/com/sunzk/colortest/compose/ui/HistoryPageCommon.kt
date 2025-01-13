package com.sunzk.colortest.compose.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.blankj.utilcode.util.ScreenUtils
import com.sunzk.colortest.R
import com.sunzk.colortest.db.bean.MockColorResult
import com.sunzk.colortest.entity.StatisticsData
import com.sunzk.demo.tools.ext.px
import com.sunzk.demo.tools.ext.toLimitedString

object HistoryPageCommon {
	
	private const val TAG: String = "HistoryPageCommon"

	/**
	 * 历史记录通用页面背景
	 */
	@Composable
	fun HistoryPageCommon(content: @Composable ColumnScope.() -> Unit) {
		Column(modifier = Modifier
			.fillMaxWidth()
			.fillMaxHeight()
			.background(colorResource(R.color.common_bg)), content = content)
	}

	/**
	 * 历史记录通用标题
	 */
	@Composable
	fun HistoryCommonTitle(showStatisticsDialog: () -> Unit) {
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
						showStatisticsDialog()
					})
		}
	}
	
	@Composable
	fun HistoryCommonEmptyHint() {
		Text(text = "还没有答题记录哦~\n快回去答题吧",
			fontSize = 17.sp,
			color = colorResource(R.color.theme_txt_disable),
			textAlign = TextAlign.Center,
			modifier = Modifier
				.padding(top = 20.dp)
				.fillMaxWidth())
	}

	@Composable
	fun <T> StatisticsDialog(
		showStatisticsDialog: MutableState<Boolean>,
		diffList: List<Pair<String, T?>>,
		statisticsData: (MutableState<T?>) -> StatisticsData?,
	) {
		Dialog(onDismissRequest = {}) {
			Card(modifier = Modifier.width(ScreenUtils.getScreenWidth().px - 40.dp)) {
				Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
					val statisticsDiff = remember { mutableStateOf<T?>(null) }
					Text(text = "统计", fontSize = 21.sp, textAlign =  TextAlign.Center, color = colorResource(R.color.theme_txt_standard), modifier = Modifier
						.padding(top = 20.dp)
						.fillMaxWidth())
					// 难度选择
					StatisticsDialogDiffSelector(diffList, statisticsDiff)
					// 统计数据
					StatisticsDialogContent(statisticsData(statisticsDiff))
					// 关闭按钮
					Box(modifier = Modifier
						.padding(top = 20.dp, bottom = 10.dp)
						.fillMaxWidth()
						.height(38.dp)
						.background(Color.LightGray, shape = RoundedCornerShape(5.dp))
						.clickable { showStatisticsDialog.value = false }
					) {
						Text(text = "关闭",
							fontSize = 18.sp,
							color = colorResource(R.color.theme_txt_standard),
							modifier = Modifier
								.wrapContentSize()
								.align(Alignment.Center))
					}
				}
			}
		}
	}

	@Composable
	private fun <T> StatisticsDialogDiffSelector(diffList: List<Pair<String, T?>>, statisticsDiff: MutableState<T?>) {
		Row(Modifier.padding(top = 10.dp)) {
			diffList.forEach { (name: String, diff: T?) ->
				Text(text = name,
					fontSize = 15.sp,
					color = if (statisticsDiff.value == diff) Color.White else colorResource(R.color.theme_txt_standard),
					textAlign = TextAlign.Center,
					modifier = Modifier
						.padding(start = 5.dp, end = 5.dp)
						.weight(1f)
						.border(1.dp, colorResource(R.color.theme_txt_disable), RoundedCornerShape(5.dp))
						.background(if (statisticsDiff.value == diff) colorResource(R.color.app_base) else Color.Transparent,
							shape = RoundedCornerShape(5.dp))
						.padding(5.dp)
						.clickable {
							statisticsDiff.value = diff
						}
				)
			}
		}
	}

	@Composable
	private fun StatisticsDialogContent(statisticsData: StatisticsData?) {
		StatisticsLine("答题总数：${statisticsData?.total ?: "Nan"}")
		StatisticsLine("答对题目：${statisticsData?.right ?: "Nan"}")
		StatisticsLine("答错题目：${statisticsData?.wrong ?: "Nan"}")
		StatisticsLine("正确率：${statisticsData?.rightRate?.let { "${(it * 100).toLimitedString(1)}%"} ?: "Nan"}")
	}

	@Composable
	private fun StatisticsLine(text: String) {
		Text(text = text, fontSize = 16.sp, color = colorResource(R.color.theme_txt_standard), modifier = Modifier.padding(top = 20.dp))
	}

	// <editor-fold desc="颜色对比绘制">

	// 固定参数 - 
	private var trapezoidDivider = 0.dp
	// 固定参数 - 
	private var trapezoidSlope = 1.3f
	// 固定参数 - 圆角
	private val trapezoidRadius = 5.dp
	// 临时参数 - 圆角区域
	private val roundPath = Path()
	// 临时参数 - 四边形区域
	private val trapezoidPath = Path()

	fun DrawScope.drawColorContrast(vararg colors: FloatArray) {
		// 循环绘制每一段
		colors.forEachIndexed { index, color ->
			if (index == 0 || index == colors.size - 1) {
				// 头尾绘制梯形
				drawTrapezoid(colors.size, color, index == 0)
			} else {
				// 中间绘制平行四边形
				drawParallelogram(colors.size, index, color)
			}
		}
	}

	private fun DrawScope.resetRound() {
		// 圆角区域可以通用的，直接搞一个就行
		roundPath.reset()
		roundPath.addRoundRect(
			RoundRect(
				left = 0F,
				top = 0F,
				right = size.width,
				bottom = size.height,
				topLeftCornerRadius = CornerRadius(trapezoidRadius.toPx(), trapezoidRadius.toPx()),
				bottomLeftCornerRadius = CornerRadius(trapezoidRadius.toPx(), trapezoidRadius.toPx()),
				topRightCornerRadius = CornerRadius(trapezoidRadius.toPx(), trapezoidRadius.toPx()),
				bottomRightCornerRadius = CornerRadius(trapezoidRadius.toPx(), trapezoidRadius.toPx()),
			)
		)
	}

	// 临时参数 - 梯形长边
	private var trapezoidWidth = 0f
	// 临时参数 - 梯形短边
	private var trapezoidShortWidth = 0f

	/**
	 * 绘制头尾的梯形
	 */
	private fun DrawScope.drawTrapezoid(count: Int, color: FloatArray, isHead: Boolean) {
		// 计算每一段使用宽度：(总宽度 / 颜色段数) + (总高度 * 梯形斜率 / 2) - (梯形分割线 * 颜色段数 - 1)
		trapezoidWidth = (size.width / count) + (size.height * trapezoidSlope / 2) - (trapezoidDivider.toPx() * (count - 1))
		// 计算每一段短边宽度：长边宽度 - 总高度 * 梯形斜率
		trapezoidShortWidth = trapezoidWidth - size.height * trapezoidSlope
		// 梯形 path
		trapezoidPath.reset()
		trapezoidPath.apply {
			// 从左上开始
			moveTo(if (isHead) 0f else size.width - trapezoidShortWidth, 0F)
			// 左下角
			lineTo(if (isHead) 0f else size.width - trapezoidWidth, size.height)
			// 右下角
			lineTo(if (isHead) trapezoidShortWidth else size.width, size.height)
			// 右上角
			lineTo(if (isHead) trapezoidWidth else size.width, 0F)
			// 回起点
			lineTo(if (isHead) 0f else size.width - trapezoidShortWidth, 0F)
		}
		// 取 path 交集
		resetRound()
		roundPath.op(roundPath, trapezoidPath, PathOperation.Intersect)
		drawPath(path = roundPath, color = Color(android.graphics.Color.HSVToColor(color)))
	}

	// 临时参数 - 平行四边形边长
	private var parallelogramSideLength = 0f
	
	/**
	 * 绘制中间的平行四边形
	 * 
	 * @param index 第几个四边形（包括头尾梯形）
	 */
	private fun DrawScope.drawParallelogram(count: Int, index: Int, color: FloatArray) {
		Log.d(TAG, "HistoryPageCommon#drawParallelogram- count: $count, index: $index, color: ${color.contentToString()}")
		// 计算每一段的边长：(总高度 / 颜色段数) - (梯形分割线 * 颜色段数 - 1)
		parallelogramSideLength = (size.width / count) - (trapezoidDivider.toPx() * (count - 1))
		// 计算头尾梯形的使用宽度(长边宽度)：(总宽度 / 颜色段数) + (总高度 * 梯形斜率 / 2) - (梯形分割线 * 颜色段数 - 1)
		trapezoidWidth = (size.width / count) + (size.height * trapezoidSlope / 2) - (trapezoidDivider.toPx() * (count - 1))
		// 计算头尾梯形的短边宽度：长边宽度 - 总高度 * 梯形斜率
		trapezoidShortWidth = trapezoidWidth - size.height * trapezoidSlope
		Log.d(TAG, "HistoryPageCommon#drawParallelogram- parallelogramSideLength=$parallelogramSideLength, trapezoidWidth=$trapezoidWidth, trapezoidShortWidth=$trapezoidShortWidth")
		// 平行四边形 path
		trapezoidPath.reset()
		trapezoidPath.apply {
			// 从左上开始
			moveTo(trapezoidWidth + (parallelogramSideLength * (index - 1)) + (trapezoidDivider.toPx() * (index - 1)), 0F)
			// 左下角
			lineTo(trapezoidShortWidth + (parallelogramSideLength * (index - 1)) + (trapezoidDivider.toPx() * (index - 1)), size.height)
			// 右下角
			lineTo(trapezoidShortWidth + (parallelogramSideLength * index) + (trapezoidDivider.toPx() * (index - 1)), size.height)
			// 右上角
			lineTo(trapezoidWidth + (parallelogramSideLength * index) + (trapezoidDivider.toPx() * (index - 1)), 0F)
			// 回起点
			lineTo(trapezoidWidth + (parallelogramSideLength * (index - 1)) + (trapezoidDivider.toPx() * (index - 1)), 0F)
		}
		drawPath(path = trapezoidPath, color = Color(android.graphics.Color.HSVToColor(color)))
	}

	// </editor-fold>
}