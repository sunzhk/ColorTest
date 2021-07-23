package com.sunzk.colortest

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunzk.base.utils.AppUtils
import com.sunzk.base.utils.Logger
import com.sunzk.colortest.entity.ModeEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import kotlin.reflect.KProperty

class MyApplication : Application() {
    override fun onCreate() {
        instance = this
        super.onCreate()
        AppUtils.setLogEnable(BuildConfig.DEBUG)
        initModeListData()
    }

    private fun initModeListData() {
        val dataStore =
            RxPreferenceDataStoreBuilder(
                applicationContext,
                Constant.MODE_SELECT_DATA_NAME
            ).build()
        dataStore.data()
            .map { preferences: Preferences ->
                preferences[stringPreferencesKey(Constant.MODE_SELECT_DATA_KEY)]
            }
            .subscribeOn(Schedulers.io())
            .map<List<ModeEntity>> { json: String? ->
                val modeEntityList: MutableList<ModeEntity> = ArrayList()
                try {
                    modeEntityList.addAll(
                        Gson().fromJson(
                            json,
                            object :
                                TypeToken<List<ModeEntity>>() {}.type
                        )
                    )
                } catch (throwable: Throwable) {
                    Logger.e(
                        TAG,
                        "MyApplication#initModeListData error- ",
                        throwable
                    )
                }
                if (modeEntityList.isEmpty()) {
                    Collections.addAll(
                        modeEntityList,
                        *Constant.MODE_ENTITY_LIST
                    )
                }
                modeEntityList
            }
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { throwable ->
                Logger.w(
                    TAG,
                    "MyApplication#initModeListData error- ",
                    throwable
                )
                val modeEntityList: ArrayList<ModeEntity> = ArrayList()
                modeEntityList.addAll(Constant.MODE_ENTITY_LIST)
                modeEntityList
            }
            .subscribe { obj: List<ModeEntity>? -> Runtime.modeList = obj!! }
    }
    
    companion object {
        private const val TAG = "MyApplication"
        var instance: MyApplication? = null
            private set
    }
}