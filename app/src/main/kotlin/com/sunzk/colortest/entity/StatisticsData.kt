package com.sunzk.colortest.entity

/**
 * 统计数据
 */
class StatisticsData(var total: Int, var right: Int, var wrong: Int) {
	val rightRate: Float
		get() = if (total == 0) 0f else right.toFloat() / total
}