package com.sunzk.base.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import com.sunzk.base.utils.StringUtils.isEmpty
import java.io.*
import java.nio.charset.Charset
import java.util.*

/**
 * 文件工具类
 */
class FileUtils private constructor() {
    interface IAssetsTraversingInterceptor {
        fun checkNeed(assetFile: String?): Boolean
    }

    companion object {
        private const val TAG = "FileUtils"
        private const val SIZE = 1024 * 8

        /*----assets工具开始----*/
        fun traversingAssets(
            context: Context,
            interceptor: IAssetsTraversingInterceptor?
        ): List<String> {
            val assetPaths = LinkedList<String>()
            val fileList = ArrayList<String>()
            assetPaths.add("")
            while (!assetPaths.isEmpty()) {
                val path = assetPaths.removeFirst()
                try {
                    val files = context.assets.list(path)
                    if (files == null || files.size == 0) {
                        continue
                    }
                    for (fileName in files) {
                        var fullPath: String
                        fullPath = if (isEmpty(path)) {
                            fileName
                        } else {
                            "$path/$fileName"
                        }
                        if (interceptor != null) {
                            if (interceptor.checkNeed(fileName)) {
                                fileList.add(fullPath)
                            } else {
                                assetPaths.add(fullPath)
                            }
                        } else {
                            fileList.add(fullPath)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return fileList
        }
        /*----assets工具结束----*/
        /**
         * 获取缓存目录
         *
         * @param context Application context
         * @return Cache [directory][File]
         */
        fun getCacheDirectory(context: Context): File? {
            var appCacheDir: File? = null
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && hasExternalStoragePermission(
                    context
                )
            ) {
                appCacheDir = context.cacheDir
            }
            if (appCacheDir == null) {
                appCacheDir = getExternalCacheDir(context)
            }
            if (appCacheDir == null) {
                Log.w(TAG, "Can't define system cache directory! The app should be re-installed.")
            }
            return appCacheDir
        }

        private fun getExternalCacheDir(context: Context): File? {
            val dataDir = File(File(Environment.getExternalStorageDirectory(), "Android"), "data")
            val appCacheDir = File(File(dataDir, context.packageName), "cache")
            if (!appCacheDir.exists()) {
                if (!appCacheDir.mkdirs()) {
                    Log.w(TAG, "Unable to create external cache directory")
                    return null
                }
            }
            return appCacheDir
        }

        /**
         * 判断是否有内存写权限
         * @param context 上下文
         * @return 如果有写权限则返回true
         */
        fun hasExternalStoragePermission(context: Context): Boolean {
            val perm =
                context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE")
            return perm == PackageManager.PERMISSION_GRANTED
        }
        /* read byte 开始 */
        /**
         * 以字节流的方式读取到字符串。
         *
         * @param is 输入流
         * @return 字符串
         */
        fun readBytesToString(`is`: InputStream): String {
            return String(readBytes(`is`)!!)
        }

        /**
         * 以字节流的方式读取到字符串。
         *
         * @param is      输入流
         * @param charset 字符集
         * @return 字符串
         */
        fun readBytesToString(`is`: InputStream, charset: String?): String? {
            try {
                return String(readBytes(`is`)!!, Charset.forName(charset))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * 以字节流的方式从文件中读取字符串
         *
         * @param file    文件
         * @param charset 字符集
         * @return 字符串
         */
        fun readBytesToString(file: File, charset: String): String? {
            try {
                return String(readBytes(file)!!, Charset.forName(charset))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * 以字节流的方式从文件中读取字符串。
         *
         * @param file 文件
         * @return 字符串
         */
        fun readBytesToString(file: File?): String {
            return String(readBytes(file)!!)
        }
        // ---------------------readBytesToString 完成。分割线----------------------
        /**
         * 以字符流的方式读取到字符串。
         *
         * @param file 文件
         * @return 字符串
         */
        fun readCharsToString(file: File?): String? {
            try {
                return readCharsToString(FileInputStream(file), null)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * 以字节流的方式读取到字符串。
         *
         * @param file    文件
         * @param charset 字符集
         * @return 字符串
         */
        fun readCharsToString(file: File?, charset: String?): String? {
            try {
                return readCharsToString(FileInputStream(file), charset)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * 以字符流的方式读取到字符串。默认编码
         *
         * @param is 输入流
         * @return 字符串
         */
        fun readCharsToString(`is`: InputStream): String {
            return String(readChars(`is`, null)!!)
        }

        /**
         * 以字符流的方式读取到字符串。
         *
         * @param is      输入流
         * @param charset 编码
         * @return 字符串
         */
        fun readCharsToString(`is`: InputStream, charset: String?): String {
            return String(readChars(`is`, charset)!!)
        }
        // ---------------readCharsToString 完成。分割线-----------------------
        /**
         * 以字节流的方式读取到字符串。
         *
         * @param file 文件
         * @return 字节数组
         */
        fun readBytes(file: File?): ByteArray? {
            try {
                return readBytes(FileInputStream(file))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * 以字节流的方式读取到字符串。
         *
         * @param is 输入流
         * @return 字节数组
         */
        fun readBytes(`is`: InputStream): ByteArray? {
            var bytes: ByteArray? = null
            try {
                val bis = BufferedInputStream(`is`)
                val cbuf = ByteArray(SIZE)
                var len: Int
                val outWriter = ByteArrayOutputStream()
                while (bis.read(cbuf).also { len = it } != -1) {
                    outWriter.write(cbuf, 0, len)
                }
                outWriter.flush()
                bis.close()
                `is`.close()
                bytes = outWriter.toByteArray()
                outWriter.close()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bytes
        }

        /**
         * 以字符流的方式读取到字符串。
         *
         * @param file    文件
         * @param charset 编码
         * @return 字符数组
         */
        fun readChars(file: File?, charset: String?): CharArray? {
            try {
                return readChars(FileInputStream(file), charset)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * 以字符流的方式读取到字符串。
         *
         * @param is      输入流
         * @param charset 编码
         * @return 字符数组
         */
        fun readChars(`is`: InputStream, charset: String?): CharArray? {
            var chars: CharArray? = null
            try {
                var isr: InputStreamReader? = null
                isr = if (charset == null) {
                    InputStreamReader(`is`)
                } else {
                    InputStreamReader(`is`, charset)
                }
                val br = BufferedReader(isr)
                val cbuf = CharArray(SIZE)
                var len: Int
                val outWriter = CharArrayWriter()
                while (br.read(cbuf).also { len = it } != -1) {
                    outWriter.write(cbuf, 0, len)
                }
                outWriter.flush()
                br.close()
                isr.close()
                `is`.close()
                chars = outWriter.toCharArray()
                outWriter.close()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return chars
        }
        /**
         * 通过字节输出流输出bytes
         *
         * @param os     输出流
         * @param text   字节数组
         * @param off    数组起始下标
         * @param length 长度
         */
        // -----------------------readxxx 完成。分割线-----------------------
        // -----------------------read 部分完成。接下来是write的部分------------
        /**
         * 通过字节输出流输出bytes
         *
         * @param os   输出流
         * @param text 字节数组
         */
        @JvmOverloads
        fun writeBytes(os: OutputStream, text: ByteArray, off: Int = 0, length: Int = text.size) {
            try {
                val bos = BufferedOutputStream(os)
                bos.write(text, off, length)
                bos.flush()
                bos.close()
                os.close()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        // ----------------------writeByte 完成。分割------------------------
        /**
         * 通过字符输出流输出chars
         *
         * @param os      输出流
         * @param text    字节数组
         * @param charset 编码方式
         */
        fun writeChars(os: OutputStream, text: CharArray, charset: String?) {
            writeChars(os, text, 0, text.size, charset)
        }

        /**
         * 通过字符输出流输出chars
         *
         * @param os      输出流
         * @param text    字节数组
         * @param off     数组起始下标
         * @param length  长度
         * @param charset 编码方式
         */
        fun writeChars(
            os: OutputStream,
            text: CharArray?,
            off: Int,
            length: Int,
            charset: String?
        ) {
            try {
                var osw: OutputStreamWriter? = null
                osw = if (charset == null) {
                    OutputStreamWriter(os)
                } else {
                    OutputStreamWriter(os, charset)
                }
                val bw = BufferedWriter(osw)
                bw.write(text, off, length)
                bw.flush()
                bw.close()
                osw.close()
                os.close()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        /**
         * 将字符串写入文件
         *
         * @param file    文件
         * @param append  是否追加
         * @param text    字符串
         * @param off     起始下标
         * @param length  长度
         * @param charset 编码
         * @return 写入成功时返回true，否则返回false
         */
        // ----------------------writeChars 完成。分割------------------------
        /**
         * 将字符串以默认编码写入文件
         *
         * @param file 文件
         * @param text 字符串
         * @return 写入成功时返回true，否则返回false
         */
        @JvmOverloads
        fun writeString(
            file: File?,
            append: Boolean,
            text: String,
            off: Int = 0,
            length: Int = text.length,
            charset: String? = null
        ): Boolean {
            try {
                val fileOutputStream = FileOutputStream(file, append)
                return writeString(fileOutputStream, text, off, length, charset)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return false
        }

        /**
         * 将字符串以默认编码写入文件
         *
         * @param file 文件
         * @param text 字符串
         * @return 写入成功时返回true，否则返回false
         */
        fun writeString(file: File?, text: String): Boolean {
            return writeString(file, false, text, 0, text.length, null)
        }

        /**
         * 将字符串写入文件（默认覆盖）
         *
         * @param file    文件
         * @param append  是否追加
         * @param text    字符串
         * @param charset 编码
         * @return 写入成功时返回true，否则返回false
         */
        fun writeString(file: File?, append: Boolean, text: String, charset: String?): Boolean {
            return writeString(file, append, text, 0, text.length, charset)
        }

        /**
         * 将字符串写入文件（默认覆盖）
         *
         * @param file    文件
         * @param text    字符串
         * @param charset 编码
         * @return 写入成功时返回true，否则返回false
         */
        fun writeString(file: File?, text: String, charset: String?): Boolean {
            return writeString(file, false, text, 0, text.length, charset)
        }

        /**
         * 字符输出流输出字符串
         *
         * @param os      输出流
         * @param text    字符串
         * @param charset 编码
         * @return 写入成功时返回true，否则返回false
         */
        fun writeString(os: OutputStream, text: String, charset: String?): Boolean {
            return writeString(os, text, 0, text.length, charset)
        }

        /**
         * 字符输出流输出字符串
         *
         * @param os      输出流
         * @param text    字符串
         * @param off     起始下标
         * @param length  长度
         * @param charset 编码
         * @return 写入成功时返回true，否则返回false
         */
        fun writeString(
            os: OutputStream,
            text: String?,
            off: Int,
            length: Int,
            charset: String?
        ): Boolean {
            try {
                val osw = if (isEmpty(charset)) OutputStreamWriter(os) else OutputStreamWriter(
                    os,
                    charset
                )
                val bw = BufferedWriter(osw)
                bw.write(text, off, length)
                bw.flush()
                if (os is FileOutputStream) {
                    try {
                        os.fd.sync()
                    } catch (e: SyncFailedException) {
                        e.printStackTrace()
                    }
                }
                bw.close()
                osw.close()
                os.close()
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    }

    init {
        throw Exception("Cannot be instantiated")
    }
}