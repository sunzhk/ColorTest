package com.sunzk.colortest

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import com.google.gson.Gson
import com.sunzk.base.utils.Logger
import com.sunzk.colortest.entity.ModeEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

object Runtime {
    private const val TAG = "Runtime"
    var testResultNumber = 0
    var isNeedBGM = false

    var modeList: List<ModeEntity>
        get() = modeEntities
        set(modeEntities) {
            Runtime.modeEntities.clear()
            Runtime.modeEntities.addAll(modeEntities)
        }

    private val modeEntities: MutableList<ModeEntity> =
        ArrayList()
    private var modeEntitiesWriterDisposable: Disposable? = null

    fun writeModeListToDataStore() {
        if (modeEntitiesWriterDisposable != null) {
            modeEntitiesWriterDisposable!!.dispose()
            modeEntitiesWriterDisposable = null
        }
        val dataStore =
            RxPreferenceDataStoreBuilder(
                context = MyApplication.instance!!, 
                name = Constant.MODE_SELECT_DATA_NAME
            ).build()
        modeEntitiesWriterDisposable =
            dataStore.updateDataAsync(
                Function { preferences: Preferences ->
                    val mutablePreferences = preferences.toMutablePreferences()
                    val key =
                        stringPreferencesKey(Constant.MODE_SELECT_DATA_KEY)
                    mutablePreferences.set(
                        key,
                        Gson().toJson(modeEntities)
                    )
                    Single.just<Preferences>(
                        mutablePreferences
                    )
                }
            )
                .subscribeOn(Schedulers.io())
                .subscribe { preferences: Preferences?, throwable: Throwable? ->
                    throwable?.let {
                        Logger.w(TAG, "Runtime#writeModeListToDataStore- ", throwable)
                    }
                }
    }
}