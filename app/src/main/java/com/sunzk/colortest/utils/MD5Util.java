package com.sunzk.colortest.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * MD5工具类
 */
public final class MD5Util {

	private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	private MD5Util(){}

	/**
	 * 计算文件的MD5
	 *
	 * @param filePath 文件路径
	 * @return 文件的MD5值。如果计算出错或文件无法读取，则返回 null
	 */
	public static String md5sum(String filePath) {
		byte[] buffer = new byte[1024];
		int numRead;
		MessageDigest md5;
		try (InputStream fis = new FileInputStream(filePath)){
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			return NumberUtils.toHexString(md5.digest());
		} catch (Exception e) {
			Logger.d("MD5Util", "", e);
		}
		return null;
	}

	/**
	 * 计算指定字符串的MD5
	 *
	 * @param s 字符串
	 * @return 字符串的MD5值
	 */
	public static String md5(String s) {
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char[] str = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
				str[k++] = HEX_DIGITS[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

}
