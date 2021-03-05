package com.sunzk.colortest.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.sunzk.colortest.R;
import com.sunzk.colortest.databinding.MergeHsbColorSelectorBinding;
import com.sunzk.colortest.entity.HSB;
import com.sunzk.base.utils.ColorUtils;
import com.sunzk.base.utils.DisplayUtil;
import com.sunzk.base.utils.NumberUtils;

import java.util.Locale;

import androidx.annotation.Nullable;

public class HSBColorSelector extends LinearLayout {

	private static final String TAG = "HSBColorSelector";

	private GradientDrawable hueGradientDrawable;
	private GradientDrawable saturationGradientDrawable;
	private GradientDrawable valueGradientDrawable;

	private MergeHsbColorSelectorBinding viewBinding;
	
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
		viewBinding = MergeHsbColorSelectorBinding.inflate(LayoutInflater.from(getContext()), this);

		initSeekBarArea();
	}

	private void initSeekBarArea() {
		int strokeWidth = DisplayUtil.dip2px(getContext(), 1);

		hueGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, ColorUtils.HUE_COLOR_LIST);
		float dimension = getResources().getDimension(R.dimen.view_hsb_color_selector_seek_bar_height);
		hueGradientDrawable.setCornerRadius(dimension / 2);
		hueGradientDrawable.setStroke(strokeWidth, Color.BLACK);
		viewBinding.sbH.setProgressDrawable(hueGradientDrawable);

		saturationGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xFF000000, 0xFF000000});
		saturationGradientDrawable.setCornerRadius(dimension / 2);
		saturationGradientDrawable.setStroke(strokeWidth, Color.BLACK);
		viewBinding.sbS.setProgressDrawable(saturationGradientDrawable);

		valueGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xFF000000, 0xFF000000});
		valueGradientDrawable.setCornerRadius(dimension / 2);
		valueGradientDrawable.setStroke(strokeWidth, Color.BLACK);
		viewBinding.sbB.setProgressDrawable(valueGradientDrawable);

		viewBinding.tvH.setOnEditorActionListener((v, actionId, event) -> {
			int value = NumberUtils.parse(v.getText().toString(), -1);
			if (value < 0) {
				value = 0;
			} else if (value > viewBinding.sbH.getMax()) {
				value = viewBinding.sbH.getMax();
			}
			viewBinding.sbH.setProgress(value);
			return false;
		});
		viewBinding.tvS.setOnEditorActionListener((v, actionId, event) -> {
			int value = NumberUtils.parse(v.getText().toString(), -1);
			if (value < 0) {
				value = 0;
			} else if (value > viewBinding.sbS.getMax()) {
				value = viewBinding.sbS.getMax();
			}
			viewBinding.sbS.setProgress(value);
			return false;
		});
		viewBinding.tvB.setOnEditorActionListener((v, actionId, event) -> {
			int value = NumberUtils.parse(v.getText().toString(), -1);
			if (value < 0) {
				value = 0;
			} else if (value > viewBinding.sbB.getMax()) {
				value = viewBinding.sbB.getMax();
			}
			viewBinding.sbB.setProgress(value);
			return false;
		});

		viewBinding.sbH.setMax(360);
		viewBinding.sbS.setMax(100);
		viewBinding.sbB.setMax(100);

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
		viewBinding.sbH.setOnSeekBarChangeListener(onSeekBarChangeListener);
		viewBinding.sbS.setOnSeekBarChangeListener(onSeekBarChangeListener);
		viewBinding.sbB.setOnSeekBarChangeListener(onSeekBarChangeListener);

		viewBinding.ivHFineTuningLeft.setOnClickListener(v -> {
			int progress = viewBinding.sbH.getProgress();
			if (progress > 0) {
				viewBinding.sbH.setProgress(progress - 1);
			}
		});
		viewBinding.ivHFineTuningRight.setOnClickListener(v -> {
			int progress = viewBinding.sbH.getProgress();
			if (progress < viewBinding.sbH.getMax()) {
				viewBinding.sbH.setProgress(progress + 1);
			}
		});

		viewBinding.ivSFineTuningLeft.setOnClickListener(v -> {
			int progress = viewBinding.sbS.getProgress();
			if (progress > 0) {
				viewBinding.sbS.setProgress(progress - 1);
			}
		});
		viewBinding.ivSFineTuningRight.setOnClickListener(v -> {
			int progress = viewBinding.sbS.getProgress();
			if (progress < viewBinding.sbS.getMax()) {
				viewBinding.sbS.setProgress(progress + 1);
			}
		});

		viewBinding.ivBFineTuningLeft.setOnClickListener(v -> {
			int progress = viewBinding.sbB.getProgress();
			if (progress > 0) {
				viewBinding.sbB.setProgress(progress - 1);
			}
		});
		viewBinding.ivBFineTuningRight.setOnClickListener(v -> {
			int progress = viewBinding.sbB.getProgress();
			if (progress < viewBinding.sbB.getMax()) {
				viewBinding.sbB.setProgress(progress + 1);
			}
		});
	}

	private void resetResultColor() {
		float h = viewBinding.sbH.getProgress() * 1.0f;
		float s = viewBinding.sbS.getProgress() * 1.0f / 100;
		float b = viewBinding.sbB.getProgress() * 1.0f / 100;
		int showH = (int) h;
		int showS = (int) (s * 100);
		int showB = (int) (b * 100);
		viewBinding.tvH.setText(String.format(Locale.getDefault(), "%d", showH));
		viewBinding.tvS.setText(String.format(Locale.getDefault(), "%d", showS));
		viewBinding.tvB.setText(String.format(Locale.getDefault(), "%d", showB));
		int color = Color.HSVToColor(new float[]{h, s, b});
		Log.d(TAG, "resetResultColor: " + h + "," + s + "," + b + "->" + Integer.toHexString(color));
		if (onColorSelectedListener != null) {
			onColorSelectedListener.onColorSelected(h, s, b);
		}
	}

	private void resetSeekBarProgressBackground() {
//		float[] selectColorHue = new float[] {getColorH(), 1.0f, 1.0f};

		float[] sStartColor = new float[]{getColorH(), 0.0f, getColorB()};
		float[] sEndColor = new float[]{getColorH(), 1.0f, getColorB()};

		float[] bStartColor = new float[3];
		Color.colorToHSV(0xFF000000, bStartColor);
		float[] bEndColor = new float[]{getColorH(), getColorS(), 1.0f};

		saturationGradientDrawable.setColors(new int[]{Color.HSVToColor(sStartColor), Color.HSVToColor(sEndColor)});
		valueGradientDrawable.setColors(new int[]{Color.HSVToColor(bStartColor), Color.HSVToColor(bEndColor)});
	}

	public int getColorRGB() {
		return Color.HSVToColor(new float[]{getColorH(), getColorS(), getColorB()});
	}

	public void setColor(HSB color) {
		setColor(color.getH(), color.getS(), color.getB());
	}
	
	public void setColor(float h, float s, float b) {
		int showH = (int) h;
		int showS = (int) (s * 100);
		int showB = (int) (b * 100);
		viewBinding.tvH.setText(String.format(Locale.getDefault(), "%d", showH));
		viewBinding.tvS.setText(String.format(Locale.getDefault(), "%d", showS));
		viewBinding.tvB.setText(String.format(Locale.getDefault(), "%d", showB));
		
		viewBinding.sbH.setProgress(showH);
		viewBinding.sbS.setProgress(showS);
		viewBinding.sbB.setProgress(showB);

		if (onColorSelectedListener != null) {
			onColorSelectedListener.onColorSelected(h, s, b);
		}
	}

	public float getColorH() {
		return viewBinding.sbH.getProgress();
	}

	public float getColorS() {
		return viewBinding.sbS.getProgress() * 1.0f / 100;
	}

	public float getColorB() {
		return viewBinding.sbB.getProgress() * 1.0f / 100;
	}

	public int getProgressH() {
		return viewBinding.sbH.getProgress();
	}

	public int getProgressS() {
		return viewBinding.sbS.getProgress();
	}

	public int getProgressB() {
		return viewBinding.sbB.getProgress();
	}

	public void reset() {
		reset(0);
	}

	public void reset(int percent) {
		if (percent < 0 || percent > 100) {
			return;
		}
		viewBinding.sbH.setProgress(viewBinding.sbH.getMax() * percent / 100);
		viewBinding.sbS.setProgress(viewBinding.sbS.getMax() * percent / 100);
		viewBinding.sbB.setProgress(viewBinding.sbB.getMax() * percent / 100);
	}

	public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
		this.onColorSelectedListener = onColorSelectedListener;
	}

	@Override
	public void setEnabled(boolean enabled) {
		viewBinding.sbH.setEnabled(enabled);
		viewBinding.sbH.setClickable(enabled);
		viewBinding.sbH.setFocusable(enabled);

		viewBinding.sbS.setEnabled(enabled);
		viewBinding.sbS.setClickable(enabled);
		viewBinding.sbS.setFocusable(enabled);

		viewBinding.sbB.setEnabled(enabled);
		viewBinding.sbB.setClickable(enabled);
		viewBinding.sbB.setFocusable(enabled);

		viewBinding.tvH.setEnabled(enabled);
		viewBinding.tvS.setEnabled(enabled);
		viewBinding.tvB.setEnabled(enabled);
	}

	public interface OnColorSelectedListener {
		void onColorSelected(float h, float s, float b);
	}
	
}
