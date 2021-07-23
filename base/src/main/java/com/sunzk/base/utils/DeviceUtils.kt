package com.sunzk.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by zhangxs on 2018/1/19.
 */

public class DeviceUtils {

	private static final String TAG = "DeviceUtils";

	public static String getProperty(String key, String defaultValue) {
		String value = defaultValue;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
			value = (String) (get.invoke(c, key, "unknown"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return value;
		}
	}

	public static String getDeviceType() {
		return Build.MODEL;
	}

	public static String getVendor() {
		return Build.MANUFACTURER;
	}

	public static String getStbID() {
		return Build.SERIAL;
	}

	/**
	 * 不建议使用
	 *
	 * @return
	 */
	@Deprecated
	public static String getDeviceId() {
//		return Build.ID;
		String deviceId = getProperty("ro.serialno", "");
		try {
			return deviceId.substring(0, 32);
		} catch (Exception e) {
			return deviceId;
		}
	}

	public static String getDeviceVersion() {
		return Build.VERSION.RELEASE;
	}

	public static IpAndMac getStbIpAddress() {
		return getStbIpAddress(false);
	}

	public static IpAndMac getStbIpAddress(boolean useWifi) {
		IpAndMac iPandMac = getLocalMac("eth0", null);
		if (useWifi && StringUtils.isBlank(iPandMac.mac)) {
			//	使用wifi配置的话 当本地mac获取失败则取wifi的mac地址
			iPandMac = getLocalMac("wlan0", iPandMac);
		}

		iPandMac = getLocalIp("eth0", iPandMac);

		if (StringUtils.isBlank(iPandMac.ip)) {
			//	本地ip如果获取失败 则获取wifi的ip地址
			iPandMac = getLocalIp("wlan0", iPandMac);
		}
		return iPandMac;
	}

	private static IpAndMac getLocalMac(String name, IpAndMac ipAndMac) {
		Log.d(TAG, "getLocalIp name:" + name);
		if (ipAndMac == null) {
			ipAndMac = new IpAndMac();
		}

		try {
			Log.d(TAG, "all interface:" + NetworkInterface.getNetworkInterfaces().toString());
			ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ni : nilist) {
				Log.d(TAG, "network inferface:" + ni.toString());
				if (ni.getName().equals(name)) {
					ipAndMac.mac = byte2hex(ni.getHardwareAddress());
					Log.d(TAG, " mac:" + ipAndMac.mac);
					break;
				}
			}
		} catch (SocketException ex) {
			Log.e("localip", ex.toString());
		}

		return ipAndMac;
	}

	private static IpAndMac getLocalIp(String name, IpAndMac ipAndMac) {
		Log.d(TAG, "getLocalIpAddress name:" + name);
		if (ipAndMac == null) {
			ipAndMac = new IpAndMac();
		}

		try {
			Log.d(TAG, "all interface:" + NetworkInterface.getNetworkInterfaces().toString());
			ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ni : nilist) {
				Log.d(TAG, "network inferface:" + ni.toString());
				if (ni.getName().equals(name)) {
					ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
					for (InetAddress address : ialist) {
						if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
							ipAndMac.ip = address.getHostAddress();
							Log.d(TAG, " ip:" + ipAndMac.ip);
							break;
						}
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("localip", ex.toString());
		}
		return ipAndMac;
	}


	private static IpAndMac getLocalIpAddress(String name) {
		Log.d(TAG, "getLocalIpAddress name:" + name);
		IpAndMac iPandMac = new IpAndMac();
		try {
			String ipv4;
			Log.d(TAG, "all interface:" + NetworkInterface.getNetworkInterfaces().toString());
			ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ni : nilist) {
				Log.d(TAG, "network inferface:" + ni.toString());
				if (ni.getName().equals(name)) {
					ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
					iPandMac.mac = byte2hex(ni.getHardwareAddress());
					for (InetAddress address : ialist) {
						if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
							iPandMac.ip = address.getHostAddress();
							Log.d(TAG, "mac:" + iPandMac.mac + " ip:" + iPandMac.ip);
							return iPandMac;
						}
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("localip", ex.toString());
		}
		return iPandMac;
	}

	public static boolean isNetworkIsOk() {
		IpAndMac iPandMac = getStbIpAddress();
		if (iPandMac == null) {
			return false;
		}
		if ("0:0:0:0".equals(iPandMac.ip)) {
			return false;
		}
		return true;
	}

	public static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1) hs = hs.append("0").append(stmp);
			else {
				hs = hs.append(stmp);
			}
			hs.append(":");
		}
		return String.valueOf(hs.substring(0, hs.length() - 1));
	}


	/**
	 * 获得IP地址，分为两种情况，一是wifi下，二是移动网络下，得到的ip地址是不一样的
	 */
	public static IpAndMac getIPAddress(Context context) {
		IpAndMac iPandMac = new IpAndMac();
		NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE || info.getType() == ConnectivityManager.TYPE_ETHERNET) {//当前使用2G/3G/4G网络 ，有线网络
				try {
					//Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
					for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
								iPandMac.mac = byte2hex(intf.getHardwareAddress());
								iPandMac.ip = inetAddress.getHostAddress();
								return iPandMac;
							}
						}
					}
				} catch (SocketException e) {
					e.printStackTrace();
				}

			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				//调用方法将int转换为地址字符串
				String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
				iPandMac.mac = wifiInfo.getMacAddress();
				iPandMac.ip = ipAddress;
				return iPandMac;

			}
		} else {
			//当前无网络连接,请在设置中打开网络
		}
		return iPandMac;
	}

	/**
	 * 将得到的int类型的IP转换为String类型
	 *
	 * @param ip
	 * @return
	 */
	public static String intIP2StringIP(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
	}

	public static class IpAndMac implements Serializable {
		public String ip;
		public String mac;
	}

}