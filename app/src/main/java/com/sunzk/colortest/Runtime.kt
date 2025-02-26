package com.sunzk.colortest

import android.app.Application.UI_MODE_SERVICE
import android.app.UiModeManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.arcsoft.closeli.utils.takeIfIs
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.entity.ModeEntity
import com.sunzk.colortest.view.colorPicker.ColorPickerType
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

    fun initConfig(context: Context) {
        val sp = context.getSharedPreferences(Constant.SP_NAME_APP, Context.MODE_PRIVATE)
        initBGMSwitch(sp)
        initDarkMode(sp)
        initColorPickerType(sp)
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
    
    fun switchNextDarkMode(): DarkMode {
        val nextMode = DarkMode.entries.getOrElse(darkMode.ordinal + 1) { DarkMode.entries[0] }
        darkMode = nextMode
        return nextMode
    }

    private fun initDarkMode(sharedPreferences: SharedPreferences) {
        val nightModeSetting = sharedPreferences.getInt(Constant.SP_KEY_NIGHT_MODE, DarkMode.FollowSystem.ordinal)
        val darkMode = DarkMode.entries.getOrElse(nightModeSetting) { DarkMode.FollowSystem }
        Runtime.darkMode = darkMode
    }

    // </editor-fold>
    
    // <editor-fold desc="取色器">
    private val _colorPickerType = MutableStateFlow(ColorPickerType.HSBProgress)
    val colorPickerType: StateFlow<ColorPickerType> = _colorPickerType
    
    private fun initColorPickerType(sharedPreferences: SharedPreferences) {
        val typeOrdinal = sharedPreferences.getInt(Constant.SP_KEY_COLOR_PICKER_TYPE, ColorPickerType.HSBProgress.ordinal)
        val type = ColorPickerType.entries.getOrElse(typeOrdinal) { ColorPickerType.HSBProgress }
        _colorPickerType.emitBy(type)
    }
    
    fun switchNextColorPickerType(): ColorPickerType {
        val next = ColorPickerType.entries.getOrElse(_colorPickerType.value.ordinal + 1) { ColorPickerType.entries[0] }
        selectColorPickerType(next)
        return next
    }

    fun selectColorPickerType(type: ColorPickerType) {
        MyApplication.instance?.sharedPreferences?.edit()?.let { sp ->
            sp.putInt(Constant.SP_KEY_COLOR_PICKER_TYPE, type.ordinal)?.apply()
            _colorPickerType.emitBy(type)
        }
    }
    
    // </editor-fold>
}