package com.sunzk.compose.style

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.sunzk.base.contracts.buildIf
import com.sunzk.familycontroller.compose.style.Colors

private const val TAG: String = "CommonStyle"

@Composable
fun Modifier.commonButton(needShadow: Boolean = false, needBorder: Boolean = false): Modifier = 
	this.then(
		Modifier
			.background(Colors.common_bt_bg, RoundedCornerShape(5.dp))
			.buildIf<Modifier>(needShadow) { it.shadow(elevation = 2.dp, shape = RoundedCornerShape(5.dp), clip = false) }
			.buildIf(needBorder) { it.border(1.dp, Colors.common_bt_stroke, RoundedCornerShape(5.dp)) }
			.padding(10.dp)
	)

@Composable
fun Modifier.commonBlock(needShadow: Boolean = false, needBorder: Boolean = false): Modifier =
	this.then(
		Modifier
			.buildIf<Modifier>(needShadow) { it.shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp), clip = false) }
			.background(Colors.common_block_bg, RoundedCornerShape(8.dp))
			.buildIf<Modifier>(needBorder) { it.border(1.dp, Colors.common_block_stroke, RoundedCornerShape(8.dp)) }
	)

private val ButtonBlockRadius = 10.dp

@Composable
fun Modifier.commonBottomBlock(needShadow: Boolean = false): Modifier =
	this.then(
		Modifier
			.buildIf<Modifier>(needShadow) { it.shadow(elevation = 5.dp, shape = RoundedCornerShape(topStart = ButtonBlockRadius, topEnd = ButtonBlockRadius), clip = false) }
			.background(Colors.common_block_bg, RoundedCornerShape(topStart = ButtonBlockRadius, topEnd = ButtonBlockRadius))
	)

@Composable
fun Modifier.commonListItem(needShadow: Boolean = false, needBorder: Boolean = true): Modifier =
	this.then(
		Modifier
			.buildIf<Modifier>(needShadow) { it.shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp), clip = false) }
			.background(Colors.common_block_bg, RoundedCornerShape(8.dp))
			.buildIf<Modifier>(needBorder) {
				it.border(1.dp, Colors.common_block_stroke, RoundedCornerShape(8.dp)) 
			}
	)