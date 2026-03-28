package com.sunzk.colortest.game.intermediateColor

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blankj.utilcode.util.ScreenUtils
import com.sunzk.colortest.R
import com.sunzk.colortest.compose.style.commonButtonStyle
import com.sunzk.colortest.compose.ui.DifficultySelector
import com.sunzk.colortest.compose.ui.common.CommonConfirmDialog
import com.sunzk.colortest.compose.ui.common.CommonSettlementDialog
import com.sunzk.colortest.tools.ext.onClick
import com.sunzk.colortest.view.colorPicker.MergedColorPicker
import com.sunzk.colortest.tools.ext.px2dp

private const val TAG: String = "IntermediateColorScreen"

@Composable
fun IntermediateColorScreen(navController: NavController, viewModel: IntermediateColorViewModel) {
    val settlementDialogState = remember { mutableStateOf<Boolean?>(null) }
    var showDifficultyHint by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        val settlementShow by settlementDialogState
        settlementShow?.let { isRight ->
            CommonSettlementDialog(
                isRight = isRight,
                onDismissRequest = { settlementDialogState.value = null },
                onCancel = {
                    settlementDialogState.value = null
                    navController.popBackStack()
                },
                onConfirm = {
                    settlementDialogState.value = null
                    viewModel.nextQuestion()
                },
            )
        }
        if (showDifficultyHint) {
            CommonConfirmDialog(
                title = "游戏说明",
                message = "• 拖动下方的滚动条，使中间色块为左右两个色块的中间色\n" +
                    "• 无论哪种难度下，左右色块的色相、饱和度、明度中都有两项是相同的\n" +
                    "• 但是${IntermediateColorResult.Difficulty.Easy.text}会锁定相同的两个值\n" +
                    "    ${IntermediateColorResult.Difficulty.Normal.text}会锁定其中一个相同的值\n" +
                    "    ${IntermediateColorResult.Difficulty.Hard.text}不锁定任何值",
                messageTextAlign = TextAlign.Start,
                onDismissRequest = { showDifficultyHint = false },
                onButtonClick = { showDifficultyHint = false },
            )
        }
        val pageData by viewModel.pageData.collectAsStateWithLifecycle()
        Title(
            Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.common_title_height)),
            pageData,
            viewModel,
            context,
        ) { showDifficultyHint = true }
        ColorArea(viewModel, pageData, Modifier
            .fillMaxWidth()
            .weight(1f))
        GameController(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            pageData,
            viewModel,
            settlementDialogState,
        )
    }
}

@Composable
private fun Title(
    modifier: Modifier,
    pageData: IntermediateColorPageData,
    viewModel: IntermediateColorViewModel,
    context: android.content.Context,
    onShowDifficultyHint: () -> Unit,
) {
    var showDifficultyDropdownMenu by remember { mutableStateOf(false) }
    Box(modifier) {
        Row(Modifier.align(Alignment.CenterStart)) {
            DifficultySelector(
                Modifier
                    .wrapContentWidth()
                    .fillMaxHeight(),
                pageData.difficulty,
                IntermediateColorResult.Difficulty.entries,
            ) { difficulty ->
                Log.d(TAG, "IntermediateColorScreen#DifficultySelector- on DifficultyText click")
                showDifficultyDropdownMenu = !showDifficultyDropdownMenu
                viewModel.switchDifficulty(difficulty)
            }
            Image(
                painter = painterResource(R.mipmap.icon_common_info),
                contentDescription = "hint",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .padding(3.dp)
                    .onClick { onShowDifficultyHint() },
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(61.dp)
                .fillMaxHeight()
                .commonButtonStyle(),
        ) {
            Text(
                text = stringResource(R.string.list_history),
                color = colorResource(R.color.common_bt_text),
                fontSize = 19.sp,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center)
                    .onClick {
                        context.startActivity(Intent(context, IntermediateColorHistoryActivity::class.java))
                    },
            )
        }
    }
}

@Composable
private fun ColorArea(
    viewModel: IntermediateColorViewModel,
    pageData: IntermediateColorPageData,
    modifier: Modifier,
) {
    val pickColor by viewModel.pickColor.collectAsStateWithLifecycle()

    val spacing = 25.dp
    val screenWidth = ScreenUtils.getAppScreenWidth().px2dp.dp
    val sideLength = (screenWidth - spacing * 4) / 3
    var radius = sideLength / 8
    val minRadius = 10.dp
    if (radius < minRadius) {
        radius = minRadius
    }
    Box(modifier) {
        Box(
            modifier = Modifier
                .width(sideLength)
                .height(sideLength)
                .align(Alignment.CenterStart)
                .background(pageData.questionLeftColor.composeColor, RoundedCornerShape(radius)),
        )
        Box(
            modifier = Modifier
                .width(sideLength)
                .height(sideLength)
                .align(Alignment.Center)
                .background(pickColor.composeColor, RoundedCornerShape(radius)),
        )
        Box(
            modifier = Modifier
                .width(sideLength)
                .height(sideLength)
                .align(Alignment.CenterEnd)
                .background(pageData.questionRightColor.composeColor, RoundedCornerShape(radius)),
        )
    }
}

@Composable
private fun GameController(
    modifier: Modifier,
    pageData: IntermediateColorPageData,
    viewModel: IntermediateColorViewModel,
    settlementDialogState: MutableState<Boolean?>,
) {
    Row(
        modifier = modifier
            .padding(bottom = 50.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
                .commonButtonStyle()
                .padding(10.dp),
        ) {
            Text(
                text = "太难了，换一题",
                color = colorResource(R.color.common_bt_text),
                fontSize = 19.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .onClick {
                        viewModel.nextQuestion()
                    },
            )
        }
        Box(
            modifier = Modifier
                .padding(start = 15.dp)
                .wrapContentHeight()
                .weight(1f)
                .commonButtonStyle()
                .padding(10.dp),
        ) {
            Text(
                text = "就是这个颜色了",
                color = colorResource(R.color.common_bt_text),
                fontSize = 19.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .onClick {
                        viewModel.saveToDB()
                        val leftColor = viewModel.pageData.value.questionLeftColor
                        val rightColor = viewModel.pageData.value.questionRightColor
                        val answerColor = viewModel.pageData.value.answerColor
                        val isRight = viewModel.pageData.value.difficulty.isRight(
                            leftColor,
                            rightColor,
                            answerColor,
                        )
                        settlementDialogState.value = isRight
                    },
            )
        }
    }
    MergedColorPicker(
        Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 35.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        pageData.answerColor,
    ) { hsb ->
        Log.d(TAG, "IntermediateColorScreen#GameController- on color pick $hsb")
        viewModel.updatePickColor(hsb)
    }
}
