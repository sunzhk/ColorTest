package com.sunzk.colortest.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.sunzk.base.expand.collect
import com.sunzk.base.expand.coroutines.GlobalMainScope
import com.sunzk.colortest.R
import kotlinx.coroutines.flow.MutableStateFlow

class BGMService: Service() {
	companion object {
		private const val TAG: String = "BGMService"
		val bgmSwitch = MutableStateFlow(false)
	}

	private var mediaPlayer: MediaPlayer? = null

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	override fun onCreate() {
		super.onCreate()
		bgmSwitch.collect(GlobalMainScope) {
			if (it) {
				if (mediaPlayer == null) {
					mediaPlayer = MediaPlayer.create(this@BGMService, R.raw.bgm).apply {
						setVolume(0.3f, 0.3f)
						isLooping = true
					}
				}
				mediaPlayer?.start()
			} else {
				mediaPlayer?.pause()
			}
		}
	}
	
}