package com.sunzk.colortest.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 图片工具类
 * Created by sunzhk on 2018/4/17.
 */
public class ImageUtils {

	/**
	 * 加载一个本地图片
	 *
	 * @param localImageFilePath 图片路径
	 * @return {@link Bitmap}
	 */
	public static Bitmap getLocalBitmap(String localImageFilePath) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = new FileInputStream(localImageFilePath);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

}
