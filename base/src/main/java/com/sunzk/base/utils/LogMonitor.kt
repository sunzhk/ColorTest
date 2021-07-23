package com.sunzk.base.utils

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.sunzk.base.utils.Logger.w

/**
 * @author yongjie created on 2020/3/11.
 */
object LogMonitor {

    private const val TAG = "LogMonitor"
    private val mLogRunnable = Runnable { //打印出执行的耗时方法的栈消息
        val sb = StringBuilder()
        val stackTrace = Looper.getMainLooper().thread.stackTrace
        for (s in stackTrace) {
            sb.append(s.toString())
            sb.append("\n")
        }
        w(TAG, sb.toString())
    }

    private val mIoHandler: Handler

    /**
     * 开始计时
     */
    fun startMonitor(timeBlock: Long) {
        mIoHandler.postDelayed(mLogRunnable, timeBlock)
    }

    /**
     * 停止计时
     */
    fun removeMonitor() {
        mIoHandler.removeCallbacks(mLogRunnable)
    }

    init {
        val logThread = HandlerThread("log")
        logThread.start()
        mIoHandler = Handler(logThread.looper)
    }
}