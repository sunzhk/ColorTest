package com.sunzk.colortest.compose.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

/** 单按钮通用说明对话框（替代原 XML + `LifecycleDialog` 实现）。 */
@Composable
fun CommonConfirmDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    buttonText: String = stringResource(R.string.common_confirm),
    titleTextAlign: TextAlign = TextAlign.Center,
    messageTextAlign: TextAlign = TextAlign.Center,
    properties: DialogProperties = DialogProperties(),
    onButtonClick: () -> Unit = { onDismissRequest() },
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Column(modifier = Modifier.wrapContentSize().commonBlock()) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colorResource(R.color.theme_txt_standard),
                textAlign = titleTextAlign,
                modifier = Modifier
                    .padding(top = 18.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            )
            Text(
                message,
                fontSize = 14.sp,
                color = colorResource(R.color.theme_txt_standard),
                textAlign = messageTextAlign,
                modifier = Modifier
                    .padding(top = 14.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            )
            HorizontalDivider(
                modifier = Modifier.padding(top = 13.dp),
                thickness = 0.5.dp,
                color = colorResource(R.color.common_dialog_divider),
            )
            Text(
                buttonText,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = colorResource(R.color.common_dialog_btn_right),
                modifier = Modifier
                    .height(43.dp)
                    .fillMaxWidth()
                    .clickableWithoutIndication { onButtonClick() },
            )
        }
    }
}
