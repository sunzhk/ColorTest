package com.sunzk.base.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava3.RxDataStore
import com.sunzk.base.utils.Logger.w
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function
import java.util.*

class DataStoreHelper(context: Context?, name: String?) {
    private val dataStore: RxDataStore<Preferences>
    val data: Flowable<Preferences>
        get() = dataStore.data()

    fun saveData(data: HashMap<String, Any>) {
        val preferencesSingle = dataStore.updateDataAsync(Function { preferences ->
            val mutablePreferences = preferences.toMutablePreferences()
            var value: Any
            for ((key, value1) in data) {
                try {
                    addKeyValue(key, value1, mutablePreferences)
                } catch (throwable: Throwable) {
                    Logger.w(TAG, "DataStoreHelper#apply- ", throwable)
                }
            }
            Single.just(mutablePreferences)
        })
        preferencesSingle.subscribe()
    }

    private fun addKeyValue(keyName: String, value: Any, mutablePreferences: MutablePreferences) {
        if (value is Int) {
            val key: Preferences.Key<Int> = intPreferencesKey(keyName)
            mutablePreferences.set(key, value)
        } else if (value is Long) {
            val key: Preferences.Key<Long> = longPreferencesKey(keyName)
            mutablePreferences.set(key, value)
        } else if (value is Float) {
            val key: Preferences.Key<Float> = floatPreferencesKey(keyName)
            mutablePreferences.set(key, value)
        } else if (value is Double) {
            val key: Preferences.Key<Double> = doublePreferencesKey(keyName)
            mutablePreferences.set(key, value)
        } else if (value is Boolean) {
            val key: Preferences.Key<Boolean> = booleanPreferencesKey(keyName)
            mutablePreferences.set(key, value)
        } else if (value is String) {
            val key: Preferences.Key<String> = stringPreferencesKey(keyName)
            mutablePreferences.set(key, value)
        } else {
            val key: Preferences.Key<String> = stringPreferencesKey(keyName)
            mutablePreferences.set(key, value.toString())
        }
    }

    companion object {
        private const val TAG = "DataStoreHelper"
    }

    init {
        dataStore = RxPreferenceDataStoreBuilder(context!!, name!!).build()
    }
}