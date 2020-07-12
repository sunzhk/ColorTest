package com.sunzk.colortest.utils;

/**
 * 数字计算相关工具类
 * Created by sunzhk on 2018/4/17.
 */
public final class NumberUtils {

	private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	private NumberUtils() {}

	/**
	 * 将 byte 数组转换为16进制字符串
	 * @param bytes byte数组
	 * @return 16进制字符串
	 */
	public static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(HEX_DIGITS[(b & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * 将字符串转换为short类型
	 * @param value    需要转换的字符串
	 * @param defValue 默认值
	 * @return 成功时返回转换的结果，否则返回默认值
	 */
	public static short parse(String value, short defValue) {
		try {
			return Short.parseShort(value);
		} catch (Exception e) {
			return defValue;
		}
	}
	/**
	 * 将字符串转换为int类型
	 * @param value    需要转换的字符串
	 * @param defValue 默认值
	 * @return 成功时返回转换的结果，否则返回默认值
	 */
	public static int parse(String value, int defValue) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defValue;
		}
	}
	/**
	 * 将字符串转换为long类型
	 * @param value    需要转换的字符串
	 * @param defValue 默认值
	 * @return 成功时返回转换的结果，否则返回默认值
	 */
	public static long parse(String value, long defValue) {
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			return defValue;
		}
	}
	/**
	 * 将字符串转换为float类型
	 * @param value    需要转换的字符串
	 * @param defValue 默认值
	 * @return 成功时返回转换的结果，否则返回默认值
	 */
	public static float parse(String value, float defValue) {
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
			return defValue;
		}
	}
	/**
	 * 将字符串转换为double类型
	 * @param value    需要转换的字符串
	 * @param defValue 默认值
	 * @return 成功时返回转换的结果，否则返回默认值
	 */
	public static double parse(String value, double defValue) {
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return defValue;
		}
	}
}
