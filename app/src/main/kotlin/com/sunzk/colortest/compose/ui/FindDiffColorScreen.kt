package com.sunzk.colortest.compose.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sunzk.colortest.R
import com.sunzk.colortest.compose.style.commonButtonStyle
import com.sunzk.colortest.game.findDiffColor.FindDiffColorViewModel
import com.sunzk.colortest.tools.ext.onClick
import com.sunzk.colortest.view.FindDiffView

private const val TAG: String = "FindDiffColorScreen"

@Composable
fun FindDiffColorScreen(
    viewModel: FindDiffColorViewModel,
    onCorrectAnswer: (FindDiffView) -> Unit,
) {
    val data by viewModel.currentGameData.collectAsStateWithLifecycle()
    var findDiffViewRef by remember { mutableStateOf<FindDiffView?>(null) }
    LaunchedEffect(findDiffViewRef, data) {
        Log.d(TAG, "FindDiffColorActivity#bindData- new data: $data")
        findDiffViewRef?.let { view ->
            if (view.countPerSide != data.countPerSide) {
                view.resetCount(data.countPerSide)
            }
            view.resetColor(data.baseColor, data.diffColor, data.diffIndex)
        }
    }
    val buttonSize = dimensionResource(R.dimen.activity_find_diff_color_controller_button_size)
    val buttonSpacing = dimensionResource(R.dimen.activity_find_diff_color_controller_button_spacing)
    val levelWidth = dimensionResource(R.dimen.activity_find_diff_color_controller_level_width)
    val levelHeight = dimensionResource(R.dimen.activity_find_diff_color_controller_level_height)
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp),
    ) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                factory = { context ->
                    FindDiffView(context).apply {
                        findDiffViewRef = this
                        setOnDiffColorViewClickListener { view, result ->
                            Log.d(TAG, "onClick: $result")
                            if (result) {
                                (view as? FindDiffView)?.let { onCorrectAnswer(it) }
                            }
                        }
                    }
                },
                update = { view ->
                    findDiffViewRef = view
                },
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.seek_fine_tuning_left),
                contentDescription = null,
                modifier = Modifier
                    .size(buttonSize)
                    .onClick {
                        val currentLevel = data.level
                        if (currentLevel > FindDiffColorViewModel.MIN_LEVEL) {
                            viewModel.changeLevel(currentLevel - 1)
                        }
                    },
            )
            Spacer(Modifier.width(buttonSpacing))
            Text(
                text = "${data.level}",
                color = colorResource(R.color.theme_txt_standard),
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(levelWidth)
                    .height(levelHeight),
            )
            Spacer(Modifier.width(buttonSpacing))
            Image(
                painter = painterResource(R.drawable.seek_fine_tuning_right),
                contentDescription = null,
                modifier = Modifier
                    .size(buttonSize)
                    .onClick {
                        val currentLevel = data.level
                        if (currentLevel < FindDiffColorViewModel.MAX_LEVEL) {
                            viewModel.changeLevel(currentLevel + 1)
                        }
                    },
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
                .commonButtonStyle()
                .onClick {
                    Log.d(TAG, "FindDiffColorActivity#resetColor")
                    viewModel.nextRandomData()
                }
                .padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.find_diff_shuffle),
                color = colorResource(R.color.common_bt_text),
                fontSize = 21.sp,
            )
        }
    }
}
