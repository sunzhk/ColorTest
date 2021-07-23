package com.sunzk.base.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.annotation.IntRange
import com.sunzk.base.utils.Logger.e
import com.sunzk.base.utils.Logger.i
import com.sunzk.base.utils.Logger.v
import java.io.BufferedReader
import java.io.Closeable
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * 网络工具类
 * Created by sunzhk on 2018/4/8.
 */
object NetWorkUtils {
    private const val TAG = "NetWorkUtils"
    private const val PING_DELAY_DEFAULT = 1000
    private const val PING_LOST_DEFAULT = 20

    /**
     * 判断网络是否有效.
     *
     * @return 如果网络连接正常则返回true。否则返回false
     */
    fun isNetworkConnected(context: Context): Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            return info != null && info.isConnected && info.state == NetworkInfo.State.CONNECTED
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 像心跳服务器发送请求，判断网络是否连通
     *
     * @param url      心跳服务地址
     * @param listener 回调
     */
    fun requestHeartbeat(url: String?, listener: ResultListener?) {
        object : Thread() {
            override fun run() {
                try {
                    val testUrl = URL(url)
                    val connection = testUrl.openConnection() as HttpURLConnection
                    if (200 == connection.responseCode) {
                        i(TAG, "requestHeartbeat success")
                        callBack(listener, true)
                    } else {
                        i(
                            TAG,
                            "requestHeartbeat failure with error http status:" + connection.responseCode
                        )
                        callBack(listener, false)
                    }
                    connection.disconnect()
                } catch (e: IOException) {
                    e(TAG, "requestHeartbeat failure with exception", e)
                    callBack(listener, false)
                }
            }
        }.start()
    }

    /**
     * ping 目标地址 5 次，时间间隔 0.5 秒，延迟不能超过 200 毫秒，丢包率不能超过 10%
     *
     * @param ip       目标地址
     * @param listener 回调
     */
    fun pingServer(ip: String, listener: ResultListener?) {
        pingServer(ip, 5, 0.5f, 200, 10, listener)
    }

    /**
     * ping 目标地址
     *
     * @param ip            目标地址
     * @param time          请求次数
     * @param interval      间隔时间，单位：秒
     * @param delayStandard 延迟标准，单位：毫秒
     * @param lostStandard  丢包率标准，单位：百分比
     * @param listener      回调
     */
    fun pingServer(
        ip: String,
        time: Int,
        interval: Float,
        delayStandard: Int,
        @IntRange(from = 0, to = 100) lostStandard: Int,
        listener: ResultListener?
    ) {
        var interval = interval
        if (interval < 0.2f) {
            interval = 0.2f
        }
        val process: Process
        process = try {
            Runtime.getRuntime().exec("ping -c $time -i $interval $ip")
        } catch (e: IOException) {
            e(TAG, "pingServer-exec", e)
            callBack(listener, false)
            return
        }
        object : Thread() {
            override fun run() {
                super.run()
                var temp: String
                val pingResultList = ArrayList<String>()
                var errorReader: BufferedReader? = null
                var inputReader: BufferedReader? = null
                try {
                    errorReader = BufferedReader(InputStreamReader(process.errorStream))
                    while (errorReader.readLine().also { temp = it } != null) {
                        v(TAG, "pingServer-errorReader:$temp")
                    }
                    inputReader = BufferedReader(InputStreamReader(process.inputStream))
                    while (inputReader.readLine().also { temp = it } != null) {
                        v(TAG, "pingServer-inputReader:$temp")
                        pingResultList.add(temp)
                    }
                    val isSuccess = checkPingResult(pingResultList)
                    callBack(listener, isSuccess)
                } catch (e: IOException) {
                    e(TAG, "pingServer", e)
                    callBack(listener, false)
                } finally {
                    closeStream(errorReader)
                    closeStream(inputReader)
                }
            }

            private fun checkPingResult(pingResultList: ArrayList<String>): Boolean {
                var singleResult = true
                var finalResult = false
                for (line in pingResultList) {
                    if (line.contains("time=") && line.contains(
                            "ms"
                        )
                    ) { //单次ping结果
                        val pingDelay = getPingDelay(line)
                        v(TAG, "getPingDelay:" + pingDelay)
                        if (pingDelay > delayStandard) {
                            singleResult = false
                        }
                    } else if (line.contains("packet loss")) { //ping总结果
                        val packageLoss = getPackageLoss(line)
                        v(TAG, "packageLoss:" + packageLoss)
                        if (packageLoss < lostStandard) {
                            finalResult = true
                        }
                    }
                }
                v(
                    TAG,
                    "checkPingResult-singleResult:" + singleResult + " finalResult:" + finalResult
                )
                return singleResult && finalResult
            }

            private fun getPingDelay(line: String): Float {
                var result = delayStandard + 1f
                if (!line.contains("time=")) {
                    return result
                }
                val params: Array<String> = line.split(" ").toTypedArray()
                for (param in params) {
                    if (param.contains("time=")) {
                        val delay: String = param.substring(
                            5,
                            param.length
                        )
                        try {
                            result = delay.toFloat()
                        } catch (e: Exception) {
                            e(TAG, "getPingDelay", e)
                        }
                    }
                }
                return result
            }

            private fun getPackageLoss(finalResult: String): Int {
                var result = 101
                if (!finalResult.contains("packet loss")) {
                    return result
                }
                val params: Array<String> =
                    finalResult.split(" ").toTypedArray()
                for (param in params) {
                    if (param.contains("%")) {
                        val loss: String = param.substring(
                            0,
                            param.length - 1
                        )
                        try {
                            result = loss.toInt()
                        } catch (e: Exception) {
                            e(TAG, "getPackageLoss", e)
                        }
                    }
                }
                return result
            }
        }.start()
    }

    private fun closeStream(stream: Closeable?) {
        if (stream == null) {
            return
        }
        try {
            stream.close()
        } catch (e: IOException) {
            e(TAG, "closeStream", e)
        }
    }

    private fun callBack(listener: ResultListener?, result: Boolean) {
        listener?.isSuccess(result)
    }

    interface ResultListener {
        fun isSuccess(isSuccessfully: Boolean)
    }

    interface NetworkListener {
        fun isNetworkOk(ok: Boolean)
    }
}