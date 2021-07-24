package com.sunzk.colortest.tools

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 控制无障碍服务
 * Created by mazaiting on 2017/8/18.
 */
class AccessibilityOperator {

	private val TAG: String = "AccessibilityOperator"
	
	private var mAccessibilityService: AccessibilityService? = null
	private var mAccessibilityEvent: AccessibilityEvent? = null

	/**
	 * 更新事件
	 *
	 * @param service
	 * @param event
	 */
	fun updateEvent(service: AccessibilityService, event: AccessibilityEvent) {
		if (mAccessibilityService == null) {
			mAccessibilityService = service
		}
		mAccessibilityEvent = event
	}

	/**
	 * 根据Text搜索所有符合条件的节点，模糊搜索方式
	 *
	 * @param text
	 * @return
	 */
	fun clickText(text: String?): Boolean {
		val nodeInfo: AccessibilityNodeInfo? = rootNodeInfo
		if (nodeInfo != null) {
			val nodeInfos: List<AccessibilityNodeInfo> =
				nodeInfo.findAccessibilityNodeInfosByText(text)
			return performClick(nodeInfos)
		}
		return false
	}

	/**
	 * 获取根节点
	 *
	 * @return
	 */
	private val rootNodeInfo: AccessibilityNodeInfo?
		get() {
			Log.e(TAG, "getRootNodeInfo: ")
			val curEvent: AccessibilityEvent? = mAccessibilityEvent
			var nodeInfo: AccessibilityNodeInfo? = null
			if (mAccessibilityService != null) {
				// 获得窗体根节点
				nodeInfo = mAccessibilityService?.rootInActiveWindow
			}
			return nodeInfo
		}

	/**
	 * 模拟点击
	 *
	 * @param nodeInfos
	 * @return true 成功； false 失败。
	 */
	private fun performClick(nodeInfos: List<AccessibilityNodeInfo>?): Boolean {
		if (nodeInfos != null && !nodeInfos.isEmpty()) { // 判断是否非空
			var nodeInfo: AccessibilityNodeInfo
			for (element in nodeInfos) {
				nodeInfo = element // 获得要点击的View
				// 进行模拟点击
				if (nodeInfo.isEnabled()) { // 如果可以点击
					return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
				}
			}
		}
		return false
	}

}