package com.sunzk.colortest.compose.style

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.sunzk.colortest.R

@Composable
fun Modifier.commonButtonStyle(): Modifier = 
	this.background(colorResource(R.color.common_bt_bg), RoundedCornerShape(5.dp))
		.border(1.dp, colorResource(R.color.common_bt_stroke), RoundedCornerShape(5.dp))