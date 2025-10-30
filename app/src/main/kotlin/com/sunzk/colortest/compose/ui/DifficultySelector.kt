package com.sunzk.colortest.compose.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunzk.colortest.R
import com.sunzk.colortest.compose.style.commonButtonStyle
import com.sunzk.colortest.compose.ui.common.CustomDropdownMenu
import com.sunzk.colortest.game.IDifficulty
import com.sunzk.colortest.tools.ext.onClick

private const val TAG: String = "DifficultySelector"

@Composable
fun <T: IDifficulty> DifficultySelector(modifier: Modifier, difficulty: T, list: List<T>, onDifficultySelect: (T) -> Unit) {
	var showDifficultyDropdownMenu by remember { mutableStateOf(false) }
	Box(modifier) {
		DifficultyText(text = difficulty.text,
			modifier = Modifier
				.fillMaxHeight()
				.width(60.dp),
			onClick = {
				Log.d(TAG, "MockColorFragment#DifficultySelector- on DifficultyText click")
				showDifficultyDropdownMenu = !showDifficultyDropdownMenu
			})
		CustomDropdownMenu(
			modifier = Modifier
				.width(60.dp),
			expanded = showDifficultyDropdownMenu,
			onDismissRequest = { showDifficultyDropdownMenu = false }
		) {
			list.forEach { difficultyItem ->
				DifficultyText(text = difficultyItem.text,
					modifier = Modifier
						.fillMaxWidth(),
					onClick = {
						Log.d(TAG, "MockColorFragment#DifficultySelector- on DifficultyText click")
						showDifficultyDropdownMenu = false
						onDifficultySelect(difficultyItem)
					})
//				DropdownMenuItem(
//					modifier = Modifier
//						.commonButtonStyle()
//					,
//					text = { Text(text = difficultyItem.text, fontSize = 19.sp, color = colorResource(R.color.theme_txt_standard)) },
//					onClick = { onDifficultySelect(difficultyItem) }
//				)
			}
		}
	}
}

@Composable
private fun DifficultyText(text: String, modifier: Modifier, onClick: (() -> Unit)? = null) {
	Text(text = text, fontSize = 19.sp, color = colorResource(R.color.theme_txt_standard),
		textAlign = TextAlign.Center,
		maxLines = 1,
		modifier = modifier.then(Modifier
			.commonButtonStyle()
			.padding(5.dp)
			.onClick {
				Log.d(TAG, "MockColorFragment#DifficultyText- $text onClick!")
				if (onClick != null) {
					onClick()
				}
			}))
}