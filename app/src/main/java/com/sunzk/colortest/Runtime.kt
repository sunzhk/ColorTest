package com.sunzk.colortest

import android.app.Application.UI_MODE_SERVICE
import android.app.UiModeManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import com.arcsoft.closeli.utils.takeIfIs
import com.google.gson.Gson
import com.sunzk.base.expand.emitBy
import com.sunzk.base.utils.Logger
import com.sunzk.colortest.entity.ModeEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

object Runtime {
    private const val TAG = "Runtime"
    var testResultNumber = 0

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
    
    fun initConfig(context: Context) {
        val sp = context.getSharedPreferences(Constant.SP_NAME_APP, Context.MODE_PRIVATE)
        initBGMSwitch(sp)
        initDarkMode(sp)
    }

    // <editor-fold desc="背景音乐">
    val _globalBGMSwitch = MutableStateFlow(false)
    val globalBGMSwitch : StateFlow<Boolean> = _globalBGMSwitch
    
    fun toggleGlobalBGMSwitch() {
        val value = !_globalBGMSwitch.value
        _globalBGMSwitch.emitBy(value)
        MyApplication.instance?.sharedPreferences?.edit()?.putBoolean(Constant.SP_KEY_BGM, value)?.apply()
    }
    
    private fun initBGMSwitch(sharedPreferences: SharedPreferences) {
        _globalBGMSwitch.emitBy(sharedPreferences.getBoolean(Constant.SP_KEY_BGM, false))
    }
    
    // </editor-fold>
    
    // <editor-fold desc="夜间模式">
    enum class DarkMode(val text: String, val valueBlow31: Int, val value: Int) {
        FollowSystem("跟随系统", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, UiModeManager.MODE_NIGHT_AUTO),
        Dark("已打开", AppCompatDelegate.MODE_NIGHT_YES, UiModeManager.MODE_NIGHT_YES),
        Light("已关闭", AppCompatDelegate.MODE_NIGHT_NO, UiModeManager.MODE_NIGHT_NO)
    }

    var darkMode = DarkMode.FollowSystem
        set(value) {
            val application = MyApplication.instance ?: return
            field = value
            if (Build.VERSION.SDK_INT >= 31) {
                application.getSystemService(UI_MODE_SERVICE).takeIfIs<UiModeManager>()
                    ?.setApplicationNightMode(value.value)
            } else {
                AppCompatDelegate.setDefaultNightMode(value.valueBlow31)
            }
            application.sharedPreferences.edit().putInt(Constant.SP_KEY_NIGHT_MODE, value.ordinal).apply()
        }
    
    fun nextDarkMode() {
        darkMode = DarkMode.entries.getOrElse(darkMode.ordinal + 1) { DarkMode.entries[0] }
    }

    private fun initDarkMode(sharedPreferences: SharedPreferences) {
        val nightModeSetting = sharedPreferences.getInt(Constant.SP_KEY_NIGHT_MODE, DarkMode.FollowSystem.ordinal)
        val darkMode = DarkMode.entries.getOrElse(nightModeSetting) { DarkMode.FollowSystem }
        Runtime.darkMode = darkMode
    }

    // </editor-fold>
}