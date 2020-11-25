package com.sunzk.colortest.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sunzhk on 2019/1/22.
 */

public class USBBroadcastReceiver extends BroadcastReceiver {
	
	private static String usbPath;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {//U盘插入
			try {
				String path = intent.getDataString();
				usbPath = path.split("file://")[1];//U盘路径
			} catch (Exception e) {
				usbPath = null;
			}
		}else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {//U盘拔出
			usbPath = null;
		}
	}
	
	public static String getUsbPath() {
		return usbPath;
	}
}
