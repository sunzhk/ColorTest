package com.sunzk.base.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

/**
 * 字符串工具类
 */
public class StringUtils {

	private static final String TAG = "StringUtils";

	private StringUtils() {
	}

	/**
	 * 快速判断两个字符串是否相同
	 * @param str1 需要判断的字符串
	 * @param str2 需要判断的字符串
	 * @return
	 */
	public static boolean isEquals(String str1, String str2) {
		if (str1 == str2) {
			return true;
		}
		if (str1 == null || str2 == null) {
			return false;
		}
		return str1.equals(str2);
	}
	
	/**
	 * 判断一个字符串是否为空。会自动去掉首尾空格
	 *
	 * @param str 字符串
	 * @return 字符串不为 null 且不是空字符串时返回 false，否则返回true
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * 与{@link #isEmpty(String)}的区别就是增加了"null"字符串的判断
	 *
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * 对于是blank的字符串{@link #isBlank(String)}返回"",否则返回原始字符串
	 *
	 * @param string
	 * @return
	 */
	public static String getBlankStrForEmptyResult(String string) {
		String result = "";
		if (!isBlank(string)) {
			result = string;
		}
		return result;
	}

	/**
	 * 判断一个或多个字符串是否为空
	 *
	 * @param strArray 字符串数组
	 * @return 所有字符串均不为空时返回 false，否则返回true
	 */
	public static boolean isEmpty(String... strArray) {
		for (String str : strArray) {
			if (StringUtils.isEmpty(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将Throwable转换为String，以便打印异常信息
	 * @param e 需要打印的Throwable
	 * @return 异常信息字符串
	 */
	public static String valueOf(Throwable e) {
		if (e == null) {
			return null;
		}
		try {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			return stringWriter.getBuffer().toString();
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 截取并重编码字符串
	 *
	 * @param text   目标字符串
	 * @param length 截取长度
	 * @param encode 采用的编码方式
	 * @return 截取后的字符串。如果重编码失败，则返回null
	 */
	public static String substring(String text, int length, String encode) {
		if (text == null) {
			return null;
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : text.toCharArray()) {
				currentLength += String.valueOf(c).getBytes(encode).length;
				if (currentLength <= length) {
					sb.append(c);
				} else {
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			Logger.d(TAG, "substring", e);
		}
		return null;
	}

	/**
	 * 将字符串中的字符都转换为全角
	 *
	 * @param str 要转换的字符串
	 * @return 全角字符串
	 */
	public static String toSBC(String str) {
		char[] c = str.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);

			}
		}
		return new String(c);
	}

	/**
	 * 判断str是否存在于strWithComma
	 *
	 * @param strWithComma 以 , 为分隔的字符串
	 * @param str
	 * @return
	 */
	public static boolean containElement(String strWithComma, String str) {
		if (isEmpty(strWithComma) || isEmpty(str)) {
			return false;
		}
		String[] args = strWithComma.split(",");
		for (String str1 : args) {
			if (str1.equals(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将字符串中的字符都转换为全角
	 *
	 * @param str 要转换的字符串
	 * @return 半角转全角字符串不包括数字
	 */
	public static String toSBCWithoutNumber(String str) {
		if (isBlank(str)) {
			return "";
		}
		char[] c = str.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] == '0' || c[i] == '1' || c[i] == '2' ||
					c[i] == '3' || c[i] == '4' || c[i] == '5'
					|| c[i] == '6' || c[i] == '7' || c[i] == '8' || c[i] == '9') {
				continue;
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);

			}
		}
		return new String(c);
	}
}
