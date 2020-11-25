package com.sunzk.colortest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sunzk.colortest.BaseActivity;
import com.sunzk.colortest.R;
import com.sunzk.colortest.Runtime;
import com.sunzk.colortest.utils.AppUtils;
import com.sunzk.colortest.utils.DisplayUtil;
import com.sunzk.colortest.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sunzk.colortest.activity.GuessColorActivity.INTENT_KEY_DIFFICULTY;

public class ModeSelectActivity extends BaseActivity {

	private static final String TAG = "ModeSelectActivity";
	
	private CheckBox cbBgmSwitch;
	private TextView tvBgmSwitch;
	
	private RecyclerView rvModeList;
	private List<ModeEntity> modeEntityList = new ArrayList<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!checkAccess()) {
			showAlertDialog();
			return;
		}

		showVersionUpgradeDialog();

		setContentView(R.layout.activity_mode_select);
		rvModeList = findViewById(R.id.activity_mode_select_rv_mode_list);
		initModeList();
		cbBgmSwitch = findViewById(R.id.activity_mode_select_cb_bgm_switch);
		tvBgmSwitch = findViewById(R.id.activity_mode_select_tv_bgm_switch);

		resetBgmSwitchState(Runtime.isNeedBGM());

		cbBgmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			resetBgmSwitchState(isChecked);
			Runtime.setNeedBGM(isChecked);
		});
	}
	
	private void initModeList() {
		modeEntityList.add(new ModeEntity("标准模式", new Intent(this, MockColorActivity.class)));
		Intent hellIntent1 = new Intent(this, GuessColorActivity.class);
		hellIntent1.putExtra(INTENT_KEY_DIFFICULTY, GuessColorActivity.Difficulty.EASY);
		modeEntityList.add(new ModeEntity("地狱模式(体验版)", hellIntent1));
//		Intent hellIntent2 = new Intent(this, GuessColorActivity.class);
//		hellIntent1.putExtra(INTENT_KEY_DIFFICULTY, GuessColorActivity.Difficulty.MEDIUM);
//		modeEntityList.add(new ModeEntity("地狱模式(中等)", hellIntent2));
//		Intent hellIntent3 = new Intent(this, GuessColorActivity.class);
//		hellIntent1.putExtra(INTENT_KEY_DIFFICULTY, GuessColorActivity.Difficulty.DIFFICULT);
//		modeEntityList.add(new ModeEntity("地狱模式(困难)", hellIntent3));
		modeEntityList.add(new ModeEntity("找不同", new Intent(this, FindDiffColorActivity.class)));
		modeEntityList.add(new ModeEntity("选图片", new Intent(this, SelectPicActivity.class)));

		rvModeList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		int itemDecoration = DisplayUtil.dip2px(this, 6);
		rvModeList.addItemDecoration(new SpaceItemDecoration(0, itemDecoration, 0, itemDecoration));
		rvModeList.setAdapter(new RecyclerView.Adapter<ModeViewHolder>() {
			@NonNull
			@Override
			public ModeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				View inflate = LayoutInflater.from(ModeSelectActivity.this).inflate(R.layout.item_mode, parent, false);
				return new ModeViewHolder(inflate);
			}

			@Override
			public void onBindViewHolder(@NonNull ModeViewHolder holder, int position) {
				holder.bindData(modeEntityList.get(position));
			}

			@Override
			public int getItemCount() {
				return modeEntityList.size();
			}
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
//		String upgradeMessage = "    已更新至正式版，可以尽情使用了";
		String upgradeMessage = "    1. 增加了背景音乐的开关，现在可以一边网抑云一边玩啦\n" +
								"    2. 颜色选择器增加了微调按钮\n" +
								"    3. 增加了找一找模式~\n" +
								"    再次感谢可爱的寂书予~";
		if (versionCode > lastHintVersionCode) {
			new AlertDialog.Builder(this)
					.setTitle("v" + AppUtils.getVersionName(this))
					.setMessage("更新说明:\n" + upgradeMessage + "\n\n    design by 寂书予\n    develop by 作死菌")
					.setPositiveButton("知道了", (dialog, which) -> {
						dialog.dismiss();
					}).create().show();
		}
		sharedPreferences.edit().putInt("lastHintVersionCode", versionCode).apply();
	}
	
	static class ModeEntity {
		private String title;
		private Intent intent;

		public ModeEntity(@NonNull String title, @NonNull Intent intent) {
			this.title = title;
			this.intent = intent;
		}
	}
	
	class ModeViewHolder extends RecyclerView.ViewHolder {

		private final TextView textView;

		public ModeViewHolder(@NonNull View itemView) {
			super(itemView);
			this.textView = itemView.findViewById(R.id.item_mode_select_tv_title);
		}
		
		public void bindData(ModeEntity modeEntity) {
			textView.setText(modeEntity.title);
			itemView.setOnClickListener(v -> {
				startActivity(modeEntity.intent);
			});
		}
	}

}
