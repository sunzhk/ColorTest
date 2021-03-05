package com.sunzk.colortest;

import android.content.Intent;

import com.sunzk.colortest.activity.FindDiffColorActivity;
import com.sunzk.colortest.activity.GuessColorActivity;
import com.sunzk.colortest.activity.MockColorActivity;
import com.sunzk.colortest.activity.SelectPicActivity;
import com.sunzk.colortest.entity.ModeEntity;

import static com.sunzk.colortest.activity.GuessColorActivity.INTENT_KEY_DIFFICULTY;

public class Constant {
	
	public static final ModeEntity[] MODE_ENTITY_LIST;
	public static final String MODE_SELECT_DATA_NAME = "settings";
	public static final String MODE_SELECT_DATA_KEY = "modeList";
	
	static {
		MyApplication context = MyApplication.getInstance();
		
		Intent hellIntent1 = new Intent(context, GuessColorActivity.class);
		hellIntent1.putExtra(INTENT_KEY_DIFFICULTY, GuessColorActivity.Difficulty.EASY);
		Intent hellIntent2 = new Intent(context, GuessColorActivity.class);
		hellIntent1.putExtra(INTENT_KEY_DIFFICULTY, GuessColorActivity.Difficulty.MEDIUM);
		Intent hellIntent3 = new Intent(context, GuessColorActivity.class);
		hellIntent1.putExtra(INTENT_KEY_DIFFICULTY, GuessColorActivity.Difficulty.DIFFICULT);

		MODE_ENTITY_LIST = new ModeEntity[]{
			new ModeEntity("标准模式", new Intent(context, MockColorActivity.class)),
			new ModeEntity("地狱模式(体验版)", hellIntent1),
			new ModeEntity("地狱模式(中等)", hellIntent2), 
			new ModeEntity("地狱模式(困难)", hellIntent3),
			new ModeEntity("找不同", new Intent(context, FindDiffColorActivity.class)),
			new ModeEntity("选图片", new Intent(context, SelectPicActivity.class))
		};

	}
	
}
