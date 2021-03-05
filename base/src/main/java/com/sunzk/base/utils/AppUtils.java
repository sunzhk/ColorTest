package com.sunzk.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;

import java.io.InputStream;
import java.util.List;

import androidx.annotation.Nullable;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 应用工具类，包含一些Android相关功能的工具
 * Created by sunzhk on 2018/4/8.
 */

public class AppUtils {

	private static final String TAG = "AppUtils";

	private static Boolean isDebug = null;//是否是debug模式

	private AppUtils() {
	}

	/**
	 * 初始化，需要在 Application 中完成
	 *
	 * @param applicationContext 上下文
	 */
	public static void init(Context applicationContext) {
		syncIsDebug(applicationContext);
	}

	/**
	 * 是否是debug模式
	 */
	public static boolean isDebug() {
		return isDebug != null && isDebug;
	}

	/**
	 * 设置日志是否启用
	 */
	public static void setLogEnable(boolean enable){
		isDebug = enable;
	}

	/**
	 * 判断当前是否是在主线程
	 *
	 * @return 如果在主线程则返回true，否则返回false
	 */
	public static boolean isInMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	/**
	 * 判断目标线程是否是主线程
	 *
	 * @param thread 目标线程
	 * @return 如果目标线程是主线程则返回true，否则返回false
	 */
	public static boolean isMainThread(Thread thread) {
		return thread == Looper.getMainLooper().getThread();
	}

	/**
	 * 初始化 debug 状态。需要在 Application 中完成
	 *
	 * @param context 上下文
	 */
	private static void syncIsDebug(Context context) {
		if (isDebug == null) {
			isDebug = context.getApplicationInfo() != null && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		}
	}

	/**
	 * 获取版本号，1,2,3,4 之类的整数，随版本的发布逐步增大
	 *
	 * @param context 上下文
	 * @return 当前应用的版本号。获取失败则返回 -1
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			Logger.d(TAG, "getVersionCode", e);
			return -1;
		}
	}

	/**
	 * 获取版本名称，比如1.1.1，2.1 等,字符窜
	 *
	 * @param context 上下文
	 * @return 当前应用的版本名。获取失败则返回 null
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			Logger.d(TAG, "getVersionName", e);
			return null;
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
	public static void writeSetting(String category, String key, String value, Context context) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(category, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString(key, value);
		editor.apply();
	}

	/**
	 * 读SharedPreferences，适合读单条数据
	 *
	 * @param category SharedPreferences名称
	 * @param key      数据的Key
	 * @param context  上下文
	 * @return 数据的值
	 */
	public static String readSetting(String category, String key, Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(category, Activity.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");
	}

	/**
	 * 获取总的接受字节数，包含Mobile和WiFi等
	 *
	 * @param context 上下文
	 * @return 总的接受字节数。如果失败，则返回 -1
	 */
	@SuppressLint("WrongConstant")
	public static long getUidRxBytes(Context context) {
		PackageManager pm = context.getPackageManager();
		ApplicationInfo ai = null;
		try {
			ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			Logger.d(TAG, "getUidRxBytes", e);
		}
		if (ai == null) {
			return -1;
		}
		return TrafficStats.getUidRxBytes(ai.uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
	}

	/**
	 * 获取屏幕宽度
	 *
	 * @param context 上下文
	 * @return 屏幕宽度
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		return displayMetrics.widthPixels;
	}

	/**
	 * 获得屏幕高度
	 *
	 * @param context 上下文
	 * @return 屏幕高度
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		return displayMetrics.heightPixels;
	}

	/**
	 * 获取 Assets 文件的内容
	 *
	 * @param context  上下文
	 * @param fileName 文件名称
	 * @return 文件内容
	 */
	public static String getAssetsFileContent(Context context, String fileName) {
		try (InputStream is = context.getAssets().open(fileName)) {
			int size = is.available();

			// Read the entire asset into a local byte buffer.
			byte[] buffer = new byte[size];
			if (is.read(buffer) <= 0) {
				return "";
			}
			// Convert the buffer into a string.
			return new String(buffer, "UTF-8");
		} catch (Exception e) {
			Logger.d(TAG, "getAssetsFileContent", e);
		}
		return "";
	}

	/**
	 * 检测某ActivityUpdate是否在当前Task的栈顶
	 *
	 * @param activity 要判断的Activity
	 */
	public static boolean isTopActivy(Activity activity) {
		ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		String cmpNameTemp = null;

		if (null != runningTaskInfos) {
			cmpNameTemp = (runningTaskInfos.get(0).topActivity).toString();
			Logger.d(TAG, "当前栈顶:" + cmpNameTemp);
			Logger.d(TAG, "请求栈顶:" + activity.getComponentName());
		}

		if (null == cmpNameTemp) return false;
		return cmpNameTemp.equals(activity.getComponentName().toString());
	}


	/**
	 * 判断应用是否在栈顶
	 *
	 * @param context 上下文
	 * @return
	 */
	public static boolean isAppOnForeground(Context context) {
		if (context == null) {
			return false;
		}
		try {
			ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
			if (!tasksInfo.isEmpty() && context.getPackageName().equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		} catch (SecurityException e) {
			Logger.d(TAG, "isAppOnForeground", e);
		}
		return false;
	}

	/**
	 * 从Context获取真实的Activity
	 */
	@Nullable
	public static Activity getActivity(Context context) {
		if (context == null) {
			return null;
		}
		while (context instanceof ContextWrapper) {
			if (context instanceof Activity) {
				return (Activity) context;
			}
			context = ((ContextWrapper) context).getBaseContext();
		}
		return null;
	}

	/**
	 * 打开外部app
	 *
	 * @param context 上下文
	 */
	public static void openLinkApp(Context context, String packageName, String activityName, Bundle data) {
		if (StringUtils.isEmpty(packageName)) {
			return;
		}
		Intent mIntent = new Intent();
		if (data != null) {
			mIntent.putExtras(data);
		}
		try {
			if (StringUtils.isEmpty(activityName)) {
				PackageManager packageManager = context.getPackageManager();
				Intent intent = packageManager.getLaunchIntentForPackage(packageName);  //com.xx.xx是我们获取到的包名
				if (intent != null) {
					context.startActivity(intent);
				} else {
//					ToastUtil.show(context, "跳转发生错误", false);
					Logger.d(TAG, "跳转发生错误");
				}
			} else {
				ComponentName comp = new ComponentName(packageName, activityName);
				mIntent.setComponent(comp);
				mIntent.setAction("android.intent.action.VIEW");
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(mIntent);
			}
		} catch (Exception e) {
//			ToastUtil.show(context, "跳转发生错误", false);
			Logger.e(TAG, e.getMessage(), e);
		}
	}

	/**
	 * 读取 Application 的 MateData
	 *
	 * @param context 上下文
	 * @return MateData
	 */
	public static Bundle getMetaDataFromApplication(Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			return appInfo.metaData;
		} catch (PackageManager.NameNotFoundException e) {
			Logger.e(TAG, "AppUtils#getMetaDataFromApplication- ", e);
		}
		return null;
	}

	/**
	 * 读取 Activity 的 MateData
	 *
	 * @param activity 要读取 MateData 的 Activity
	 * @return MateData
	 */
	public static Bundle getMetaDataFromActivity(Activity activity) {
		ActivityInfo info;
		try {
			info = activity.getPackageManager().getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
			return info.metaData;
		} catch (PackageManager.NameNotFoundException e) {
			Logger.e(TAG, "AppUtils#getMetaDataFromActivity- ", e);
		}
		return null;
	}

	/**
	 * 读取 Service 的 MateData
	 *
	 * @param service
	 * @param serviceClass 要读取 MateData 的 Service 类
	 * @return MateData
	 */
	public static Bundle getMetaDataFromService(Service service, Class<? extends Service> serviceClass) {
		try {
			ComponentName cn = new ComponentName(service, serviceClass);
			ServiceInfo info = service.getPackageManager().getServiceInfo(cn, PackageManager.GET_META_DATA);
			return info.metaData;
		} catch (PackageManager.NameNotFoundException e) {
			Logger.e(TAG, "AppUtils#getMetaDataFromService- ", e);
		}
		return null;
	}

	/**
	 * 读取 BroadcastReceiver 的 MateData
	 *
	 * @param context       上下文
	 * @param receiverClass 要读取 MateData 的 BroadcastReceiver 类
	 */
	public static Bundle getMetaDataFromBroadCast(Context context, Class<? extends BroadcastReceiver> receiverClass) {
		try {
			ComponentName cn = new ComponentName(context, receiverClass);
			ActivityInfo info = context.getPackageManager().getReceiverInfo(cn, PackageManager.GET_META_DATA);
			return info.metaData;
		} catch (PackageManager.NameNotFoundException e) {
			Logger.e(TAG, "AppUtils#getMetaDataFromBroadCast- ", e);
		}
		return null;
	}

	/**
	 * 重启应用
	 * @param context 上下文
	 */
	public static void restartApplication(Context context) {
		final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}


}