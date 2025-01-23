package com.sunzk.demo.tools.ext

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View

fun View.dp2px(dp: Float): Float {
	return TypedValue.applyDimension(
		TypedValue.COMPLEX_UNIT_DIP, dp,
		resources.displayMetrics
	)
}

fun getSystemStatusBarHeight(context: Context): Int {
	var statusHeight = 0
	val localRect = Rect()
	(context as Activity).window.decorView.getWindowVisibleDisplayFrame(localRect)
	statusHeight = localRect.top
	if (0 == statusHeight) {
		val localClass: Class<*>
		try {
			localClass = Class.forName("com.android.internal.R\$dimen")
			val localObject = localClass.newInstance()
			val i5: Int = localClass.getField("status_bar_height")[localObject].toString().toIntOrNull() ?: 0
			statusHeight = context.getResources().getDimensionPixelSize(i5)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	return statusHeight
}