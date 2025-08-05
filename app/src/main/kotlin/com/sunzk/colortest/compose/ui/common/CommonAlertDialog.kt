package com.sunzk.colortest.compose.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import com.sunzk.colortest.R
import com.sunzk.compose.clickableWithoutIndication
import com.sunzk.compose.style.commonBlock


@Composable
fun CommonAlertDialog(title: String,
                      message: String,
                      onDismissRequest: () -> Unit,
                      leftButtonText: String = stringResource(R.string.common_cancel),
                      rightButtonText: String = stringResource(R.string.common_confirm),
                      onLeftButtonClick: () -> Unit = { onDismissRequest() },
					  onRightButtonClick: () -> Unit = { onDismissRequest() },
                      properties: DialogProperties = DialogProperties()) {
	Dialog(onDismissRequest = onDismissRequest,
		properties = properties) {
		Column(modifier = Modifier.wrapContentSize().commonBlock()) {
			Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = colorResource(R.color.theme_txt_standard), 
				modifier = Modifier.padding(top = 18.dp).wrapContentWidth().align(Alignment.CenterHorizontally))
			Text(message, fontSize = 14.sp, color = colorResource(R.color.theme_txt_standard), 
				modifier = Modifier.padding(top = 14.dp).padding(horizontal = 16.dp).align(Alignment.CenterHorizontally))
			HorizontalDivider(modifier = Modifier.padding(top = 13.dp), thickness = (0.5).dp, color = Color(0xFFDDDDDD))
			Row(modifier = Modifier.height(43.dp)) {
				Text(leftButtonText, textAlign = TextAlign.Center, fontSize = 17.sp, color = Color(0xFF007AFF), modifier = Modifier.wrapContentHeight().weight(1f)
					.align(Alignment.CenterVertically)
					.clickableWithoutIndication { onLeftButtonClick() })
				VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 1.dp, color = Color(0xFFDDDDDD))
				Text(rightButtonText, textAlign = TextAlign.Center, fontSize = 17.sp, color = Color(0xFF1079FF), modifier = Modifier.wrapContentHeight().weight(1f)
					.align(Alignment.CenterVertically)
					.clickableWithoutIndication { onRightButtonClick() })
			}
		}
	}
}