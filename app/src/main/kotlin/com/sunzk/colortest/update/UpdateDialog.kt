package com.sunzk.colortest.update

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.blankj.utilcode.util.GsonUtils

import com.sunzk.colortest.R
import com.sunzk.colortest.update.bean.Author
import com.sunzk.colortest.update.bean.VersionInfo
import com.sunzk.compose.clickableWithoutIndication
import com.sunzk.compose.style.commonBlock

/**
 * 升级弹窗
 */
@Composable
fun UpdateDialog(
	versionInfo: VersionInfo, 
	onDismissRequest: () -> Unit,
	properties: DialogProperties = DialogProperties(),
) {
	Dialog(onDismissRequest = onDismissRequest,
		properties = properties) {
		Column(modifier = Modifier
			.wrapContentSize()
			.commonBlock()
			.padding(bottom = 3.dp)) {
			Text(stringResource(R.string.hint_update_title), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colorResource(R.color.theme_txt_standard),
				modifier = Modifier
					.padding(top = 20.dp)
					.wrapContentWidth()
					.align(Alignment.CenterHorizontally))
			Text(versionInfo.body, fontSize = 15.sp, color = colorResource(R.color.theme_txt_standard),
				modifier = Modifier
					.padding(top = 14.dp)
					.padding(horizontal = 16.dp)
					.align(Alignment.CenterHorizontally))
			HorizontalDivider(modifier = Modifier.padding(top = 13.dp), thickness = (0.5).dp, color = Color(0xFFDDDDDD))
			DownloadButton("原始地址") { 
				UpdateManager.updateByBrowser(versionInfo, null)
				onDismissRequest()
			}
			HorizontalDivider(thickness = (0.5).dp, color = Color(0xFFDDDDDD))
			DownloadButton("加速1") { 
				UpdateManager.updateByBrowser(versionInfo, { "https://gh-proxy.com/${it}" })
				onDismissRequest()
			}
			HorizontalDivider(thickness = (0.5).dp, color = Color(0xFFDDDDDD))
			DownloadButton("加速2") { 
				UpdateManager.updateByBrowser(versionInfo, { "https://ghfast.top/${it}" })
				onDismissRequest()
			}
		}
	}
}

@Composable
private fun DownloadButton(text: String, onClick: () -> Unit) {
	Box(
		modifier = Modifier
			.height(40.dp)
			.fillMaxWidth()
			.clickableWithoutIndication(onClick = onClick)
	) {
		Text(text, fontSize = 16.sp, color = Color(0xFF1079FF), textAlign = TextAlign.Center,
			modifier = Modifier.wrapContentSize().align(Alignment.Center))
	}
	
}

@Preview
@Composable
fun PreviewUpdateDialog() {
	UpdateDialog(GsonUtils.fromJson<VersionInfo>(VersionInfo.testData, VersionInfo::class.java), {})
}