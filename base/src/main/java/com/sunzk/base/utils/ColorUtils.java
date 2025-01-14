package com.sunzk.base.utils;

import java.util.Random;

/**
 * 颜色工具
 * @author sunzk
 */
public class ColorUtils {
	
	private static final String TAG = "ColorUtils";

	/**
	 * 红色	R255,G0,B0	0°	血液、草莓
	 * 橙色	R255,G128,B0	30°	火、橙子
	 * 黄色	R255,G255,B0	60°	香蕉、杧果
	 * 黄绿	R128,G255,B0	90°	柠檬
	 * 绿色	R0,G255,B0	120°	草、树叶
	 * 青绿	R0,G255,B128	150°	军装
	 * 青色	R0,G255,B255	180°	水面、天空
	 * 靛蓝	R0,G128,B255	210°	水面、天空
	 * 蓝色	R0,G0,B255	240°	海、墨水
	 * 紫色	R128,G0,B255	270°	葡萄、茄子
	 * 品红	R255,G0,B255	300°	火、桃子
	 * 紫红	R255,G0,B128	330°	墨水
	 */
	public static final int[] HUE_COLOR_LIST = new int[]{
			0xFFFF0000, //红色	R255,G0,B0
			0xFFFF8000, //橙色	R255,G128,B0
			0xFFFFFF00, //黄色	R255,G255,B0
			0xFF80FF00, //黄绿	R128,G255,B0
			0xFF00FF00, //绿色	R0,G255,B0
			0xFF00FF80, //青绿	R0,G255,B128
			0xFF00FFFF, //青色	R0,G255,B255
			0xFF0000FF, //蓝色	R0,G0,B255
			0xFF8000FF, //紫色	R128,G0,B255
			0xFFFF00FF, //品红	R255,G0,B255
			0xFFFF0080, //紫红	R255,G0,B128
			0xFFFF0000};//红色	R255,G0,B0

	public static float[] randomHSBColor() {
		return randomHSBColor(0, 0, 0);
	}

	public static float[] randomHSBColor(float minH, float minS, float minB) {
		return randomHSBColor(minH, 360f, minS, 1f, minB, 1f);
	}

	public static float[] randomHSBColor(float minH, float maxH, float minS, float maxS, float minB, float maxB) {
		Random random = new Random();
		float h = random.nextInt((int) (maxH - minH)) + minH;
		float s = (maxS - minS) * random.nextFloat() + minS;
		float b = (maxB - minB) * random.nextFloat() + minB;
		return new float[]{h, s, b};
	}
}
