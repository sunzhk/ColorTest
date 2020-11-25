package com.sunzk.colortest.utils;

/**
 * 可休眠的线程
 */
public class SleepableThread extends Thread {

	private Object lock = new Object();
	private volatile boolean aWaitFlag = false;

	/**
	 * 使线程进入休眠状态，需要在线程内部调用
	 */
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

	/**
	 * 唤醒线程，在线程外部调用
	 */
	public void wake() {
		synchronized (lock) {
			if (aWaitFlag) {
				lock.notify();
				aWaitFlag = false;
			}
		}
	}

	/**
	 * 判断线程是否处于休眠状态中
	 * @return 如果线程处于休眠中返回true，否则返回false
	 */
	public boolean isWaiting() {
		return aWaitFlag;
	}

}
