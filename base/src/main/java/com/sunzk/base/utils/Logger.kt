package com.sunzk.base.utils

import android.os.Process
import android.util.Log
import android.view.KeyEvent
import java.text.SimpleDateFormat
import java.util.*

/**
 * log 封装类，用于扩充日志内容，限制日志打印条件。<br></br>
 * d、v级别日志仅在debug模式下输出到logcat<br></br>
 * 在插入U盘，并且U盘根目录下存放了校验文件的情况下，会向U盘中输出<br></br>
 * 日志会存放在U盘根目录下<br></br>
 * 使用命令行向U盘中写入日志文件，但只能获得应用本身的日志<br></br>
 * todo 多进程时可能会出现冲突
 */
object Logger {
    private const val TAG = "Logger"
    private val logDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private var printUSBLog = false

    /**
     * 强制打印日志，部分特殊情况下无法获取日志时使用
     */
    private const val forcePrint = false

    /**
     * 组合键：开启日志
     */
    private val KEY_COMBINATION_OPEN = intArrayOf(
        KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,
        KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,
        KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_UP,
        KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN
    )

    /**
     * 组合键：关闭日志
     */
    private val KEY_COMBINATION_CLOSE = intArrayOf(
        KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,
        KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,
        KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_LEFT,
        KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_RIGHT
    )

    /**
     * 在debug模式下或远程调试模式下打印一条 [Log.VERBOSE] 日志
     *
     * @param tag 用于识别日志消息的来源，建议使用当前类的类名
     * @param msg 想要打印的日志
     */
    fun v(tag: String, msg: String) {
        if (!needPrintLog()) {
            return
        }
        Log.v(tag, spliceLogTime(msg))
    }

    fun v(tag: String, msg: String, vararg data: Any) {
        if (!needPrintLog()) {
            return
        }
        Log.v(tag, spliceLogTime(splicingLogMsg(tag, msg, data)))
    }

    /**
     * 在debug模式下或远程调试模式下打印一条 [Log.DEBUG] 日志
     *
     * @param tag 用于识别日志消息的来源，建议使用当前类的类名
     * @param msg 想要打印的日志
     */
    fun d(tag: String, msg: String) {
        if (!needPrintLog()) {
            return
        }
        Log.d(tag, spliceLogTime(msg))
    }

    /**
     * 在debug模式下或远程调试模式下打印一条 [Log.DEBUG] 日志
     *
     * @param tag 用于识别日志消息的来源，建议使用当前类的类名
     * @param msg 想要打印的日志
     */
    fun d(tag: String, msg: String, t: Throwable) {
        if (!needPrintLog()) {
            return
        }
        Log.d(tag, spliceLogTime(msg), t)
    }

    /**
     * 在debug模式下或远程调试模式下打印一条 [Log.DEBUG] 日志
     *
     * @param tag 用于识别日志消息的来源，建议使用当前类的类名
     * @param msg 想要打印的日志
     * @param throwable   想要一同打印的异常
     */
    fun d(tag: String, msg: String, throwable: Throwable, vararg data: Any) {
        if (!needPrintLog()) {
            return
        }
        Log.d(tag, spliceLogTime(splicingLogMsg(tag, msg, throwable, data)), throwable)
    }

    fun d(tag: String, msg: String, vararg data: Any) {
        if (!needPrintLog()) {
            return
        }
        Log.d(tag, spliceLogTime(splicingLogMsg(tag, msg, data)))
    }

    /**
     * 在debug模式下或远程调试模式下打印一条 [Log.INFO] 日志
     *
     * @param tag 用于识别日志消息的来源，建议使用当前类的类名
     * @param msg 想要打印的日志
     */
    fun i(tag: String, msg: String) {
        Log.i(tag, spliceLogTime(msg))
    }

    fun i(tag: String, msg: String, vararg data: Any) {
        Log.i(tag, spliceLogTime(splicingLogMsg(tag, msg, data)))
    }

    /**
     * 在debug模式下或远程调试模式下打印一条 [Log.WARN] 日志
     *
     * @param tag 用于识别日志消息的来源，建议使用当前类的类名
     * @param msg 想要打印的日志
     */
    fun w(tag: String, msg: String) {
        Log.w(tag, spliceLogTime(msg))
    }

    fun w(tag: String, msg: String, throwable: Throwable) {
        Log.w(tag, spliceLogTime(msg), throwable)
    }

    fun w(tag: String, msg: String, vararg data: Any) {
        Log.w(tag, spliceLogTime(splicingLogMsg(tag, msg, data)))
    }

    /**
     * 在debug模式下或远程调试模式下打印一条 [Log.ERROR] 日志
     *
     * @param tag 用于识别日志消息的来源，建议使用当前类的类名
     * @param msg 想要打印的日志
     */
    fun e(tag: String, msg: String) {
        Log.e(tag, spliceLogTime(msg))
    }

    /**
     * 在debug模式下或远程调试模式下打印一条 [Log.ERROR] 日志
     *
     * @param tag 用于识别日志消息的来源，建议使用当前类的类名
     * @param msg 想要打印的日志
     * @param t   想要一同打印的异常
     */
    fun e(tag: String, msg: String, t: Throwable?) {
        if (t == null) {
            Log.e(tag, spliceLogTime(msg))
        } else {
            Log.e(tag, spliceLogTime(msg), t)
        }
    }

    /**
     * 在debug模式下或远程调试模式下打印一条 [Log.ERROR] 日志
     *
     * @param tag       用于识别日志消息的来源，建议使用当前类的类名
     * @param msg       想要打印的日志
     * @param data      想要一同打印的数据
     */
    fun e(tag: String, msg: String, vararg data: Any?) {
        Log.e(tag, spliceLogTime(splicingLogMsg(tag, msg, data)))
    }

    /**
     * 在debug模式下或远程调试模式下打印一条 [Log.ERROR] 日志
     *
     * @param tag       用于识别日志消息的来源，建议使用当前类的类名
     * @param msg       想要打印的日志
     * @param throwable 想要一同打印的异常
     * @param data      想要一同打印的数据
     */
    fun e(tag: String, msg: String, throwable: Throwable?, vararg data: Any?) {
        Log.e(tag, spliceLogTime(splicingLogMsg(tag, msg, data)), throwable)
    }

    /* ---------------- 日志方法结束 ---------------- */
    private fun needPrintLog(): Boolean {
        return AppUtils.isDebug() || printUSBLog
    }

    private fun spliceLogTime(msg: String): String {
        return spliceLogTime(System.currentTimeMillis(), msg)
    }

    /**
     * 使用logcat命令写日志文件时，是没有日志时间的，所以需要给每条日志的msg中增加时间信息
     * @param time
     * @param msg
     * @return
     */
    private fun spliceLogTime(time: Long, msg: String): String {
        return if (printUSBLog) {
            val pid = Process.myPid().toString()
            val tid = Thread.currentThread().id.toString()
            String.format(
                "%s\t%s-%s\t%s",
                logDateFormat.format(Date(time)),
                pid,
                tid,
                msg
            )
        } else {
            msg
        }
    }

    private fun splicingLogMsg(tag: String, msg: String, vararg data: Any?): String {
        return splicingLogMsg(tag, msg, null, data)
    }

    private fun splicingLogMsg(
        tag: String,
        msg: String,
        throwable: Throwable?,
        vararg data: Any?
    ): String {
        if (data == null || data.isEmpty()) {
            return msg
        }
        val stringBuilder = StringBuilder()
        stringBuilder.append(msg)
        for (datum in data) {
            try {
                stringBuilder.append("\t")
                stringBuilder.append(datum ?: "[数据丢失]")
            } catch (e: Exception) {
                stringBuilder.append("[数据损坏]")
            }
        }
        return stringBuilder.toString()
    }
}