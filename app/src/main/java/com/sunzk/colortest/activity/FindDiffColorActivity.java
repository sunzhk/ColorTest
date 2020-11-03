package com.sunzk.colortest.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunzk.colortest.BaseActivity;
import com.sunzk.colortest.R;
import com.sunzk.colortest.entity.HSB;
import com.sunzk.colortest.utils.ColorUtils;
import com.sunzk.colortest.view.FindDiffView;
import com.sunzk.colortest.view.OnDiffColorViewClickListener;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

public class FindDiffColorActivity extends BaseActivity {

	private static final String TAG = "FindDiffColorActivity";

	private static final int MIN_LEVEL = 1;
	private static final int DEFAULT_LEVEL = 3;
	private static final int MAX_LEVEL = 10;

	private RelativeLayout flContainer;
	private Button btLightSwitch;
	private FindDiffView findDiffView;
	
	private TextView tvHint;

	private ImageView ivLevelLeft;
	private TextView tvLevel;
	private ImageView ivLevelRight;
	private int currentLevel = DEFAULT_LEVEL;

	private boolean isLight = true;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_diff_color);
		flContainer = findViewById(R.id.activity_find_diff_color_fl_container);
		findDiffView = findViewById(R.id.activity_find_diff_color_fd);
		tvHint = findViewById(R.id.activity_find_diff_color_tv_hint);
		
		initControllerView();
		
		
		findViewById(R.id.activity_find_diff_color_bt_change).setOnClickListener(v -> {
			resetColor();
		});

		btLightSwitch = findViewById(R.id.activity_find_diff_color_bt_light_switch);

		switchLight(false);
		btLightSwitch.setOnClickListener(v -> {
			switchLight(!isLight);
		});

		findDiffView.setOnDiffColorViewClickListener(new OnDiffColorViewClickListener() {
			@Override
			public void onClick(View view, boolean result) {
				Log.d(TAG, "onClick: " + result);
				showResult(result);
			}
		});

		resetLevel(DEFAULT_LEVEL);
	}

	private void initControllerView() {
		ivLevelLeft = findViewById(R.id.activity_find_diff_color_iv_level_left);
		tvLevel = findViewById(R.id.activity_find_diff_color_tv_level);
		ivLevelRight = findViewById(R.id.activity_find_diff_color_iv_level_right);
		ivLevelLeft.setOnClickListener(v -> {
			if (currentLevel > MIN_LEVEL) {
				resetLevel(currentLevel - 1);
			}
		});
		ivLevelRight.setOnClickListener(v -> {
			if (currentLevel < MAX_LEVEL) {
				resetLevel(currentLevel + 1);
			}
		});
	}

	private void resetLevel(@IntRange(from = 1, to = 10) int level) {
		Log.d(TAG, "resetLevel: " + level);
		currentLevel = level;
		tvLevel.setText(String.valueOf(level));
		resetDifficulty(getCountPerSide());
	}
	
	private void switchLight(boolean on) {
		isLight = on;
		if (on) {
			flContainer.setBackgroundResource(R.color.bg_activity_find_diff_color_light);
			btLightSwitch.setText(R.string.light_switch_text_turn_off);
			tvLevel.setTextColor(getResources().getColor(R.color.text_default_light));
			findDiffView.setResultStrokeColor(getResources().getColor(R.color.text_default_light));
		} else {
			flContainer.setBackgroundResource(R.color.bg_activity_find_diff_color_dark);
			btLightSwitch.setText(R.string.light_switch_text_turn_on);
			tvLevel.setTextColor(getResources().getColor(R.color.text_default_dark));
			findDiffView.setResultStrokeColor(getResources().getColor(R.color.text_default_dark));
		}
	}

	private void resetDifficulty(int countPerSide) {
		Log.d(TAG, "resetDifficulty: " + countPerSide);
		findDiffView.resetCount(countPerSide);
		resetColor();
	}

	private void resetColor() {
		float[] baseColor = ColorUtils.randomHSBColor(0f, 0f, 0.2f);
		float colorDiff = getColorDiff();
		Log.d(TAG, "resetColor-colorDiff: " + colorDiff);
		float diffSmall = colorDiff / 20;
		float diffS = baseColor[1] > 0.5f ? baseColor[1] - diffSmall : baseColor[1] + diffSmall;
//		if (diffS > 1.0f) {
//			diffS = baseColor[1] - diffSmall;
//		}
//		float diffS = baseColor[1];
		float diffB = baseColor[2] > 0.6f ? baseColor[2] - colorDiff : baseColor[2] + colorDiff;
//		if (diffB > 1.0f) {
//			diffB = baseColor[2] - colorDiff;
//		}
		float[] diffColor = new float[]{baseColor[0], diffS, diffB};
		findDiffView.resetColor(new HSB(baseColor), new HSB(diffColor));
	}

	private void showResult(boolean result) {
//		tvHint.setText(result ? "猜中啦！" : "猜错啦！");
		if (result) {
			findDiffView.showResult();
//			new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
//					.setTitle("被发现啦").setMessage("厉害呀~这都能找到~")
//					.setPositiveButton("继续", (dialog, which) -> dialog.cancel())
//					.setOnCancelListener(dialog -> resetColor())
//					.create().show();
		}
	}

	private int getCountPerSide() {
		return currentLevel + 2;
	}
	
	private float getColorDiff() {
		Log.d(TAG, "getColorDiff: " + currentLevel + " - " + (0.3f / (currentLevel * 10)));
		return 0.02f + 0.5f / (currentLevel * 10);
	}

	@Override
	protected boolean needBGM() {
		return true;
	}
}
