package com.sunzk.base.utils;

import android.os.Looper;
import android.util.Printer;

/**
 * 用于检查主线程中，超时
 * @author yongjie created on 2020/3/11.
 */
public class BlockDetectByPrinter {
	private static final String TAG = "BlockDetectByPrinter";
	//方法耗时的卡口，单位毫秒
	private static final long TIME_BLOCK = 50L;
	
	public static void start() {
		BlockDetectByPrinter.start(TIME_BLOCK);
	}
	
	public static void start(long timeBlock) {
		Looper.getMainLooper().setMessageLogging(new Printer() {
			//分发和处理消息开始前的log
			private static final String START = ">>>>> Dispatching";
			//分发和处理消息结束后的log
			private static final String END = "<<<<< Finished";

			@Override
			public void println(String x) {
				if (x.startsWith(START)) {
					//开始计时
					LogMonitor.getInstance().startMonitor(timeBlock);
				}
				if (x.startsWith(END)) {
					//结束计时，并计算出方法执行时间
					LogMonitor.getInstance().removeMonitor();
				}
			}
		});
	}
}
