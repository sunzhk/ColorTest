package com.sunzk.colortest;

import android.app.Application;

import com.sunzk.colortest.utils.AppUtils;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		AppUtils.setLogEnable(BuildConfig.DEBUG);
	}
}
