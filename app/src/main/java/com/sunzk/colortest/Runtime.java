package com.sunzk.colortest;

import com.google.gson.Gson;
import com.sunzk.colortest.entity.ModeEntity;
import com.sunzk.base.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Runtime {
	
	private static final String TAG = "Runtime";
	
	public static int testResultNumber = 0;
	
	private static boolean needBGM = false;

	public static boolean isNeedBGM() {
		return needBGM;
	}

	public static void setNeedBGM(boolean needBGM) {
		Runtime.needBGM = needBGM;
	}
	
	public static List<ModeEntity> getModeList() {
		return modeEntities;
	}
	
	private static List<ModeEntity> modeEntities = new ArrayList<>();
	private static Disposable modeEntitiesWriterDisposable;

	public static void setModeList(@NonNull List<ModeEntity> modeEntities) {
		Runtime.modeEntities.clear();
		Runtime.modeEntities.addAll(modeEntities);
	}
	
	public static void writeModeListToDataStore() {
		if (modeEntitiesWriterDisposable != null) {
			modeEntitiesWriterDisposable.dispose();
			modeEntitiesWriterDisposable = null;
		}
		RxDataStore<Preferences> dataStore = new RxPreferenceDataStoreBuilder(MyApplication.getInstance(), Constant.MODE_SELECT_DATA_NAME).build();
		modeEntitiesWriterDisposable = dataStore.updateDataAsync(preferences -> {
			MutablePreferences mutablePreferences = preferences.toMutablePreferences();
			Preferences.Key<String> key = PreferencesKeys.stringKey(Constant.MODE_SELECT_DATA_KEY);
			mutablePreferences.set(key, new Gson().toJson(Runtime.modeEntities));
			return Single.just(mutablePreferences);
		})
		.subscribeOn(Schedulers.io())
		.subscribe((preferences, throwable) -> Logger.w(TAG, "Runtime#writeModeListToDataStore- ", throwable));
	}
}
