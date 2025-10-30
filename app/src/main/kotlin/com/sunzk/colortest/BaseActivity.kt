package com.sunzk.colortest

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.sunzk.base.expand.collect
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.service.BGMService
import com.sunzk.colortest.tools.FloatingSettingWindowManager

open class BaseActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BaseActivity"
    }

    protected var needFloatingWindow = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 禁用Activity进出动画
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
	    } else {
            overridePendingTransition(0, 0)
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Runtime.globalBGMSwitch.collect(lifecycleScope) {
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                whenBGMSwitch(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Runtime.globalBGMSwitch.value && needBGM()) {
            playBGM()
        } else {
            pauseBGM()
        }
        if (needFloatingWindow) {
            FloatingSettingWindowManager.attach(this)
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (needFloatingWindow) {
            FloatingSettingWindowManager.detach(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    protected open fun needBGM(): Boolean {
        return false
    }

    private fun whenBGMSwitch(switchOn: Boolean) {
        Log.d(TAG, "BaseActivity#whenBGMSwitch- switchOn=$switchOn, needBGM=${needBGM()}")
        if (switchOn && needBGM()) {
            playBGM()
        } else {
            BGMService.bgmSwitch.emitBy(false)
        }
    }
    
    private fun playBGM() {
        Log.d(TAG, "BaseActivity#playBGM")
        BGMService.bgmSwitch.emitBy(true)
    }
    
    private fun pauseBGM() {
        Log.d(TAG, "BaseActivity#pauseBGM")
        BGMService.bgmSwitch.emitBy(false)
    }
}