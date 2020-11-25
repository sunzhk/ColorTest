package com.sunzk.colortest.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

import androidx.annotation.NonNull;

/**
 * 全局获取Application<br>
 * 使用反射调用隐藏API，方便但不安全，且在Android 9 之后开始限制 @hide api，不建议广泛使用
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class Applications {

	private static String TAG = "Applications";

	@NonNull
	public static Application context() {
		return CURRENT;
	}

	@SuppressLint("StaticFieldLeak")
	private static final Application CURRENT;

	static {
		try {
			Object activityThread = getActivityThread();
			Object app = activityThread.getClass().getMethod("getApplication").invoke(activityThread);
			CURRENT = (Application) app;
		} catch (Throwable e) {
			throw new IllegalStateException("Can not access Application context by magic code, boom!", e);
		}
	}

	private static Object getActivityThread() {
		Object activityThread = null;
		try {
			@SuppressLint("PrivateApi") Method method = Class.forName("android.app.ActivityThread").getMethod("currentActivityThread");
			method.setAccessible(true);
			activityThread = method.invoke(null);
		} catch (final Exception e) {
			Log.w(TAG, e);
		}
		return activityThread;
	}
}