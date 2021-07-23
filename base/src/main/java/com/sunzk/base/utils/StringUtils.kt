package com.sunzk.base.utils

import com.sunzk.base.utils.Logger.d
import java.io.PrintWriter
import java.io.StringWriter
import java.io.UnsupportedEncodingException

/**
 * 字符串工具类
 */
object StringUtils {
    private const val TAG = "StringUtils"

    /**
     * 快速判断两个字符串是否相同
     * @param str1 需要判断的字符串
     * @param str2 需要判断的字符串
     * @return
     */
    fun isEquals(str1: String?, str2: String?): Boolean {
        if (str1 === str2) {
            return true
        }
        return if (str1 == null || str2 == null) {
            false
        } else str1 == str2
    }

    /**
     * 判断一个字符串是否为空。会自动去掉首尾空格
     *
     * @param str 字符串
     * @return 字符串不为 null 且不是空字符串时返回 false，否则返回true
     */
    fun isEmpty(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' }.length == 0
    }

    /**
     * 与[.isEmpty]的区别就是增加了"null"字符串的判断
     *
     * @param str
     * @return
     */
    fun isBlank(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' }.length == 0
    }

    /**
     * 对于是blank的字符串[.isBlank]返回"",否则返回原始字符串
     *
     * @param string
     * @return
     */
    fun getBlankStrForEmptyResult(string: String?): String? {
        var result: String? = ""
        if (!isBlank(string)) {
            result = string
        }
        return result
    }

    /**
     * 判断一个或多个字符串是否为空
     *
     * @param strArray 字符串数组
     * @return 所有字符串均不为空时返回 false，否则返回true
     */
    fun isEmpty(vararg strArray: String?): Boolean {
        for (str in strArray) {
            if (isEmpty(str)) {
                return true
            }
        }
        return false
    }

    /**
     * 将Throwable转换为String，以便打印异常信息
     * @param e 需要打印的Throwable
     * @return 异常信息字符串
     */
    fun valueOf(e: Throwable?): String? {
        return if (e == null) {
            null
        } else try {
            val stringWriter = StringWriter()
            val printWriter = PrintWriter(stringWriter)
            e.printStackTrace(printWriter)
            stringWriter.buffer.toString()
        } catch (ex: Exception) {
            null
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
    fun substring(text: String?, length: Int, encode: String?): String? {
        if (text == null) {
            return null
        }
        try {
            val sb = StringBuilder()
            var currentLength = 0
            for (c in text.toCharArray()) {
                currentLength += c.toString().toByteArray(charset(encode!!)).size
                if (currentLength <= length) {
                    sb.append(c)
                } else {
                    break
                }
            }
            return sb.toString()
        } catch (e: UnsupportedEncodingException) {
            d(TAG, "substring", e)
        }
        return null
    }

    /**
     * 将字符串中的字符都转换为全角
     *
     * @param str 要转换的字符串
     * @return 全角字符串
     */
    fun toSBC(str: String): String {
        val c = str.toCharArray()
        for (i in c.indices) {
            if (c[i] == ' ') {
                c[i] = '\u3000'
            } else if (c[i] < '\u007f') {
                c[i] = (c[i] + 65248)
            }
        }
        return String(c)
    }

    /**
     * 判断str是否存在于strWithComma
     *
     * @param strWithComma 以 , 为分隔的字符串
     * @param str
     * @return
     */
    fun containElement(strWithComma: String, str: String): Boolean {
        if (isEmpty(strWithComma) || isEmpty(str)) {
            return false
        }
        val args = strWithComma.split(",").toTypedArray()
        for (str1 in args) {
            if (str1 == str) {
                return true
            }
        }
        return false
    }

    /**
     * 将字符串中的字符都转换为全角
     *
     * @param str 要转换的字符串
     * @return 半角转全角字符串不包括数字
     */
    fun toSBCWithoutNumber(str: String): String {
        if (isBlank(str)) {
            return ""
        }
        val c = str.toCharArray()
        for (i in c.indices) {
            if (c[i] == ' ') {
                c[i] = '\u3000'
            } else if (c[i] == '0' || c[i] == '1' || c[i] == '2' || c[i] == '3' || c[i] == '4' || c[i] == '5' || c[i] == '6' || c[i] == '7' || c[i] == '8' || c[i] == '9') {
                continue
            } else if (c[i] < '\u007f') {
                c[i] = (c[i] + 65248)
            }
        }
        return String(c)
    }
}