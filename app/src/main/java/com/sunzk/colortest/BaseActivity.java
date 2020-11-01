package com.sunzk.colortest;

import android.media.MediaPlayer;

import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
	
	private static final String TAG = "BaseActivity";

	private MediaPlayer mediaPlayer;

	@Override
	protected void onResume() {
		super.onResume();
		if (mediaPlayer == null) {
			if (Runtime.isNeedBGM() && needBGM()) {
				playBGM();
			}
		} else {
			mediaPlayer.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
	}

	protected boolean needBGM() {
		return false;
	}
	
	@RawRes
	protected int getBGM() {
		return R.raw.bgm;
	}

	private void playBGM() {
		mediaPlayer = MediaPlayer.create(this, getBGM());
		mediaPlayer.setVolume(0.5f, 0.5f);
		mediaPlayer.start();
		//是否循环播放
		mediaPlayer.setLooping(true);
	}
	
}