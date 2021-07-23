package com.sunzk.base.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.SyncFailedException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 文件工具类
 */
public class FileUtils {

	private static final String TAG = "FileUtils";

	private static final int SIZE = 1024 * 8;

	private FileUtils() throws Exception {
		throw new Exception("Cannot be instantiated");
	}

	/*----assets工具开始----*/
	
	public static List<String> traversingAssets(@NonNull Context context, @Nullable IAssetsTraversingInterceptor interceptor) {
		LinkedList<String> assetPaths = new LinkedList<>();
		ArrayList<String> fileList = new ArrayList<>();
		assetPaths.add("");
		while (!assetPaths.isEmpty()) {
			String path = assetPaths.removeFirst();
			try {
				String[] files = context.getAssets().list(path);
				if (files == null || files.length == 0) {
					continue;
				}
				for (String fileName : files) {
					String fullPath;
					if (StringUtils.isEmpty(path)) {
						fullPath = fileName;
					} else {
						fullPath = path + "/" + fileName;
					}
					if (interceptor != null) {
						if (interceptor.checkNeed(fileName)){
							fileList.add(fullPath);
						} else {
							assetPaths.add(fullPath);
						}
					} else {
						fileList.add(fullPath);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileList;
	}

	public interface IAssetsTraversingInterceptor {
		boolean checkNeed(String assetFile);
	}

	/*----assets工具结束----*/
	
	/**
	 * 获取缓存目录
	 *
	 * @param context Application context
	 * @return Cache {@link File directory}
	 */
	public static File getCacheDirectory(Context context) {
		File appCacheDir = null;
		if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
			appCacheDir = context.getCacheDir();
		}
		if (appCacheDir == null) {
			appCacheDir = getExternalCacheDir(context);
		}
		if (appCacheDir == null) {
			Log.w(TAG,"Can't define system cache directory! The app should be re-installed.");
		}
		return appCacheDir;
	}

	private static File getExternalCacheDir(Context context) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				Log.w(TAG,"Unable to create external cache directory");
				return null;
			}
		}
		return appCacheDir;
	}

	/**
	 * 判断是否有内存写权限
	 * @param context 上下文
	 * @return 如果有写权限则返回true
	 */
	public static boolean hasExternalStoragePermission(Context context) {
		int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
		return perm == PackageManager.PERMISSION_GRANTED;
	}

	/* read byte 开始 */

	/**
	 * 以字节流的方式读取到字符串。
	 *
	 * @param is 输入流
	 * @return 字符串
	 */
	public static String readBytesToString(InputStream is) {
		return new String(readBytes(is));
	}

	/**
	 * 以字节流的方式读取到字符串。
	 *
	 * @param is      输入流
	 * @param charset 字符集
	 * @return 字符串
	 */
	public static String readBytesToString(InputStream is, String charset) {
		try {
			return new String(readBytes(is), charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以字节流的方式从文件中读取字符串
	 *
	 * @param file    文件
	 * @param charset 字符集
	 * @return 字符串
	 */
	public static String readBytesToString(File file, String charset) {
		try {
			return new String(readBytes(file), charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 以字节流的方式从文件中读取字符串。
	 *
	 * @param file 文件
	 * @return 字符串
	 */
	public static String readBytesToString(File file) {
		return new String(readBytes(file));
	}

	// ---------------------readBytesToString 完成。分割线----------------------

	/**
	 * 以字符流的方式读取到字符串。
	 *
	 * @param file 文件
	 * @return 字符串
	 */
	public static String readCharsToString(File file) {
		try {
			return readCharsToString(new FileInputStream(file), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以字节流的方式读取到字符串。
	 *
	 * @param file    文件
	 * @param charset 字符集
	 * @return 字符串
	 */
	public static String readCharsToString(File file, String charset) {
		try {
			return readCharsToString(new FileInputStream(file), charset);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以字符流的方式读取到字符串。默认编码
	 *
	 * @param is 输入流
	 * @return 字符串
	 */
	public static String readCharsToString(InputStream is) {
		return new String(readChars(is, null));
	}

	/**
	 * 以字符流的方式读取到字符串。
	 *
	 * @param is      输入流
	 * @param charset 编码
	 * @return 字符串
	 */
	public static String readCharsToString(InputStream is, String charset) {
		return new String(readChars(is, charset));
	}

	// ---------------readCharsToString 完成。分割线-----------------------

	/**
	 * 以字节流的方式读取到字符串。
	 *
	 * @param file 文件
	 * @return 字节数组
	 */
	public static byte[] readBytes(File file) {
		try {
			return readBytes(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以字节流的方式读取到字符串。
	 *
	 * @param is 输入流
	 * @return 字节数组
	 */
	public static byte[] readBytes(InputStream is) {
		byte[] bytes = null;
		try {
			BufferedInputStream bis = new BufferedInputStream(is);

			byte[] cbuf = new byte[SIZE];
			int len;
			ByteArrayOutputStream outWriter = new ByteArrayOutputStream();
			while ((len = bis.read(cbuf)) != -1) {
				outWriter.write(cbuf, 0, len);
			}
			outWriter.flush();

			bis.close();
			is.close();

			bytes = outWriter.toByteArray();
			outWriter.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 以字符流的方式读取到字符串。
	 *
	 * @param file    文件
	 * @param charset 编码
	 * @return 字符数组
	 */
	public static char[] readChars(File file, String charset) {
		try {
			return readChars(new FileInputStream(file), charset);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以字符流的方式读取到字符串。
	 *
	 * @param is      输入流
	 * @param charset 编码
	 * @return 字符数组
	 */
	public static char[] readChars(InputStream is, String charset) {
		char[] chars = null;
		try {
			InputStreamReader isr = null;
			if (charset == null) {
				isr = new InputStreamReader(is);
			} else {
				isr = new InputStreamReader(is, charset);
			}
			BufferedReader br = new BufferedReader(isr);
			char[] cbuf = new char[SIZE];
			int len;
			CharArrayWriter outWriter = new CharArrayWriter();
			while ((len = br.read(cbuf)) != -1) {
				outWriter.write(cbuf, 0, len);
			}
			outWriter.flush();

			br.close();
			isr.close();
			is.close();

			chars = outWriter.toCharArray();
			outWriter.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return chars;
	}

	// -----------------------readxxx 完成。分割线-----------------------
	// -----------------------read 部分完成。接下来是write的部分------------

	/**
	 * 通过字节输出流输出bytes
	 *
	 * @param os   输出流
	 * @param text 字节数组
	 */
	public static void writeBytes(OutputStream os, byte[] text) {
		writeBytes(os, text, 0, text.length);
	}

	/**
	 * 通过字节输出流输出bytes
	 *
	 * @param os     输出流
	 * @param text   字节数组
	 * @param off    数组起始下标
	 * @param length 长度
	 */
	public static void writeBytes(OutputStream os, byte[] text, int off, int length) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(os);
			bos.write(text, off, length);

			bos.flush();
			bos.close();
			os.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	public static void writeChars(OutputStream os, char[] text, String charset) {
		writeChars(os, text, 0, text.length, charset);
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
	public static void writeChars(OutputStream os, char[] text, int off, int length, String charset) {
		try {
			OutputStreamWriter osw = null;

			if (charset == null) {
				osw = new OutputStreamWriter(os);
			} else {
				osw = new OutputStreamWriter(os, charset);
			}
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(text, off, length);

			bw.flush();
			bw.close();
			osw.close();
			os.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ----------------------writeChars 完成。分割------------------------

	/**
	 * 将字符串以默认编码写入文件
	 *
	 * @param file 文件
	 * @param text 字符串
	 * @return 写入成功时返回true，否则返回false
	 */
	public static boolean writeString(File file, boolean append, String text) {
		return writeString(file, append, text, 0, text.length(), null);
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
	public static boolean writeString(File file, boolean append, String text, int off, int length, String charset) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file, append);
			return writeString(fileOutputStream, text, off, length, charset);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 将字符串以默认编码写入文件
	 *
	 * @param file 文件
	 * @param text 字符串
	 * @return 写入成功时返回true，否则返回false
	 */
	public static boolean writeString(File file, String text) {
		return writeString(file, false, text, 0, text.length(), null);
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
	public static boolean writeString(File file, boolean append, String text, String charset) {
		return writeString(file, append, text, 0, text.length(), charset);
	}

	/**
	 * 将字符串写入文件（默认覆盖）
	 *
	 * @param file    文件
	 * @param text    字符串
	 * @param charset 编码
	 * @return 写入成功时返回true，否则返回false
	 */
	public static boolean writeString(File file, String text, String charset) {
		return writeString(file, false, text, 0, text.length(), charset);
	}

	/**
	 * 字符输出流输出字符串
	 *
	 * @param os      输出流
	 * @param text    字符串
	 * @param charset 编码
	 * @return 写入成功时返回true，否则返回false
	 */
	public static boolean writeString(OutputStream os, String text, String charset) {
		return writeString(os, text, 0, text.length(), charset);
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
	public static boolean writeString(OutputStream os, String text, int off, int length, String charset) {
		try {
			OutputStreamWriter osw = StringUtils.isEmpty(charset) ? new OutputStreamWriter(os) : new OutputStreamWriter(os, charset);
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(text, off, length);
			
			bw.flush();
			if (os instanceof FileOutputStream) {
				try {
					((FileOutputStream) os).getFD().sync();
				} catch (SyncFailedException e) {
					e.printStackTrace();
				}
			}
			bw.close();
			osw.close();
			os.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}