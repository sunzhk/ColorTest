package com.sunzk.base.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by sunzhk on 2019/1/22.
 */
class USBBroadcastReceiver : BroadcastReceiver() {

    companion object {
        var usbPath: String? = null
            private set
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MEDIA_MOUNTED) { //U盘插入
            usbPath = try {
                val path = intent.dataString
                path!!.split("file://").toTypedArray()[1] //U盘路径
            } catch (e: Exception) {
                null
            }
        } else if (intent.action == Intent.ACTION_MEDIA_UNMOUNTED) { //U盘拔出
            usbPath = null
        }
    }
}