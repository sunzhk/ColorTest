package com.sunzk.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.IntRange;

/**
 * 网络工具类
 * Created by sunzhk on 2018/4/8.
 */

public class NetWorkUtils {

	private static final String TAG = "NetWorkUtils";

	private static final int PING_DELAY_DEFAULT = 1000;
	private static final int PING_LOST_DEFAULT = 20;

	/**
	 * 判断网络是否有效.
	 *
	 * @return 如果网络连接正常则返回true。否则返回false
	 */
	public static boolean isNetworkConnected(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			return info != null && info.isConnected() && info.getState() == NetworkInfo.State.CONNECTED;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 像心跳服务器发送请求，判断网络是否连通
	 *
	 * @param url      心跳服务地址
	 * @param listener 回调
	 */
	public static void requestHeartbeat(final String url, final ResultListener listener){
		new Thread(){
			@Override
			public void run() {
				try {
					URL testUrl = new URL(url);
					HttpURLConnection connection = (HttpURLConnection) testUrl.openConnection();
					if(200 == connection.getResponseCode()){
						Logger.i(TAG, "requestHeartbeat success");
						callBack(listener, true);
					}else{
						Logger.i(TAG, "requestHeartbeat failure with error http status:"+connection.getResponseCode());
						callBack(listener, false);
					}
					connection.disconnect();
				} catch (IOException e) {
					Logger.e(TAG, "requestHeartbeat failure with exception", e);
					callBack(listener, false);
				}
			}
		}.start();
	}

	/**
	 * ping 目标地址 5 次，时间间隔 0.5 秒，延迟不能超过 200 毫秒，丢包率不能超过 10%
	 *
	 * @param ip       目标地址
	 * @param listener 回调
	 */
	public static void pingServer(String ip, final ResultListener listener) {
		pingServer(ip, 5, 0.5f, 200, 10, listener);
	}

	/**
	 * ping 目标地址
	 *
	 * @param ip            目标地址
	 * @param time          请求次数
	 * @param interval      间隔时间，单位：秒
	 * @param delayStandard 延迟标准，单位：毫秒
	 * @param lostStandard  丢包率标准，单位：百分比
	 * @param listener      回调
	 */
	public static void pingServer(String ip, int time, float interval, final int delayStandard, @IntRange(from = 0, to = 100) final int lostStandard, final ResultListener listener) {
		if (interval < 0.2f) {
			interval = 0.2f;
		}
		final Process process;
		try {
			process = Runtime.getRuntime().exec("ping -c " + time + " -i " + interval + " " + ip);
		} catch (IOException e) {
			Logger.e(TAG, "pingServer-exec", e);
			callBack(listener, false);
			return;
		}
		new Thread() {
			@Override
			public void run() {
				super.run();
				String temp;
				ArrayList<String> pingResultList = new ArrayList<>();
				BufferedReader errorReader = null;
				BufferedReader inputReader = null;
				try {
					errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					while ((temp = errorReader.readLine()) != null) {
						Logger.v(TAG, "pingServer-errorReader:" + temp);
					}
					inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					while ((temp = inputReader.readLine()) != null) {
						Logger.v(TAG, "pingServer-inputReader:" + temp);
						pingResultList.add(temp);
					}
					boolean isSuccess = checkPingResult(pingResultList);
					callBack(listener, isSuccess);
				} catch (IOException e) {
					Logger.e(TAG, "pingServer", e);
					callBack(listener, false);
				} finally {
					closeStream(errorReader);
					closeStream(inputReader);
				}
			}

			private boolean checkPingResult(ArrayList<String> pingResultList) {
				boolean singleResult = true;
				boolean finalResult = false;
				for (String line : pingResultList) {
					if (line.contains("time=") && line.contains("ms")) {//单次ping结果
						float pingDelay = getPingDelay(line);
						Logger.v(TAG, "getPingDelay:" + pingDelay);
						if (pingDelay > delayStandard) {
							singleResult = false;
						}
					} else if (line.contains("packet loss")) {//ping总结果
						int packageLoss = getPackageLoss(line);
						Logger.v(TAG, "packageLoss:" + packageLoss);
						if (packageLoss < lostStandard) {
							finalResult = true;
						}
					}
				}
				Logger.v(TAG, "checkPingResult-singleResult:" + singleResult + " finalResult:" + finalResult);
				return singleResult && finalResult;
			}

			private float getPingDelay(String line) {
				float result = delayStandard + 1f;
				if (!line.contains("time=")) {
					return result;
				}
				String[] params = line.split(" ");
				for (String param : params) {
					if (param.contains("time=")) {
						String delay = param.substring(5, param.length());
						try {
							result = Float.parseFloat(delay);
						} catch (Exception e) {
							Logger.e(TAG, "getPingDelay", e);
						}
					}
				}
				return result;
			}

			private int getPackageLoss(String finalResult) {
				int result = 101;
				if (!finalResult.contains("packet loss")) {
					return result;
				}
				String[] params = finalResult.split(" ");
				for (String param : params) {
					if (param.contains("%")) {
						String loss = param.substring(0, param.length() - 1);
						try {
							result = Integer.parseInt(loss);
						} catch (Exception e) {
							Logger.e(TAG, "getPackageLoss", e);
						}
					}
				}

				return result;
			}

		}.start();
	}

	private static void closeStream(Closeable stream) {
		if (stream == null) {
			return;
		}
		try {
			stream.close();
		} catch (IOException e) {
			Logger.e(TAG, "closeStream", e);
		}
	}

	private static void callBack(ResultListener listener, boolean result) {
		if (listener != null) {
			listener.isSuccess(result);
		}
	}

	public interface ResultListener {
		void isSuccess(boolean isSuccessfully);
	}


	public interface NetworkListener {
		void isNetworkOk(boolean ok);
	}

}
