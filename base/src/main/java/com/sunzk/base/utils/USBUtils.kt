package com.sunzk.base.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/**
 * USB 工具类，主要用于U盘状态监听以及U盘读写
 * Created by sunzhk on 2019/1/22.
 */
object USBUtils {
    
    private const val TAG = "USBUtils"
    private val listeners = ArrayList<(isUSBReady: Boolean, usbPath: String?) -> Unit?>()
    fun register(listener: (isUSBReady: Boolean, usbPath: String?) -> Unit?) {
        listeners.add(listener)
    }

    fun unregister(listener: (isUSBReady: Boolean, usbPath: String?) -> Unit?) {
        listeners.remove(listener)
    }

    val uSBPath: String?
        get() {
            val path = USBBroadcastReceiver.usbPath
            if (!StringUtils.isBlank(path)) {
                return path
            }
            val usbList = allExternalSdcardPath
            return if (!usbList.isEmpty()) {
                usbList[0]
            } else null
        }// 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件// 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径// 将常见的linux分区过滤掉

    // 下面这些分区是我们需要的
    // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
    val allExternalSdcardPath: List<String>
        get() {
            val pathList: MutableList<String> = ArrayList()
            var isr: InputStreamReader? = null
            try {
                // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
                val runtime = Runtime.getRuntime()
                val proc = runtime.exec("mount")
                isr = InputStreamReader(proc.inputStream)
                var line: String
                val br = BufferedReader(isr)
                while (br.readLine().also { line = it } != null) {
                    // 将常见的linux分区过滤掉
                    if (line.contains("proc") || line.contains("tmpfs") || line.contains("media") || line.contains(
                            "asec"
                        ) || line.contains("secure") || line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data") || line.contains("shell") || line.contains(
                            "root"
                        ) || line.contains("acct") || line.contains("misc") || line.contains("obb")
                    ) {
                        continue
                    }

                    // 下面这些分区是我们需要的
                    if (line.contains("fat") || line.contains("fuse") || line.contains("ntfs")) {
                        // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
                        val items = line.split(" ").toTypedArray()
                        if (items != null && items.size > 1) {
                            val path = items[1].toLowerCase(Locale.getDefault())
                            // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                            if (path != null && !pathList.contains(path) && path.contains("sd")) pathList.add(
                                items[1]
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (isr != null) {
                    try {
                        isr.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return pathList
        }

    class USBBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var usbState = false
            var callBackPath: String? = null
            if (intent.action == Intent.ACTION_MEDIA_MOUNTED) { //U盘插入
                try {
                    val path = intent.dataString
                    usbPath = path!!.split("file://").toTypedArray()[1] //U盘路径
                } catch (e: Exception) {
                    usbPath = null
                }
                callBackPath = uSBPath
                usbState = !StringUtils.isBlank(callBackPath)
            } else if (intent.action == Intent.ACTION_MEDIA_UNMOUNTED) { //U盘拔出
                usbPath = null
            }
            Logger.d(TAG, "USBBroadcastReceiver#onReceive- $usbState , $callBackPath")
            callOnUSBStateChangeListener(usbState, callBackPath)
        }

        private fun callOnUSBStateChangeListener(state: Boolean, usbPath: String?) {
            for (listener in listeners) {
                listener.invoke(state, usbPath)
            }
        }

        companion object {
            var usbPath: String? = null
                private set
        }
    }

}