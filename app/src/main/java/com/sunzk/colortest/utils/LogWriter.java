package com.sunzk.colortest.utils;

public class LogWriter {
	
	public void init() {
		MainHandler.getInstance().postDelayed(() -> {
			ThreadHelper.getInstance().execute(() -> {
				
			});
		}, 10_000);
	}
	
}
