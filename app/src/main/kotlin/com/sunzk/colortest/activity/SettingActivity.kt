package com.sunzk.colortest.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.R
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.Runtime.DarkMode

class SettingActivity : BaseActivity() {
	companion object {
		private const val TAG: String = "SettingActivity"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		needFloatingWindow = false
		setContent { Page() }
	}

	@Preview
	@Composable
	private fun Page() {
		Column {
			Text(text = "设置",
				textAlign = TextAlign.Start,
				fontSize = 31.sp,
				fontWeight = FontWeight.Bold,
				color = colorResource(R.color.theme_txt_standard),
				modifier = Modifier
					.padding(start = 25.dp, top = 25.dp)
					.fillMaxWidth())
			Box(contentAlignment = Alignment.Center, modifier = Modifier
				.padding(bottom = 20.dp)
				.fillMaxWidth()
				.weight(1f)) {
				Column {
					Text(text = "深色模式",
						textAlign = TextAlign.Center,
						fontSize = 18.sp,
						color = colorResource(R.color.theme_txt_standard),
						modifier = Modifier
							.fillMaxWidth())
					var darkMode by remember { mutableStateOf(Runtime.darkMode) }
					Log.d(TAG, "SettingActivity#Page- currentDarkMode=$darkMode")
					val interactionSource = remember { MutableInteractionSource() }
					val isPressed by interactionSource.collectIsPressedAsState()
					val backgroundColor = colorResource(if (isPressed) R.color.common_bt_bg_pressed else R.color.common_bt_bg)
					Button(onClick = {
						darkMode = DarkMode.entries.getOrElse(Runtime.darkMode.ordinal + 1) { DarkMode.entries[0] }
						Runtime.darkMode = darkMode
					},
						interactionSource = interactionSource,
						shape = RoundedCornerShape(8.dp),
						colors = ButtonDefaults.buttonColors(
							containerColor = backgroundColor,
							contentColor = backgroundColor,
							disabledContainerColor = colorResource(R.color.common_bt_bg_disable),
							disabledContentColor = colorResource(R.color.common_bt_bg_disable)),
						border = BorderStroke(1.dp, colorResource(R.color.common_bt_stroke)),
						modifier = Modifier
							.padding(top = 10.dp, start = 25.dp, end = 25.dp)
							.fillMaxWidth()
							.padding(top = 5.dp, bottom = 5.dp)) {
						Text(text = darkMode.text, fontSize = 23.sp, color = colorResource(R.color.common_bt_text))
					}
				}
			}
		}
	}
}