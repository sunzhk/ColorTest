package com.sunzk.colortest.utils;

import com.sunzk.colortest.annotation.Keep;

import java.util.Collections;
import java.util.Vector;

import androidx.annotation.IntRange;

/**
 * 替代AsyncTask，免去了繁琐的aWait和wake，只需要在任务完成后调用{@link Task#finishTask()}方法即可切换到下一个任务
 * Created by sunzhk on 2019/1/24.
 */
public class LoopThread extends Thread {

	private static final String TAG = "LoopThread";
	
	private static final int TASK_STATE_DETECT_INTERVAL = 200;
	
	private final Vector<Task> tasks = new Vector<>();

	private boolean loopFlag = true;
	
	private OnLoopThreadFinishedListener listener;

	@Override
	public void run() {
		while (loopFlag) {
			Task task;
			synchronized (tasks) {
				if (tasks.isEmpty()) {
					break;
				}
				task = tasks.remove(0);
			}
			task.run();
			// 有可能需要等待异步任务结束，因此进入循环等待
			while (!task.isFinished()) {
				try {
					sleep(TASK_STATE_DETECT_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		//结束后提供回调以便处理
		MainHandler.getInstance().post(() -> {
			if (listener != null) {
				listener.onFinished();
			}
		});
	}

	/**
	 * 增加任务
	 * @param task
	 */
	public void add(Task task) {
		if (tasks.contains(task)) {
			return;
		}
		tasks.add(task);
		synchronized (task) {
			Collections.sort(tasks, (o1, o2) -> o2.getPriority() - o1.getPriority());
		}
	}
	
	public boolean remove(Task task) {
		return task != null && tasks.remove(task);
	}
	
	public void clear() {
		tasks.clear();
	}

	/**
	 * 不会立刻结束线程，而是会在当前任务完成后再结束
	 */
	public void finish() {
		loopFlag = false;
		tasks.clear();
	}

	public void setOnLoopThreadFinishedListener(OnLoopThreadFinishedListener listener) {
		this.listener = listener;
	}

	public static abstract class Task implements Runnable {

		@IntRange(from = 0, to = 1000)
		private int priority = 0;
		
		private volatile boolean finished = false;

		public Task() {
			this(500);
		}

		public Task(int priority) {
			this.priority = priority;
		}

		public void finishTask() {
			finished = true;
		}
		
		public boolean isFinished() {
			return finished;
		}

		/**
		 * 获取任务的优先级
		 *
		 * @return
		 */
		public final int getPriority() {
			return priority;
		}

		/**
		 * 设置任务的优先级
		 *
		 * @param priority 优先级：0 ~ 1000，优先级越高就越先执行
		 */
		@Keep
		public final void setPriority(@IntRange(from = 0, to = 1000) int priority) {
			if(priority < 0 || priority > 1000) {
				throw new IllegalArgumentException("优先级的范围是 0 ~ 1000");
			}
			this.priority = priority;
		}
		
	}

	public static abstract class SyncTask extends Task {
		
		@Override
		public void run() {
			
		}
	}
	
	public interface OnLoopThreadFinishedListener {
		void onFinished();
	}

}
