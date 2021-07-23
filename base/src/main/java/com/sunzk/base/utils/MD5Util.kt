package com.sunzk.base.utils

import com.sunzk.base.utils.Logger.d
import java.io.FileInputStream
import java.security.MessageDigest

/**
 * MD5工具类
 */
object MD5Util {
    private val HEX_DIGITS: CharArray =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    /**
     * 计算文件的MD5
     *
     * @param filePath 文件路径
     * @return 文件的MD5值。如果计算出错或文件无法读取，则返回 null
     */
    fun md5sum(filePath: String?): String? {
        val buffer = ByteArray(1024)
        var numRead: Int
        var md5: MessageDigest
        try {
            FileInputStream(filePath).use { fis ->
                md5 = MessageDigest.getInstance("MD5")
                while (fis.read(buffer).also { numRead = it } > 0) {
                    md5.update(buffer, 0, numRead)
                }
                return NumberUtils.toHexString(md5.digest())
            }
        } catch (e: Exception) {
            d("MD5Util", "", e)
        }
        return null
    }

    /**
     * 计算指定字符串的MD5
     *
     * @param s 字符串
     * @return 字符串的MD5值
     */
    fun md5(s: String): String? {
        return try {
            val btInput = s.toByteArray()
            // 获得MD5摘要算法的 MessageDigest 对象
            val mdInst = MessageDigest.getInstance("MD5")
            // 使用指定的字节更新摘要
            mdInst.update(btInput)
            // 获得密文
            val md = mdInst.digest()
            // 把密文转换成十六进制的字符串形式
            val j = md.size
            val str = CharArray(j * 2)
            var k = 0
            for (i in 0 until j) {
                val byte0 = md[i].toInt()
                str[k++] = HEX_DIGITS[byte0 ushr 4 and 0xf]
                str[k++] = HEX_DIGITS[byte0 and 0xf]
            }
            String(str)
        } catch (e: Exception) {
            null
        }
    }
}