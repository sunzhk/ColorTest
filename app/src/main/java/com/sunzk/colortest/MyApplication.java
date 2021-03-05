package com.sunzk.colortest;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunzk.colortest.entity.ModeEntity;
import com.sunzk.base.utils.AppUtils;
import com.sunzk.base.utils.Logger;
import com.sunzk.base.utils.StringUtils;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyApplication extends Application {
	
	private static final String TAG = "MyApplication";
	
	private static MyApplication instance;

	public static MyApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		instance = this;
		super.onCreate();
		AppUtils.setLogEnable(BuildConfig.DEBUG);
		initModeListData();
	}

	private void initModeListData() {
		RxDataStore<Preferences> dataStore = new RxPreferenceDataStoreBuilder(getApplicationContext(), Constant.MODE_SELECT_DATA_NAME).build();
		dataStore.data()
				.map(preferences -> preferences.get(PreferencesKeys.stringKey(Constant.MODE_SELECT_DATA_KEY)))
				.subscribeOn(Schedulers.io())
				.map(json -> {
					List<ModeEntity> modeEntityList = new ArrayList<>();
					try {
						modeEntityList.addAll(new Gson().fromJson(json, new TypeToken<List<ModeEntity>>() {}.getType()));
					} catch (Throwable throwable) {
						Logger.e(TAG, "MyApplication#initModeListData error- ", throwable);
					}
					if (modeEntityList.isEmpty()) {
						Collections.addAll(modeEntityList, Constant.MODE_ENTITY_LIST);
					}
					return modeEntityList;
				})
				.observeOn(AndroidSchedulers.mainThread())
				.onErrorReturn(new Function<Throwable, List<ModeEntity>>() {
					@Override
					public List<ModeEntity> apply(Throwable throwable) throws Throwable {
						Logger.w(TAG, "MyApplication#initModeListData error- ", throwable);
						List<ModeEntity> modeEntityList = new ArrayList<>();
						Collections.addAll(modeEntityList, Constant.MODE_ENTITY_LIST);
						return modeEntityList;
					}
				})
				.subscribe(Runtime::setModeList);
	}
}
