package com.sunzk.colortest.compose.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sunzk.colortest.R
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.compose.style.commonButtonStyle
import com.sunzk.colortest.tools.ext.onClick

/**
 * 悬浮设置面板（由 [com.sunzk.colortest.tools.FloatingSettingWindowManager] 挂载到窗口）。
 */
@Composable
fun FloatingSettingScreen(
    isFold: Boolean,
    onFold: () -> Unit,
    onUnfold: () -> Unit,
) {
    val bgmOn by Runtime.globalBGMSwitch.collectAsStateWithLifecycle()
    val colorPickerType by Runtime.colorPickerType.collectAsStateWithLifecycle()
    var darkMode by remember { mutableStateOf(Runtime.darkMode) }

    val iconSize = dimensionResource(R.dimen.floating_setting_icon_size)
    val iconPadding = dimensionResource(R.dimen.floating_setting_icon_padding)
    val switchWidth = dimensionResource(R.dimen.common_switch_width)
    val switchHeight = dimensionResource(R.dimen.common_switch_height)
    val textSizeSp = 17.sp

    Column(Modifier.wrapContentSize()) {
        AnimatedVisibility(
            visible = isFold,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            GearIconBox(
                iconSize = iconSize,
                iconPadding = iconPadding,
                onUnfold = onUnfold,
            )
        }
        AnimatedVisibility(
            visible = !isFold,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column(
                Modifier
                    .commonButtonStyle()
                    .padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
            ) {
                Image(
                    painter = painterResource(R.mipmap.icon_setting_close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .padding(iconPadding)
                        .onClick { onFold() },
                )
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .onClick { Runtime.toggleGlobalBGMSwitch() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.mipmap.icon_setting_background_music),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                            .padding(3.dp),
                    )
                    Text(
                        text = "背景音乐：",
                        color = colorResource(R.color.common_bt_text),
                        fontSize = textSizeSp,
                    )
                    Image(
                        painter = painterResource(
                            if (bgmOn) {
                                R.mipmap.icon_common_switch_on
                            } else {
                                R.mipmap.icon_common_switch_off
                            },
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(switchWidth, switchHeight),
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .onClick {
                            darkMode = Runtime.switchNextDarkMode()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.mipmap.icon_setting_dark_mode),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                            .padding(iconPadding),
                    )
                    Text(
                        text = "深色模式：${darkMode.text}",
                        color = colorResource(R.color.common_bt_text),
                        fontSize = textSizeSp,
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .onClick { Runtime.switchNextColorPickerType() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.mipmap.icon_setting_color_picker),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                            .padding(iconPadding),
                    )
                    Text(
                        text = "取色器模式：${colorPickerType.text}",
                        color = colorResource(R.color.common_bt_text),
                        fontSize = textSizeSp,
                    )
                }
            }
        }
    }
}

@Composable
private fun GearIconBox(
    iconSize: Dp,
    iconPadding: Dp,
    onUnfold: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(iconSize)
            .commonButtonStyle()
            .padding(iconPadding)
            .onClick { onUnfold() },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.mipmap.icon_setting),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
