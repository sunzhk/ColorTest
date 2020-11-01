package com.sunzk.colortest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sunzk.colortest.BaseActivity;
import com.sunzk.colortest.R;
import com.sunzk.colortest.Runtime;
import com.sunzk.colortest.utils.AppUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import static com.sunzk.colortest.activity.GuessColorActivity.INTENT_KEY_DIFFICULTY;

public class ModeSelectActivity extends BaseActivity {

	private static final String TAG = "ModeSelectActivity";
	private CheckBox cbBgmSwitch;
	private TextView tvBgmSwitch;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!checkAccess()) {
			showAlertDialog();
			return;
		}

		showVersionUpgradeDialog();

		setContentView(R.layout.activity_mode_select);
		findViewById(R.id.activity_mode_select_bt_standard).setOnClickListener(v -> {
			Intent intent = new Intent(this, MockColorActivity.class);
			startActivity(intent);
		});
		findViewById(R.id.activity_mode_select_bt_hell1).setOnClickListener(v -> {
			Intent intent = new Intent(this, GuessColorActivity.class);
			intent.putExtra(INTENT_KEY_DIFFICULTY, GuessColorActivity.Difficulty.EASY);
			startActivity(intent);
		});
		findViewById(R.id.activity_mode_select_bt_hell2).setOnClickListener(v -> {
			Intent intent = new Intent(this, GuessColorActivity.class);
			intent.putExtra(INTENT_KEY_DIFFICULTY, GuessColorActivity.Difficulty.MEDIUM);
			startActivity(intent);
		});
		findViewById(R.id.activity_mode_select_bt_hell3).setOnClickListener(v -> {
			Intent intent = new Intent(this, GuessColorActivity.class);
			intent.putExtra(INTENT_KEY_DIFFICULTY, GuessColorActivity.Difficulty.DIFFICULT);
			startActivity(intent);
		});

		cbBgmSwitch = findViewById(R.id.activity_mode_select_cb_bgm_switch);
		tvBgmSwitch = findViewById(R.id.activity_mode_select_tv_bgm_switch);

		resetBgmSwitchState(Runtime.isNeedBGM());

		cbBgmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			resetBgmSwitchState(isChecked);
			Runtime.setNeedBGM(isChecked);
		});
	}
	
	private void resetBgmSwitchState(boolean switchOn) {
		cbBgmSwitch.setChecked(switchOn);
		tvBgmSwitch.setText(switchOn ? R.string.bgm_switch_on : R.string.bgm_switch_off);
	}

	private boolean checkAccess() {
//		Date dateNow = new Date(System.currentTimeMillis());
//		Calendar compare = Calendar.getInstance();
//		compare.set(2021, 9, 18, 0, 0);
//		Date dateCompare = compare.getTime();
//		Log.d(TAG, "checkAccess: " + dateNow + " , " + dateCompare);
//		return dateNow.before(dateCompare);
		return true;
	}

	/**
	 * 测试版本的情况下，显示提示框
	 */
	private void showAlertDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("过期啦").setMessage("本程序为试用版，请联系开发者获取最新版本\nmail:312797831@qq.com").setPositiveButton("好吧", (dialog, which) -> ModeSelectActivity.this.finish()).create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
	}

	private void showVersionUpgradeDialog() {
		SharedPreferences sharedPreferences = getSharedPreferences("versionHint", MODE_PRIVATE);
		int versionCode = AppUtils.getVersionCode(this);
		int lastHintVersionCode = sharedPreferences.getInt("lastHintVersionCode", -1);
		if (versionCode > lastHintVersionCode) {
			new AlertDialog.Builder(this).setTitle("v" + AppUtils.getVersionName(this)).setMessage("更新说明:\n    已更新至正式版，可以尽情使用了\n\n    design by 寂书予\n    develop by 作死菌").setPositiveButton("知道了", (dialog, which) -> {
				dialog.dismiss();
			}).create().show();
		}
		sharedPreferences.edit().putInt("lastHintVersionCode", versionCode).apply();
	}

}
