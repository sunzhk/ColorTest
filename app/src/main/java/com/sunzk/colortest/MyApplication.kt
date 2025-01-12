package com.sunzk.colortest

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.arcsoft.closeli.utils.takeIfIs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunzk.base.utils.AppUtils
import com.sunzk.base.utils.Logger
import com.sunzk.colortest.Runtime.DarkMode
import com.sunzk.colortest.entity.ModeEntity
import com.sunzk.colortest.service.BGMService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class MyApplication : Application() {

    companion object {
        private const val TAG = "MyApplication"
        var instance: MyApplication? = null
            private set
    }

    val sharedPreferences by lazy { getSharedPreferences(Constant.SP_NAME_APP, Context.MODE_PRIVATE) }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this)
        AppUtils.setLogEnable(BuildConfig.DEBUG)
//        initModeListData()
        Runtime.initConfig(this)
        startBGMService()
    }

    private fun initModeListData() {
        Log.d(TAG, "initModeListData: ${Gson().toJson(Constant.MODE_ENTITY_LIST)}")
        val dataStore =
            RxPreferenceDataStoreBuilder(
                applicationContext,
                Constant.MODE_SELECT_DATA_NAME
            ).build()
        dataStore.data()
            .map { preferences: Preferences ->
	            preferences[stringPreferencesKey(Constant.MODE_SELECT_DATA_KEY)].toString()
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
    
    private fun startBGMService() {
        startService(Intent(this, BGMService::class.java))
    }
}