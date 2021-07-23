package com.sunzk.base.utils;

import android.content.Context;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * log 封装类，用于扩充日志内容，限制日志打印条件。<br>
 * d、v级别日志仅在debug模式下输出到logcat<br>
 * 在插入U盘，并且U盘根目录下存放了校验文件的情况下，会向U盘中输出<br>
 * 日志会存放在U盘根目录下<br>
 * 使用命令行向U盘中写入日志文件，但只能获得应用本身的日志<br>
 * todo 多进程时可能会出现冲突
 */
public class Logger {

	private static final String TAG = "Logger";

	private static String authorizationFileName = "IPTV_Log_Authorization_File";

	private static SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

	private static boolean printUSBLog = false;
	
	/**
	 * 强制打印日志，部分特殊情况下无法获取日志时使用
	 */
	private static boolean forcePrint = false;

	/**
	 * 组合键：开启日志
	 */
	private static final int[] KEY_COMBINATION_OPEN = new int[]{
			KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,
			KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,
			KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_UP,
			KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN};
	/**
	 * 组合键：关闭日志
	 */
	private static final int[] KEY_COMBINATION_CLOSE = new int[]{
			KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,
			KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_MENU,
			KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_LEFT,
			KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_RIGHT};

	//静态设置USB监听
	static {
		USBUtils.OnUSBStateChangeListener listener = (isUSBReady, usbPath) -> {
			printUSBLog = isUSBReady;
			//先确认U盘路径
			if (!isUSBReady || StringUtils.isBlank(usbPath)) {
				printUSBLog = false;
			} else {// 再判断一下U盘中有没有授权文件，没有的话就不往U盘写
				File authFile = new File(usbPath, authorizationFileName);
				if (!authFile.exists()) {
//					Context application = Applications.context();
//					if(application!=null){
//						ToastUtil.show(application, "检测到U盘，未找到目标文件", true);
//					}
					printUSBLog = false;
				}
			}
			if (printUSBLog) {
				MainHandler.getInstance().post(() -> {
					Context application = Applications.context();
					if(application!=null){
						Toast.makeText(application, "检测到U盘，开始记录日志", Toast.LENGTH_SHORT).show();
					}

				});
				new Thread() {
					@Override
					public void run() {
						try {
							//先删除已有的日志文件，否则命令行执行时不会覆盖
							String logFileName = usbPath + "/logcat.log";
							File logFile = new File(logFileName);
							if (logFile.exists()) {
								logFile.delete();
							}
							Runtime.getRuntime().exec("logcat -f " + logFileName);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
			} else if (!isUSBReady) {
				MainHandler.getInstance().post(new Runnable() {
					@Override
					public void run() {
//						Context application = Applications.context();
//						if(application!=null){
//							ToastUtil.show(application, "检测到U盘弹出", true);
//						}
					}
				});
			}
		};
		USBUtils.register(listener);
		//如果APK启动前U盘就已经插上了，就需要先判断一下当前的USB状态，直接写
		if (!StringUtils.isBlank(USBUtils.getUSBPath())) {
			listener.onStateChange(true, USBUtils.getUSBPath());
		}
	}

	/**
	 * 在debug模式下或远程调试模式下打印一条 {@link Log#VERBOSE} 日志
	 *
	 * @param tag 用于识别日志消息的来源，建议使用当前类的类名
	 * @param msg 想要打印的日志
	 */
	public static void v(String tag, String msg) {
		if (!needPrintLog()) {
			return;
		}
		Log.v(tag, spliceLogTime(msg));
	}

	public static void v(final String tag, String msg, Object... data) {
		if (!needPrintLog()) {
			return;
		}
		Log.v(tag, spliceLogTime(splicingLogMsg(tag, msg, data)));
	}

	/**
	 * 在debug模式下或远程调试模式下打印一条 {@link Log#DEBUG} 日志
	 *
	 * @param tag 用于识别日志消息的来源，建议使用当前类的类名
	 * @param msg 想要打印的日志
	 */
	public static void d(String tag, String msg) {
		if (!needPrintLog()) {
			return;
		}
		Log.d(tag, spliceLogTime(msg));
	}

	/**
	 * 在debug模式下或远程调试模式下打印一条 {@link Log#DEBUG} 日志
	 *
	 * @param tag 用于识别日志消息的来源，建议使用当前类的类名
	 * @param msg 想要打印的日志
	 */
	public static void d(String tag, String msg, Throwable t) {
		if (!needPrintLog()) {
			return;
		}
		Log.d(tag, spliceLogTime(msg), t);
	}

	/**
	 * 在debug模式下或远程调试模式下打印一条 {@link Log#DEBUG} 日志
	 *
	 * @param tag 用于识别日志消息的来源，建议使用当前类的类名
	 * @param msg 想要打印的日志
	 * @param throwable   想要一同打印的异常
	 */
	public static void d(final String tag, String msg, final Throwable throwable, Object... data) {
		if (!needPrintLog()) {
			return;
		}
		Log.d(tag, spliceLogTime(splicingLogMsg(tag, msg, throwable, data)), throwable);
	}

	public static void d(final String tag, String msg, Object... data) {
		if (!needPrintLog()) {
			return;
		}
		Log.d(tag, spliceLogTime(splicingLogMsg(tag, msg, data)));
	}

	/**
	 * 在debug模式下或远程调试模式下打印一条 {@link Log#INFO} 日志
	 *
	 * @param tag 用于识别日志消息的来源，建议使用当前类的类名
	 * @param msg 想要打印的日志
	 */
	public static void i(String tag, String msg) {
		Log.i(tag, spliceLogTime(msg));
	}

	public static void i(final String tag, String msg, Object... data) {
		Log.i(tag, spliceLogTime(splicingLogMsg(tag, msg, data)));
	}

	/**
	 * 在debug模式下或远程调试模式下打印一条 {@link Log#WARN} 日志
	 *
	 * @param tag 用于识别日志消息的来源，建议使用当前类的类名
	 * @param msg 想要打印的日志
	 */
	public static void w(String tag, String msg) {
		Log.w(tag, spliceLogTime(msg));
	}

	public static void w(final String tag, String msg, Throwable throwable) {
		Log.w(tag, spliceLogTime(msg), throwable);
	}

	public static void w(final String tag, String msg, Object... data) {
		Log.w(tag, spliceLogTime(splicingLogMsg(tag, msg, data)));
	}

	/**
	 * 在debug模式下或远程调试模式下打印一条 {@link Log#ERROR} 日志
	 *
	 * @param tag 用于识别日志消息的来源，建议使用当前类的类名
	 * @param msg 想要打印的日志
	 */
	public static void e(String tag, String msg) {
		Log.e(tag, spliceLogTime(msg));
	}

	/**
	 * 在debug模式下或远程调试模式下打印一条 {@link Log#ERROR} 日志
	 *
	 * @param tag 用于识别日志消息的来源，建议使用当前类的类名
	 * @param msg 想要打印的日志
	 * @param t   想要一同打印的异常
	 */
	public static void e(String tag, String msg, Throwable t) {
		if (t == null) {
			Log.e(tag, spliceLogTime(msg));
		} else {
			Log.e(tag, spliceLogTime(msg), t);
		}
	}
	/**
	 * 在debug模式下或远程调试模式下打印一条 {@link Log#ERROR} 日志
	 *
	 * @param tag       用于识别日志消息的来源，建议使用当前类的类名
	 * @param msg       想要打印的日志
	 * @param data      想要一同打印的数据
	 */
	public static void e(final String tag, String msg, Object... data) {
		Log.e(tag, spliceLogTime(splicingLogMsg(tag, msg, data)));
	}

	/**
	 * 在debug模式下或远程调试模式下打印一条 {@link Log#ERROR} 日志
	 *
	 * @param tag       用于识别日志消息的来源，建议使用当前类的类名
	 * @param msg       想要打印的日志
	 * @param throwable 想要一同打印的异常
	 * @param data      想要一同打印的数据
	 */
	public static void e(final String tag, String msg, Throwable throwable, Object... data) {
		Log.e(tag, spliceLogTime(splicingLogMsg(tag, msg, data)), throwable);
	}

	/* ---------------- 日志方法结束 ---------------- */

	private static boolean needPrintLog() {
		return AppUtils.isDebug() || printUSBLog;
	}

	private static String spliceLogTime(String msg) {
		return spliceLogTime(System.currentTimeMillis(), msg);
	}

	/**
	 * 使用logcat命令写日志文件时，是没有日志时间的，所以需要给每条日志的msg中增加时间信息
	 * @param time
	 * @param msg
	 * @return
	 */
	private static String spliceLogTime(long time, String msg) {
		if (printUSBLog) {
			String pid = String.valueOf(Process.myPid());
			String tid = String.valueOf(Thread.currentThread().getId());
			return String.format("%s\t%s-%s\t%s", logDateFormat.format(new Date(time)), pid, tid, msg);
		} else {
			return msg;
		}
	}


	private static String splicingLogMsg(String tag, String msg, final Object... data) {
		return splicingLogMsg(tag, msg, null, data);
	}

	private static String splicingLogMsg(String tag, String msg, Throwable throwable, final Object... data) {
		if (data == null || data.length == 0) {
			return msg;
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(msg);
		for (Object datum : data) {
			try {
				stringBuilder.append("\t");
				stringBuilder.append(datum == null ? "[数据丢失]" : datum);
			} catch (Exception e) {
				stringBuilder.append("[数据损坏]");
			}
		}
		return stringBuilder.toString();
	}

}