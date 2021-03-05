package com.sunzk.base.utils;

import androidx.annotation.NonNull;

/**
 * Created by sunzhk on 2018/9/17.
 */

public interface IPinYinConverter {

	/**
	 * 单个汉字转拼音
	 * @param chinese 单个汉字
	 * @return
	 */
	String toPinYin(@NonNull char chinese);

	/**
	 * 汉字字符串转拼音
	 * @param chinese 汉字字符串
	 * @return
	 */
	String toPinYin(@NonNull String chinese);

	/**
	 * 单个汉字获取拼音首字母
	 * @param chinese 单个汉字
	 * @return
	 */
	String toPinYinInitials(@NonNull char chinese);

	/**
	 * 汉字字符串获取拼音首字母
	 * @param chinese 汉字字符串
	 * @return
	 */
	String toPinYinInitials(@NonNull String chinese);

	/**
	 * 拼音转汉字
	 * @param pingYin
	 * @return
	 */
	String toChinese(@NonNull String pingYin);

	/**
	 * 单个字符判断是否是汉字
	 * @param chinese
	 * @return
	 */
	boolean isChinese(@NonNull char chinese);

	/**
	 * 汉字字符串判断是否是汉字
	 * @param chinese
	 * @return
	 */
	boolean isChinese(@NonNull String chinese);
}
