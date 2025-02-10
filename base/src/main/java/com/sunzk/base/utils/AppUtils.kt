package com.sunzk.base.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.TrafficStats
import android.os.Build
import android.os.Bundle
import android.os.Looper
import com.sunzk.base.utils.Logger.d
import com.sunzk.base.utils.Logger.e
import com.sunzk.base.utils.StringUtils.isEmpty
import java.nio.charset.Charset

/**
 * 应用工具类，包含一些Android相关功能的工具
 * Created by sunzhk on 2018/4/8.
 */
object AppUtils {
    private const val TAG = "AppUtils"
    private var isDebug: Boolean? = null //是否是debug模式

    /**
     * 初始化，需要在 Application 中完成
     *
     * @param applicationContext 上下文
     */
    fun init(applicationContext: Context) {
        syncIsDebug(applicationContext)
    }

    /**
     * 是否是debug模式
     */
    fun isDebug(): Boolean {
        return isDebug != null && isDebug!!
    }

    /**
     * 设置日志是否启用
     */
    fun setLogEnable(enable: Boolean) {
        isDebug = enable
    }

    /**
     * 判断当前是否是在主线程
     *
     * @return 如果在主线程则返回true，否则返回false
     */
    val isInMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()

    /**
     * 判断目标线程是否是主线程
     *
     * @param thread 目标线程
     * @return 如果目标线程是主线程则返回true，否则返回false
     */
    fun isMainThread(thread: Thread): Boolean {
        return thread === Looper.getMainLooper().thread
    }

    /**
     * 初始化 debug 状态。需要在 Application 中完成
     *
     * @param context 上下文
     */
    private fun syncIsDebug(context: Context) {
        if (isDebug == null) {
            isDebug =
                context.applicationInfo != null && context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        }
    }

    /**
     * 获取版本号，1,2,3,4 之类的整数，随版本的发布逐步增大
     *
     * @param context 上下文
     * @return 当前应用的版本号。获取失败则返回 -1
     */
    fun getVersionCode(context: Context): Long {
        return try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(context.packageName, 0)
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
		        info.longVersionCode
	        } else {
                @Suppress("DEPRECATION")
		        info.versionCode.toLong()
	        }
        } catch (e: Exception) {
            d(TAG, "getVersionCode", e)
            -1
        }
    }

    /**
     * 获取版本名称，比如1.1.1，2.1 等,字符窜
     *
     * @param context 上下文
     * @return 当前应用的版本名。获取失败则返回 null
     */
    fun getVersionName(context: Context): String? {
        return try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(context.packageName, 0)
            info.versionName
        } catch (e: Exception) {
            d(TAG, "getVersionName", e)
            null
        }
    }

    /**
     * 写SharedPreferences，适合写单条数据
     *
     * @param category SharedPreferences名称
     * @param key      数据的Key
     * @param value    数据的值
     * @param context  上下文
     */
    fun writeSetting(category: String?, key: String?, value: String?, context: Context) {
        val mySharedPreferences = context.getSharedPreferences(category, Activity.MODE_PRIVATE)
        val editor = mySharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * 读SharedPreferences，适合读单条数据
     *
     * @param category SharedPreferences名称
     * @param key      数据的Key
     * @param context  上下文
     * @return 数据的值
     */
    fun readSetting(category: String?, key: String?, context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(category, Activity.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")
    }

    /**
     * 获取总的接受字节数，包含Mobile和WiFi等
     *
     * @param context 上下文
     * @return 总的接受字节数。如果失败，则返回 -1
     */
    @SuppressLint("WrongConstant")
    fun getUidRxBytes(context: Context): Long {
        val pm = context.packageManager
        var ai: ApplicationInfo? = null
        try {
            ai = pm.getApplicationInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        } catch (e: PackageManager.NameNotFoundException) {
            d(TAG, "getUidRxBytes", e)
        }
        if (ai == null) {
            return -1
        }
        return if (TrafficStats.getUidRxBytes(ai.uid) == TrafficStats.UNSUPPORTED.toLong()) 0 else TrafficStats.getTotalRxBytes() / 1024
    }

    /**
     * 获取屏幕宽度
     *
     * @param context 上下文
     * @return 屏幕宽度
     */
    fun getScreenWidth(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels
    }

    /**
     * 获得屏幕高度
     *
     * @param context 上下文
     * @return 屏幕高度
     */
    fun getScreenHeight(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.heightPixels
    }

    /**
     * 获取 Assets 文件的内容
     *
     * @param context  上下文
     * @param fileName 文件名称
     * @return 文件内容
     */
    fun getAssetsFileContent(context: Context, fileName: String?): String {
        try {
            context.assets.open(fileName!!).use { `is` ->
                val size = `is`.available()

                // Read the entire asset into a local byte buffer.
                val buffer = ByteArray(size)
                return if (`is`.read(buffer) <= 0) {
                    ""
                } else String(buffer, Charset.forName("UTF-8"))
                // Convert the buffer into a string.
            }
        } catch (e: Exception) {
            d(TAG, "getAssetsFileContent", e)
        }
        return ""
    }

    /**
     * 判断应用是否在栈顶
     *
     * @param context 上下文
     * @return
     */
    fun isAppOnForeground(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        try {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasksInfo = activityManager.getRunningTasks(1)
            if (!tasksInfo.isEmpty() && context.packageName == tasksInfo[0].topActivity!!.packageName) {
                return true
            }
        } catch (e: SecurityException) {
            d(TAG, "isAppOnForeground", e)
        }
        return false
    }

    /**
     * 打开外部app
     *
     * @param context 上下文
     */
    fun openLinkApp(context: Context, packageName: String, activityName: String, data: Bundle?) {
        if (isEmpty(packageName)) {
            return
        }
        val mIntent = Intent()
        if (data != null) {
            mIntent.putExtras(data)
        }
        try {
            if (isEmpty(activityName)) {
                val packageManager = context.packageManager
                val intent =
                    packageManager.getLaunchIntentForPackage(packageName!!) //com.xx.xx是我们获取到的包名
                if (intent != null) {
                    context.startActivity(intent)
                } else {
//					ToastUtil.show(context, "跳转发生错误", false);
                    d(TAG, "跳转发生错误")
                }
            } else {
                val comp = ComponentName(packageName, activityName)
                mIntent.component = comp
                mIntent.action = "android.intent.action.VIEW"
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(mIntent)
            }
        } catch (e: Exception) {
//			ToastUtil.show(context, "跳转发生错误", false);
            e(TAG, e.message!!, e)
        }
    }

    /**
     * 读取 Application 的 MateData
     *
     * @param context 上下文
     * @return MateData
     */
    fun getMetaDataFromApplication(context: Context): Bundle? {
        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            return appInfo.metaData
        } catch (e: PackageManager.NameNotFoundException) {
            e(TAG, "AppUtils#getMetaDataFromApplication- ", e)
        }
        return null
    }

    /**
     * 读取 Activity 的 MateData
     *
     * @param activity 要读取 MateData 的 Activity
     * @return MateData
     */
    fun getMetaDataFromActivity(activity: Activity): Bundle? {
        val info: ActivityInfo
        try {
            info = activity.packageManager.getActivityInfo(
                activity.componentName,
                PackageManager.GET_META_DATA
            )
            return info.metaData
        } catch (e: PackageManager.NameNotFoundException) {
            e(TAG, "AppUtils#getMetaDataFromActivity- ", e)
        }
        return null
    }

    /**
     * 读取 Service 的 MateData
     *
     * @param service
     * @param serviceClass 要读取 MateData 的 Service 类
     * @return MateData
     */
    fun getMetaDataFromService(service: Service, serviceClass: Class<out Service?>?): Bundle? {
        try {
            val cn = ComponentName(service, serviceClass!!)
            val info = service.packageManager.getServiceInfo(cn, PackageManager.GET_META_DATA)
            return info.metaData
        } catch (e: PackageManager.NameNotFoundException) {
            e(TAG, "AppUtils#getMetaDataFromService- ", e)
        }
        return null
    }

    /**
     * 读取 BroadcastReceiver 的 MateData
     *
     * @param context       上下文
     * @param receiverClass 要读取 MateData 的 BroadcastReceiver 类
     */
    fun getMetaDataFromBroadCast(
        context: Context,
        receiverClass: Class<out BroadcastReceiver?>?
    ): Bundle? {
        try {
            val cn = ComponentName(context, receiverClass!!)
            val info = context.packageManager.getReceiverInfo(cn, PackageManager.GET_META_DATA)
            return info.metaData
        } catch (e: PackageManager.NameNotFoundException) {
            e(TAG, "AppUtils#getMetaDataFromBroadCast- ", e)
        }
        return null
    }

    /**
     * 重启应用
     * @param context 上下文
     */
    fun restartApplication(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }
}