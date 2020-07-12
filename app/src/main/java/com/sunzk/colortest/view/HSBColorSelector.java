package com.sunzk.colortest.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.sunzk.colortest.R;
import com.sunzk.colortest.utils.ColorUtils;
import com.sunzk.colortest.utils.DisplayUtil;
import com.sunzk.colortest.utils.NumberUtils;

import java.util.Locale;

import androidx.annotation.Nullable;

public class HSBColorSelector extends LinearLayout {
	
	private static final String TAG = "HSBColorSelector";
	
	private GradientDrawable hueGradientDrawable;
	private GradientDrawable saturationGradientDrawable;
	private GradientDrawable valueGradientDrawable;

	private SeekBar sbH;
	private EditText tvH;
	private SeekBar sbS;
	private EditText tvS;
	private SeekBar sbB;
	private EditText tvB;
	
	private OnColorSelectedListener onColorSelectedListener;
	
	/* ---------------- 构造方法开始 ---------------- */
	public HSBColorSelector(Context context) {
		super(context);
		init();
	}

	public HSBColorSelector(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HSBColorSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public HSBColorSelector(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	/* ---------------- 构造方法结束 ---------------- */
	
	private void init() {
		this.setOrientation(LinearLayout.VERTICAL);
		LayoutInflater.from(getContext()).inflate(R.layout.merge_hsb_color_selector, this);
		sbH = findViewById(R.id.activity_mock_color_sb_h);
		sbS = findViewById(R.id.activity_mock_color_sb_s);
		sbB = findViewById(R.id.activity_mock_color_sb_b);
		tvH = findViewById(R.id.activity_mock_color_tv_h);
		tvS = findViewById(R.id.activity_mock_color_tv_s);
		tvB = findViewById(R.id.activity_mock_color_tv_b);
		initSeekBarArea();
	}

	private void initSeekBarArea() {
		int strokeWidth = DisplayUtil.dip2px(getContext(), 1);
		
		hueGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, ColorUtils.HUE_COLOR_LIST);
		float dimension = getResources().getDimension(R.dimen.view_hsb_color_selector_seek_bar_height);
		hueGradientDrawable.setCornerRadius(dimension / 2);
		hueGradientDrawable.setStroke(strokeWidth, Color.BLACK);
		sbH.setProgressDrawable(hueGradientDrawable);

		saturationGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {0xFF000000, 0xFF000000});
		saturationGradientDrawable.setCornerRadius(dimension / 2);
		saturationGradientDrawable.setStroke(strokeWidth, Color.BLACK);
		sbS.setProgressDrawable(saturationGradientDrawable);
		
		valueGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {0xFF000000, 0xFF000000});
		valueGradientDrawable.setCornerRadius(dimension / 2);
		valueGradientDrawable.setStroke(strokeWidth, Color.BLACK);
		sbB.setProgressDrawable(valueGradientDrawable);
		
		tvH.setOnEditorActionListener((v, actionId, event) -> {
			int value = NumberUtils.parse(v.getText().toString(), -1);
			if (value < 0) {
				value = 0;
			} else if (value > sbH.getMax()) {
				value = sbH.getMax();
			}
			sbH.setProgress(value);
			return false;
		});
		tvS.setOnEditorActionListener((v, actionId, event) -> {
			int value = NumberUtils.parse(v.getText().toString(), -1);
			if (value < 0) {
				value = 0;
			} else if (value > sbS.getMax()) {
				value = sbS.getMax();
			}
			sbS.setProgress(value);
			return false;
		});
		tvB.setOnEditorActionListener((v, actionId, event) -> {
			int value = NumberUtils.parse(v.getText().toString(), -1);
			if (value < 0) {
				value = 0;
			} else if (value > sbB.getMax()) {
				value = sbB.getMax();
			}
			sbB.setProgress(value);
			return false;
		});

		sbH.setMax(360);
		sbS.setMax(100);
		sbB.setMax(100);

		SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				resetResultColor();
				resetSeekBarProgressBackground();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		};
		sbH.setOnSeekBarChangeListener(onSeekBarChangeListener);
		sbS.setOnSeekBarChangeListener(onSeekBarChangeListener);
		sbB.setOnSeekBarChangeListener(onSeekBarChangeListener);
	}

	private void resetResultColor() {
		float h = sbH.getProgress() * 1.0f;
		float s = sbS.getProgress() * 1.0f / 100;
		float b = sbB.getProgress() * 1.0f / 100;
		int showH = (int) h;
		int showS = (int) (s * 100);
		int showB = (int) (b * 100);
		tvH.setText(String.format(Locale.getDefault(), "%d", showH));
		tvS.setText(String.format(Locale.getDefault(), "%d", showS));
		tvB.setText(String.format(Locale.getDefault(), "%d", showB));
		int color = Color.HSVToColor(new float[]{h, s, b});
		Log.d(TAG, "resetResultColor: " + h + "," + s + "," + b + "->" + Integer.toHexString(color));
		if (onColorSelectedListener != null) {
			onColorSelectedListener.onColorSelected(h, s, b);
		}
	}
	
	private void resetSeekBarProgressBackground() {
//		float[] selectColorHue = new float[] {getColorH(), 1.0f, 1.0f};
		
		float[] sStartColor = new float[] {getColorH(), 0.0f, getColorB()};
		float[] sEndColor = new float[] {getColorH(), 1.0f, getColorB()};

		float[] bStartColor = new float[3];
		Color.colorToHSV(0xFF000000, bStartColor);
		float[] bEndColor = new float[] {getColorH(), getColorS(), 1.0f};

		saturationGradientDrawable.setColors(new int[] {Color.HSVToColor(sStartColor), Color.HSVToColor(sEndColor)});
		valueGradientDrawable.setColors(new int[] {Color.HSVToColor(bStartColor), Color.HSVToColor(bEndColor)});
	}
	
	public int getColorRGB() {
		return Color.HSVToColor(new float[] {getColorH(), getColorS(), getColorB()});
	}

	public float getColorH() {
		return sbH.getProgress();
	}
	
	public float getColorS() {
		return sbS.getProgress() * 1.0f / 100;
	}
	
	public float getColorB() {
		return sbB.getProgress() * 1.0f / 100;
	}

	public int getProgressH() {
		return sbH.getProgress();
	}

	public int getProgressS() {
		return sbS.getProgress();
	}

	public int getProgressB() {
		return sbB.getProgress();
	}
	
	public void reset() {
		reset(0);
	}
	
	public void reset(int percent) {
		if (percent < 0 || percent > 100) {
			return;
		}
		sbH.setProgress(sbH.getMax() * percent / 100);
		sbS.setProgress(sbS.getMax() * percent / 100);
		sbB.setProgress(sbB.getMax() * percent / 100);
	}

	public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
		this.onColorSelectedListener = onColorSelectedListener;
	}

	@Override
	public void setEnabled(boolean enabled) {
		sbH.setEnabled(enabled);
		sbH.setClickable(enabled);
		sbH.setFocusable(enabled);
		
		sbS.setEnabled(enabled);
		sbS.setClickable(enabled);
		sbS.setFocusable(enabled);
		
		sbB.setEnabled(enabled);
		sbB.setClickable(enabled);
		sbB.setFocusable(enabled);

		tvH.setEnabled(enabled);
		tvS.setEnabled(enabled);
		tvB.setEnabled(enabled);
	}

	public interface OnColorSelectedListener {
		void onColorSelected(float h, float s, float b);
	}
}
