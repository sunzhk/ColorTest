package com.sunzk.compose.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sunzk.compose.clickableWithoutIndication

private const val TAG: String = "BottomDialog"

/**
 * 底部弹窗
 */
@Composable
fun BottomDialog(onDismissRequest: () -> Unit,
                 properties: DialogProperties = DialogProperties(),
                 content: @Composable () -> Unit) {
	Dialog(onDismissRequest = onDismissRequest, 
		properties = properties) {
		Layout(content = {
			// 加一层Box，拦截掉内部点击，避免触发Layout的点击然后就把弹窗关掉了
			Box(modifier = Modifier.wrapContentSize().clickable(enabled = false){}) {
				content()
			}
		}, modifier = Modifier.clickableWithoutIndication { 
			// 用Layout改了显示位置之后，原生的dismissOnClickOutside就不生效了，只能自己加一道
			if (properties.dismissOnClickOutside) {
				onDismissRequest()
			}
		}) { measurable, constraints ->
			val placeable =
				measurable.firstOrNull()?.measure(constraints) ?: return@Layout layout(0, 0) {}
			layout(constraints.maxWidth, constraints.maxHeight) {
				val bottomOffset = constraints.maxHeight - placeable.height
				placeable.placeRelative(0, bottomOffset)
			}
		}
	}
}

@Preview
@Composable
fun PreviewBottomDialog() {
	BottomDialog({}) {
		Text("测试底部弹窗", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().background(Color.White))
	}
}