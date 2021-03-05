package com.sunzk.base.utils;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;

public class DataStoreHelper {
	
	private static final String TAG = "DataStoreHelper";

	private RxDataStore<Preferences> dataStore;

	public DataStoreHelper(Context context, String name) {
		dataStore = new RxPreferenceDataStoreBuilder(context, name).build();
	}

	public Flowable<Preferences> getData() {
		return dataStore.data();
	}

	public void saveData(HashMap<String, Object> data) {
		Single<Preferences> preferencesSingle = dataStore.updateDataAsync(new Function<Preferences, Single<Preferences>>() {
			@Override
			public Single<Preferences> apply(Preferences preferences) throws Throwable {
				MutablePreferences mutablePreferences = preferences.toMutablePreferences();
				Object value;
				for (Map.Entry<String, Object> entry : data.entrySet()) {
					try {
						addKeyValue(entry.getKey(), entry.getValue(), mutablePreferences);
					} catch (Throwable throwable) {
						Logger.w(TAG, "DataStoreHelper#apply- ", throwable);
					}
				}
				return Single.just(mutablePreferences);
			}
		});
		preferencesSingle.subscribe();
	}
	
	private void addKeyValue(String keyName, Object value, MutablePreferences mutablePreferences) {
		if (value instanceof Integer) {
			Preferences.Key<Integer> key = PreferencesKeys.intKey(keyName);
			mutablePreferences.set(key, (Integer) value);
		} else if (value instanceof Long) {
			Preferences.Key<Long> key = PreferencesKeys.longKey(keyName);
			mutablePreferences.set(key, (Long) value);
		} else if (value instanceof Float) {
			Preferences.Key<Float> key = PreferencesKeys.floatKey(keyName);
			mutablePreferences.set(key, (Float) value);
		} else if (value instanceof Double) {
			Preferences.Key<Double> key = PreferencesKeys.doubleKey(keyName);
			mutablePreferences.set(key, (Double) value);
		} else if (value instanceof Boolean) {
			Preferences.Key<Boolean> key = PreferencesKeys.booleanKey(keyName);
			mutablePreferences.set(key, (Boolean) value);
		} else if (value instanceof String) {
			Preferences.Key<String> key = PreferencesKeys.stringKey(keyName);
			mutablePreferences.set(key, (String) value);
		} else {
			Preferences.Key<String> key = PreferencesKeys.stringKey(keyName);
			mutablePreferences.set(key, value.toString());
		}
	}

}
