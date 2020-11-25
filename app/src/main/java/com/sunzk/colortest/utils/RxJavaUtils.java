package com.sunzk.colortest.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;

/**
 * RxJava工具类
 */
public class RxJavaUtils {

	private static final String TAG = "RxJavaUtils";

	/**
	 * RxJava2 + Retrofit2 通过mergeDelayError操作符并行多个请求时，如果其中一个请求的JSON解析异常，则会导致后续请求不触发onNext，导致数据丢失。<br/>
	 * 因此自己实现了一个 mergeDelayError 工具方法，以规避上述问题。
	 * @param observableList
	 * @param <T>
	 * @return
	 */
	public static <T> Observable<IndexedData<T>> mergeDelayError(List<Observable<T>> observableList) {
		return Observable.create(new ObservableOnSubscribe<IndexedData<T>>() {
			
			private int completeNum = 0;

			@Override
			public void subscribe(ObservableEmitter<IndexedData<T>> emitter) throws Exception {
				// 先获取一下请求的总数量
				int size = observableList.size();
				// 用于暂存请求中发生的所有异常，以便在所有Observable执行完毕后，统一抛出
				List<Throwable> throwableList = new ArrayList<>();
				// 遍历列表，执行Observable
				for (int i = 0; i < size; i++) {
					Observable<T> observable = observableList.get(i);
					// 为了在回调中使用，需要单独
					int index = i;
					observable.subscribe(new Observer<T>() {
						@Override
						public void onSubscribe(Disposable d) {

						}

						@Override
						public void onNext(T section) {
							try {
								emitter.onNext(new IndexedData<>(index, section));
							} catch (Throwable throwable) {
								throwableList.add(new RuntimeException(String.format(Locale.getDefault(), "error on observable %d", index), throwable));
							}
						}

						@Override
						public void onError(Throwable e) {
							Logger.d(TAG, "RxJavaUtils#onError- ", e);
							throwableList.add(new RuntimeException(String.format(Locale.getDefault(), "error on observable %d", index), e));
							onComplete();
						}

						@Override
						public void onComplete() {
							completeNum++;
							if (completeNum == size) {
								if (!throwableList.isEmpty()) {
									emitter.onError(new CompositeException(throwableList));
								} else {
									emitter.onComplete();
								}
							}
						}
					});
				}
			}
		});
	}

	public static class IndexedData<T> {
		private int index;
		private T data;

		public IndexedData(int index, T data) {
			this.index = index;
			this.data = data;
		}

		public int getIndex() {
			return index;
		}

		public T getData() {
			return data;
		}
	}

}
