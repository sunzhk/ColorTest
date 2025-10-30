package com.sunzk.compose

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

/**
 * 扩展Modifier，提供一个无指示器的点击事件
 */
fun Modifier.clickableWithoutIndication(
	onClick: () -> Unit,
): Modifier {
	return this.clickable(
		onClick = onClick,
		indication = null, // 不使用默认的点击效果
		interactionSource = null // 不使用默认的交互源
	)
}