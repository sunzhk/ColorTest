package com.sunzk.colortest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunzk.colortest.BaseActivity;
import com.sunzk.colortest.Constant;
import com.sunzk.colortest.R;
import com.sunzk.colortest.Runtime;
import com.sunzk.colortest.databinding.ActivityModeSelectBinding;
import com.sunzk.colortest.databinding.ItemModeBinding;
import com.sunzk.colortest.entity.ModeEntity;
import com.sunzk.base.utils.AppUtils;
import com.sunzk.base.utils.DisplayUtil;
import com.sunzk.base.utils.Logger;
import com.sunzk.colortest.view.DragSwipeCallback;
import com.sunzk.colortest.view.IDragSwipe;
import com.sunzk.colortest.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sunzk.colortest.activity.GuessColorActivity.INTENT_KEY_DIFFICULTY;

public class ModeSelectActivity extends BaseActivity {

	private static final String TAG = "ModeSelectActivity";
	
	private ActivityModeSelectBinding viewBinding;
	
	private List<ModeEntity> modeEntityList = new ArrayList<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!checkAccess()) {
			showAlertDialog();
			return;
		}

		showVersionUpgradeDialog();
		viewBinding = ActivityModeSelectBinding.inflate(getLayoutInflater());
		setContentView(viewBinding.getRoot());
		initModeList();

		resetBgmSwitchState(Runtime.isNeedBGM());

		viewBinding.cbBgmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			resetBgmSwitchState(isChecked);
			Runtime.setNeedBGM(isChecked);
		});
	}
	
	private void initModeList() {
		modeEntityList.addAll(Runtime.getModeList());

		viewBinding.rvModeList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		int itemDecoration = DisplayUtil.dip2px(this, 6);
		viewBinding.rvModeList.addItemDecoration(new SpaceItemDecoration(0, itemDecoration, 0, itemDecoration));
		RecyclerView.Adapter<ModeViewHolder> adapter = new RecyclerView.Adapter<ModeViewHolder>() {
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
		};
		viewBinding.rvModeList.setAdapter(adapter);

		ItemTouchHelper.Callback callback = new DragSwipeCallback(new IDragSwipe() {
			@Override
			public void onItemSwapped(int fromPosition, int toPosition) {
				Logger.d(TAG, "ModeSelectActivity#onItemSwapped- ", fromPosition, toPosition);
				if (fromPosition < toPosition) {
					for (int i = fromPosition; i < toPosition; i++) {
						Collections.swap(modeEntityList, i, i + 1);
					}
				} else {
					for (int i = fromPosition; i > toPosition; i--) {
						Collections.swap(modeEntityList, i, i - 1);
					}
				}
				
				adapter.notifyItemMoved(fromPosition, toPosition);
				refreshModeEntityListInDataStore();
			}

			@Override
			public void onItemDeleted(int position) {
				Logger.d(TAG, "ModeSelectActivity#onItemDeleted- ", position);
			}

			@Override
			public void onItemDone(int position) {
				Logger.d(TAG, "ModeSelectActivity#onItemDone- ", position);
				modeEntityList.remove(position);
				adapter.notifyItemRemoved(position);
				refreshModeEntityListInDataStore();
			}
			
			private void refreshModeEntityListInDataStore() {
				Runtime.setModeList(modeEntityList);
				Runtime.writeModeListToDataStore();
			}
		});
		ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
		touchHelper.attachToRecyclerView(viewBinding.rvModeList);
	}
	
	private void resetBgmSwitchState(boolean switchOn) {
		viewBinding.cbBgmSwitch.setChecked(switchOn);
		viewBinding.tvBgmSwitch.setText(switchOn ? R.string.bgm_switch_on : R.string.bgm_switch_off);
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
	
	class ModeViewHolder extends RecyclerView.ViewHolder {

		private final ItemModeBinding itemViewBinding;

		public ModeViewHolder(@NonNull View itemView) {
			super(itemView);
			itemViewBinding = ItemModeBinding.bind(itemView);
		}
		
		public void bindData(ModeEntity modeEntity) {
			itemViewBinding.tvTitle.setText(modeEntity.getTitle());
			itemView.setOnClickListener(v -> startActivity(modeEntity.getIntent()));
		}
	}

}
