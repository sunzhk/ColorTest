package com.sunzk.colortest.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.sunzk.colortest.tools.AccessibilityOperator

class AccessibilityControlService : AccessibilityService() {
	
	private val TAG: String = "AccessibilityControlService"

	var operator = AccessibilityOperator()

	override fun onServiceConnected() {
		super.onServiceConnected()
		Log.i(TAG, "onServiceConnected")
	}
	
	override fun onAccessibilityEvent(event: AccessibilityEvent) {
		// 此方法是在主线程中回调过来的，所以消息是阻塞执行的
		// 获取包名
		val pkgName: String = event.getPackageName().toString()
		val eventType: Int = event.getEventType()
		Log.v(TAG, "onAccessibilityEvent $pkgName - $eventType")
		operator.updateEvent(this, event)
	}

	override fun onInterrupt() {
		
	}
}