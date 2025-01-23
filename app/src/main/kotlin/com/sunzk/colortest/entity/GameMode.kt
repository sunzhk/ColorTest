package com.sunzk.colortest.entity

/**
 * 游戏模式定义
 */
enum class GameMode {
	/**
	 * 模仿颜色：随机生成颜色，用户通过调节HSB来模拟该颜色，然后系统显示正确答案
 	 */
	MockColor,
	/**
	 * 随机生成两个颜色，用户通过调节HSB来找出这两个颜色的中间色
	 */
	IntermediateColor,

	/**
	 * 生成一个颜色方块矩阵，其中一个方块的颜色与其他颜色略有不同，用户需要找出这个颜色
	 */
	FindDiffColor,

	/**
	 * 生成一个颜色方块矩阵和一个示例方块，从方块矩阵中找出与示例方块颜色相同的方块
	 */
	FindSameColor,
}