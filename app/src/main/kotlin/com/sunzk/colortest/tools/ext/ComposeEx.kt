package com.sunzk.colortest.tools.ext

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope


@Composable
inline fun Modifier.onClick(
	time: Int = 200,
	enabled: Boolean = true,//中间这三个是clickable自带的参数
	onClickLabel: String? = null,
	role: Role? = null,
	crossinline onClick: () -> Unit
): Modifier {
	var lastClickTime by remember { mutableLongStateOf(value = 0L) } //使用remember函数记录上次点击的时间
	return clickable(enabled, onClickLabel, role) {
		val currentTimeMillis = System.currentTimeMillis()
		if (currentTimeMillis - time >= lastClickTime) { //判断点击间隔,如果在间隔内则不回调
			onClick()
			lastClickTime = currentTimeMillis
		}
	}
}

operator fun ConstraintLayoutScope.ConstrainedLayoutReferences.get(index: Int): ConstrainedLayoutReference {
	return when (index) {
		0 -> component1()
		1 -> component1()
		2 -> component1()
		3 -> component1()
		4 -> component1()
		5 -> component1()
		6 -> component1()
		7 -> component1()
		8 -> component1()
		9 -> component1()
		10 -> component1()
		11 -> component1()
		12 -> component1()
		13 -> component1()
		14 -> component1()
		15 -> component1()
		else -> throw IllegalArgumentException("只支持16个组件约束")
	}
}