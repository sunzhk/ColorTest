package com.sunzk.colortest.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 轻量级的线程帮助类，主要用于有的时候希望开一个线程完成一个一次性耗时不长的任务。
 * 不用用这个类去做大量的高并发线程操作。大量的并发任务建议用RxJava去做
 *
 * @author yongjie created on 2020/3/12.
 */
public class ThreadHelper {
	private static final String TAG = "ThreadHelper";
	private static volatile ThreadHelper threadHelper;

	private ThreadPoolExecutor threadPoolExecutor;
	private static final int CORE_POOL_SIZE = 1;
	private static final long KEEP_ALIVE_TIME = 10L;
	private static final int BLOCK_QUEUE_SIZE = 5;


	private ThreadHelper() {
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, availableProcessors * 2, KEEP_ALIVE_TIME, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(BLOCK_QUEUE_SIZE));
	}

	public static ThreadHelper getInstance() {
		if (threadHelper == null) {
			synchronized (ThreadHelper.class) {
				if (threadHelper == null) {
					threadHelper = new ThreadHelper();
				}
			}
		}
		return threadHelper;
	}


	public void execute(Runnable runnable) {
		threadPoolExecutor.execute(runnable);
	}

	public boolean remove(Runnable runnable) {
		boolean result = threadPoolExecutor.remove(runnable);
		return result;
	}
}
