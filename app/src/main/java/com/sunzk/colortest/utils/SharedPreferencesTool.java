package com.sunzk.colortest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * SharedPreferences 工具类
 */
public class SharedPreferencesTool {
	private SharedPreferences sharedPreferences;

	/**
	 * 初始化创建或打开文件
	 */
	public SharedPreferencesTool(String FILENAME, Context context) {
		sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
	}

//	静态工具开始

	/**
	 * 保存数据，数据类型仅限 boolean，int，long，float，String，String[]
	 *
	 * @param context               上下文
	 * @param sharedPreferencesName SharedPreferences名称
	 * @param key                   数据名称
	 * @param value                 数据值
	 * @param <T>                   数据类型，仅限 Boolean，Int，Long，Float，String，String[]
	 * @return 保存成功则返回 true，否则返回 false
	 */
	public static <T> boolean saveData(Context context, String sharedPreferencesName, String key, T value) {
		try {
			SharedPreferences sp = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
			if (value instanceof Boolean) {
				return sp.edit().putBoolean(key, (Boolean) value).commit();
			} else if (value instanceof Integer) {
				return sp.edit().putInt(key, (Integer) value).commit();
			} else if (value instanceof Long) {
				return sp.edit().putLong(key, (Long) value).commit();
			} else if (value instanceof Float) {
				return sp.edit().putFloat(key, (Float) value).commit();
			} else if (value instanceof String) {
				return sp.edit().putString(key, (String) value).commit();
			} else if (value instanceof String[]) {
				HashSet<String> stringSet = new HashSet<>();
				Collections.addAll(stringSet, (String[]) value);
				return sp.edit().putStringSet(key, stringSet).commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取一条 boolean 数据
	 *
	 * @param context               上下文
	 * @param sharedPreferencesName SharedPreferences名称
	 * @param key                   数据名称
	 * @param defValue              默认值
	 * @return 返回数据的值。如果获取失败，则返回默认值
	 */
	public static boolean getData(Context context, String sharedPreferencesName, String key, boolean defValue) {
		SharedPreferences sp = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		return sp.getBoolean(key, defValue);
	}

	/**
	 * 获取一条 int 数据
	 *
	 * @param context               上下文
	 * @param sharedPreferencesName SharedPreferences名称
	 * @param key                   数据名称
	 * @param defValue              默认值
	 * @return 返回数据的值。如果获取失败，则返回默认值
	 */
	public static int getData(Context context, String sharedPreferencesName, String key, int defValue) {
		SharedPreferences sp = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		return sp.getInt(key, defValue);
	}

	/**
	 * 获取一条 long 数据
	 *
	 * @param context               上下文
	 * @param sharedPreferencesName SharedPreferences名称
	 * @param key                   数据名称
	 * @param defValue              默认值
	 * @return 返回数据的值。如果获取失败，则返回默认值
	 */
	public static long getData(Context context, String sharedPreferencesName, String key, long defValue) {
		SharedPreferences sp = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		return sp.getLong(key, defValue);
	}

	/**
	 * 获取一条 float 数据
	 *
	 * @param context               上下文
	 * @param sharedPreferencesName SharedPreferences名称
	 * @param key                   数据名称
	 * @param defValue              默认值
	 * @return 返回数据的值。如果获取失败，则返回默认值
	 */
	public static float getData(Context context, String sharedPreferencesName, String key, float defValue) {
		SharedPreferences sp = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		return sp.getFloat(key, defValue);
	}

	/**
	 * 获取一条 String 数据
	 *
	 * @param context               上下文
	 * @param sharedPreferencesName SharedPreferences名称
	 * @param key                   数据名称
	 * @param defValue              默认值
	 * @return 返回数据的值。如果获取失败，则返回默认值
	 */
	public static String getData(Context context, String sharedPreferencesName, String key, String defValue) {
		SharedPreferences sp = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		return sp.getString(key, defValue);
	}

	/**
	 * 获取一条 Set<String> 数据
	 *
	 * @param context               上下文
	 * @param sharedPreferencesName SharedPreferences名称
	 * @param key                   数据名称
	 * @param defValue              默认值
	 * @return 返回数据的值。如果获取失败，则返回默认值
	 */
	public static Set<String> getData(Context context, String sharedPreferencesName, String key, Set<String> defValue) {
		SharedPreferences sp = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		return sp.getStringSet(key, defValue);
	}
//	静态工具结束，同步保存开始

	/**
	 * 同步保存保存一个 boolean 数据，数据会立即写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataSync(String key, boolean value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 同步保存保存一个 int 数据，数据会立即写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataSync(String key, int value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 同步保存保存一个 long 数据，数据会立即写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataSync(String key, long value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	/**
	 * 同步保存保存一个 float 数据，数据会立即写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataSync(String key, float value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	/**
	 * 同步保存保存一个 String 数据，数据会立即写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataSync(String key, String value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 同步保存保存一个 Set<String> 数据，数据会立即写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataSync(String key, Set<String> value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putStringSet(key, value);
		editor.commit();
	}

//同步保存结束，异步保存开始

	/**
	 * 异步保存保存一个 boolean 数据，数据会先保存在内存中，统一写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataAsync(String key, boolean value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 异步保存保存一个 int 数据，数据会先保存在内存中，统一写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataAsync(String key, int value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 异步保存保存一个 long 数据，数据会先保存在内存中，统一写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataAsync(String key, long value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	/**
	 * 异步保存保存一个 float 数据，数据会先保存在内存中，统一写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataAsync(String key, float value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	/**
	 * 异步保存保存一个 String 数据，数据会先保存在内存中，统一写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataAsync(String key, String value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 异步保存保存一个 Set<String> 数据，数据会先保存在内存中，统一写入磁盘
	 *
	 * @param key   数据名称
	 * @param value 数据的值
	 */
	public void saveDataAsync(String key, Set<String> value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putStringSet(key, value);
		editor.commit();
	}

//异步保存结束，读取数据开始

	/**
	 * 读取一条 boolean 数据
	 *
	 * @param key      数据名称
	 * @param defValue 默认值，会在读取失败时返回
	 * @return boolean 数据
	 */
	public boolean readData(String key, boolean defValue) {
		return sharedPreferences.getBoolean(key, defValue);
	}

	/**
	 * 读取一条 int 数据
	 *
	 * @param key      数据名称
	 * @param defValue 默认值，会在读取失败时返回
	 * @return int 数据
	 */
	public int readData(String key, int defValue) {
		return sharedPreferences.getInt(key, defValue);
	}

	/**
	 * 读取一条 long 数据
	 *
	 * @param key      数据名称
	 * @param defValue 默认值，会在读取失败时返回
	 * @return long 数据
	 */
	public long readData(String key, long defValue) {
		return sharedPreferences.getLong(key, defValue);
	}

	/**
	 * 读取一条 float 数据
	 *
	 * @param key      数据名称
	 * @param defValue 默认值，会在读取失败时返回
	 * @return float 数据
	 */
	public float readData(String key, float defValue) {
		return sharedPreferences.getFloat(key, defValue);
	}

	/**
	 * 读取一条 String 数据
	 *
	 * @param key      数据名称
	 * @param defValue 默认值，会在读取失败时返回
	 * @return String 数据
	 */
	public String readData(String key, String defValue) {
		return sharedPreferences.getString(key, defValue);
	}

	/**
	 * 读取一条 Set<String> 数据
	 *
	 * @param key 数据名称
	 * @return Set<String> 数据，读取失败时返回null。
	 */
	public Set<String> readData(String key) {
		return sharedPreferences.getStringSet(key, null);
	}

	/**
	 * 清空数据
	 */
	public void clearData() {
		//先拿到SharedPreferences的编辑器
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

}
