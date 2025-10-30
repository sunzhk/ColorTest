package com.sunzk.colortest

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDex
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
        AppUtils.setLogEnable(BuildConfig.DEBUG)
        Runtime.initConfig(this)
        startBGMService()
    }

    private fun startBGMService() {
        startService(Intent(this, BGMService::class.java))
    }
}