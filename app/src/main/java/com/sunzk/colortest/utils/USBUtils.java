package com.sunzk.colortest.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * USB 工具类，主要用于U盘状态监听以及U盘读写
 * Created by sunzhk on 2019/1/22.
 */
public class USBUtils {

	private static final String TAG = "USBUtils";

	private static ArrayList<OnUSBStateChangeListener> listeners = new ArrayList<>();

	public static void register(OnUSBStateChangeListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public static void unregister(OnUSBStateChangeListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}

	public static String getUSBPath() {
		String path = USBBroadcastReceiver.getUsbPath();
		if (!StringUtils.isBlank(path)) {
			return path;
		}
		List<String> usbList = getAllExternalSdcardPath();
		if (!usbList.isEmpty()) {
			return usbList.get(0);
		}
		return null;
	}

	public static List<String> getAllExternalSdcardPath() {
		List<String> pathList = new ArrayList<>();
		InputStreamReader isr = null;
		try {
			// 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			isr = new InputStreamReader(proc.getInputStream());
			String line;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				// 将常见的linux分区过滤掉
				if (line.contains("proc") || line.contains("tmpfs") || line.contains("media") || line.contains("asec") || line.contains("secure") || line.contains("system") || line.contains("cache")
						|| line.contains("sys") || line.contains("data") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("misc") || line.contains("obb")) {
					continue;
				}

				// 下面这些分区是我们需要的
				if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs"))){
					// 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
					String items[] = line.split(" ");
					if (items != null && items.length > 1){
						String path = items[1].toLowerCase(Locale.getDefault());
						// 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
						if (path != null && !pathList.contains(path) && path.contains("sd"))
							pathList.add(items[1]);
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return pathList;
	}

	public static class USBBroadcastReceiver extends BroadcastReceiver {

		private static String usbPath;

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean usbState = false;
			String callBackPath = null;
			if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {//U盘插入
				try {
					String path = intent.getDataString();
					usbPath = path.split("file://")[1];//U盘路径
				} catch (Exception e) {
					usbPath = null;
				}
				callBackPath = USBUtils.getUSBPath();
				usbState = !StringUtils.isBlank(callBackPath);
			}else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {//U盘拔出
				usbPath = null;
			}
			Logger.d(TAG, "USBBroadcastReceiver#onReceive- " + usbState + " , " + callBackPath);
			callOnUSBStateChangeListener(usbState, callBackPath);
		}

		public static String getUsbPath() {
			return usbPath;
		}

		private void callOnUSBStateChangeListener(boolean state, String usbPath) {
			for (OnUSBStateChangeListener listener : listeners) {
				listener.onStateChange(state, usbPath);
			}
		}
	}

	public static interface OnUSBStateChangeListener {

		void onStateChange(boolean isUSBReady, String usbPath);

	}

}
