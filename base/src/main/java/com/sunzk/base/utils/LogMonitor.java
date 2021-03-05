package com.sunzk.base.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * @author yongjie created on 2020/3/11.
 */
public final class LogMonitor {
	private static final String TAG = "LogMonitor";
	private static LogMonitor sInstance = new LogMonitor();
	private Handler mIoHandler;

	private LogMonitor() {
		HandlerThread logThread = new HandlerThread("log");
		logThread.start();
		mIoHandler = new Handler(logThread.getLooper());
	}

	private static Runnable mLogRunnable = new Runnable() {
		@Override
		public void run() {
			//打印出执行的耗时方法的栈消息
			StringBuilder sb = new StringBuilder();
			StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
			for (StackTraceElement s : stackTrace) {
				sb.append(s.toString());
				sb.append("\n");
			}
			Logger.w(TAG, sb.toString());
		}
	};

	public static LogMonitor getInstance() {
		return sInstance;
	}

	/**
	 * 开始计时
	 */
	public void startMonitor(long timeBlock) {
		mIoHandler.postDelayed(mLogRunnable, timeBlock);
	}

	/**
	 * 停止计时
	 */
	public void removeMonitor() {
		mIoHandler.removeCallbacks(mLogRunnable);
	}

}
