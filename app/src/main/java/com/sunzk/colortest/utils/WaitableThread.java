package com.sunzk.colortest.utils;

/**
 * 可休眠的线程，减少CPU占用
 * 在线程内调用{@link #aWait()}方法休眠线程，然后从外部调用{@link #wake()}方法唤醒线程
 * Created by sunzhk on 2018/1/3.
 */
public class WaitableThread extends Thread {

	private Object lock = new Object();
	private volatile boolean aWaitFlag = false;

	public void aWait() {
		if(aWaitFlag){
			return;
		}
		synchronized (lock) {
			try {
				aWaitFlag = true;
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void wake() {
		synchronized (lock) {
			if (aWaitFlag) {
				lock.notify();
				aWaitFlag = false;
			}
		}
	}

	public boolean isWaiting() {
		return aWaitFlag;
	}

}
