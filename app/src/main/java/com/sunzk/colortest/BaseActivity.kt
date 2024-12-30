package com.sunzk.colortest

import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity

open class BaseActivity : FragmentActivity() {
    private var mediaPlayer: MediaPlayer? = null
    override fun onResume() {
        super.onResume()
        if (mediaPlayer == null) {
            if (Runtime.isNeedBGM && needBGM()) {
                playBGM()
            }
        } else {
            mediaPlayer!!.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer != null) {
            mediaPlayer!!.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }
    }

    protected open fun needBGM(): Boolean {
        return false
    }

    @get:RawRes
    protected val bGM: Int
        protected get() = R.raw.bgm

    private fun playBGM() {
        mediaPlayer = MediaPlayer.create(this, bGM)
        mediaPlayer!!.setVolume(0.5f, 0.5f)
        mediaPlayer!!.start()
        //是否循环播放
        mediaPlayer!!.setLooping(true)
    }

    companion object {
        private const val TAG = "BaseActivity"
    }
}