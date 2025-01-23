package com.sunzk.colortest

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.sunzk.base.utils.AppUtils
import com.sunzk.colortest.service.BGMService

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

    private fun startBGMService() {
        startService(Intent(this, BGMService::class.java))
    }
}