package com.sunzk.base.utils

import android.os.Looper
import android.util.Printer
import com.sunzk.base.utils.LogMonitor

/**
 * 用于检查主线程中，超时
 * @author yongjie created on 2020/3/11.
 */
object BlockDetectByPrinter {
    private const val TAG = "BlockDetectByPrinter"

    //分发和处理消息开始前的log
    private const val START = ">>>>> Dispatching"

    //分发和处理消息结束后的log
    private const val END = "<<<<< Finished"

    //方法耗时的卡口，单位毫秒
    private const val TIME_BLOCK = 50L
    fun start() {
        start(TIME_BLOCK)
    }

    fun start(timeBlock: Long) {
        Looper.getMainLooper().setMessageLogging(object : Printer {
            override fun println(x: String) {
                if (x.startsWith(START)) {
                    //开始计时
                    LogMonitor.startMonitor(timeBlock)
                }
                if (x.startsWith(END)) {
                    //结束计时，并计算出方法执行时间
                    LogMonitor.removeMonitor()
                }
            }
        })
    }
}