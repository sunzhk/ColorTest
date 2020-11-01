package com.sunzk.colortest;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.sunzk.colortest.activity.GuessColorActivity;
import com.sunzk.colortest.activity.MockColorActivity;
import com.sunzk.colortest.activity.ModeSelectActivity;

import java.util.Calendar;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().getDecorView().postDelayed(this::goToTestActivity,2000);
	}

	private void goToTestActivity() {
		Intent intent = new Intent(this, ModeSelectActivity.class);
		startActivity(intent);
		this.finish();
	}
}
