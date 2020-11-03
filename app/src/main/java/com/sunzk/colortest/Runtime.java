package com.sunzk.colortest;

public class Runtime {
	
	public static int testResultNumber = 0;
	
	private static boolean needBGM = false;

	public static boolean isNeedBGM() {
		return needBGM;
	}

	public static void setNeedBGM(boolean needBGM) {
		Runtime.needBGM = needBGM;
	}
}
