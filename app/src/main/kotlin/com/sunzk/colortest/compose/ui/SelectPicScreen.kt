package com.sunzk.colortest.compose.ui

import android.graphics.Color as AndroidColor
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.shizhefei.view.largeimage.LargeImageView
import com.sunzk.colortest.activity.SelectPicActivity
import kotlin.math.roundToInt

@Composable
fun SelectPicScreen(
    onLargeImageViewReady: (LargeImageView) -> Unit,
) {
    var floatX by remember { mutableStateOf(0f) }
    var floatY by remember { mutableStateOf(0f) }
    var rgbText by remember { mutableStateOf("") }
    var showOverlay by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                LargeImageView(ctx).apply {
                    onLargeImageViewReady(this)
                    setOnTouchListener { v: View, event: MotionEvent ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                                val bitmap = SelectPicActivity.getBitmapFromView(v)
                                val pixel = bitmap.getPixel(
                                    event.x.toInt().coerceIn(0, bitmap.width - 1),
                                    event.y.toInt().coerceIn(0, bitmap.height - 1),
                                )
                                floatX = event.x
                                floatY = event.y
                                rgbText = "${AndroidColor.red(pixel)}," +
                                    "${AndroidColor.green(pixel)}," +
                                    "${AndroidColor.blue(pixel)}"
                                showOverlay = true
                            }
                            MotionEvent.ACTION_UP -> performClick()
                        }
                        false
                    }
                }
            },
        )
        if (showOverlay) {
            Row(
                modifier = Modifier
                    .offset { IntOffset(floatX.roundToInt(), floatY.roundToInt()) },
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(2.dp)
                        .background(Color.Black),
                )
                Text(
                    text = rgbText,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .offset(y = (-25).dp)
                        .background(Color(0x90000000)),
                    fontSize = 21.sp,
                    color = Color.White,
                )
            }
        }
    }
}
