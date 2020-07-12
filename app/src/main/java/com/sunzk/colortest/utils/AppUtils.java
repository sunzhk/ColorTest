package com.sunzk.colortest.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class AppUtils {

	private static final String TAG = "AppUtils";
	
	/**
	 * 获取屏幕大小
	 *
	 * @param context
	 * @return
	 */
	public static int[] getScreenSize(Context context) {
		return new int[2];
	}

	public static int getScreenWidth(Context context) {
		if (AppUtils.isAllScreenDevice(context)) {
			return getScreenRealWidth(context);
		} else {
			return context.getResources().getDisplayMetrics().widthPixels;
		}
	}

	private static Point[] sRealSizes = new Point[2];
	private static int getScreenRealWidth(Context context) {
		int orientation = context.getResources().getConfiguration().orientation;
		orientation = orientation == 1 ? 0 : 1;
		if (sRealSizes[orientation] == null) {
			WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = windowManager.getDefaultDisplay();
			Point point = new Point();
			display.getRealSize(point);
			sRealSizes[orientation] = point;
		}
		return sRealSizes[orientation].x;
	}

	public static boolean isAllScreenDevice(Context context) {
		if (Build.VERSION.SDK_INT < 21) {
			return false;
		} else {
			WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = windowManager.getDefaultDisplay();
			Point point = new Point();
			display.getRealSize(point);
			float width;
			float height;
			if (point.x < point.y) {
				width = point.x;
				height = point.y;
			} else {
				width = point.y;
				height = point.x;
			}
			if (height / width >= 1.97f) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 内部升级用，1,2,3,4 之类的整数，随版本的发布逐步增大
	 * 获取版本号
	 *
	 * @return 当前应用的版本号
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return 0;
	}

	/**
	 * 给用户显示用，比如1.1.1，2.1 等,字符窜
	 * 获取版本名
	 *
	 * @return 当前应用的版本名
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			Log.e(TAG, "getVersionName", e);
			return "";
		}
	}


}
