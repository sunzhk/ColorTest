package com.sunzk.colortest.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sunzk.colortest.BaseActivity;
import com.sunzk.colortest.R;
import com.sunzk.colortest.databinding.ActivityGuessColorBinding;
import com.sunzk.base.utils.AppUtils;
import com.sunzk.base.utils.ColorUtils;
import com.sunzk.base.utils.DisplayUtil;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class GuessColorActivity extends BaseActivity {
	
	private static final String TAG = "GuessColorActivity";
	
	public static final String INTENT_KEY_DIFFICULTY = "difficulty";
	
	private ActivityGuessColorBinding viewBinding;
	
	private Difficulty difficulty = Difficulty.EASY;

	private float[] leftColor = new float[3];
	private float[] rightColor = new float[3];
	private float[] centerColor = new float[3];

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewBinding = ActivityGuessColorBinding.inflate(getLayoutInflater());
		setContentView(viewBinding.getRoot());

		Object dif = getIntent().getSerializableExtra(INTENT_KEY_DIFFICULTY);
		if (dif instanceof Difficulty) {
			difficulty = (Difficulty) dif;
		}

		Log.d(TAG, "onCreate: " + difficulty.name());
		
		
		viewBinding.hsbColorSelector.setOnColorSelectedListener((h, s, b) -> {
			viewBinding.cdColorCenter.setCardBackgroundColor(Color.HSVToColor(new float[]{h, s, b}));
		});

		viewBinding.btNext.setOnClickListener(v -> nextQuestion());
		viewBinding.btAnswer.setOnClickListener(v -> showAnswer());
		
		initColorContentView();
		nextQuestion();
	}

	@Override
	protected boolean needBGM() {
		return true;
	}

	private void initColorContentView() {
		int spacing = DisplayUtil.dip2px(this, 25);
		int sideLength = (AppUtils.getScreenWidth(this) - spacing * 4) / 3;
		int radius = sideLength / 8;
		int minRadius = DisplayUtil.dip2px(this, 10);
		if (radius < minRadius) {
			radius = minRadius;
		}

		viewBinding.flContent.setPadding(spacing, 0, spacing, 0);

		viewBinding.cdColorLeft.setRadius(radius);
		viewBinding.cdColorLeft.getLayoutParams().width = sideLength;
		viewBinding.cdColorLeft.getLayoutParams().height = sideLength;

		viewBinding.cdColorCenter.setRadius(radius);
		viewBinding.cdColorCenter.getLayoutParams().width = sideLength;
		viewBinding.cdColorCenter.getLayoutParams().height = sideLength;
		ViewGroup.LayoutParams layoutParams = viewBinding.cdColorCenter.getLayoutParams();
		if (layoutParams instanceof LinearLayout.LayoutParams) {
			((LinearLayout.LayoutParams) layoutParams).leftMargin = spacing;
			((LinearLayout.LayoutParams) layoutParams).rightMargin = spacing;
		}

		viewBinding.cdColorRight.setRadius(radius);
		viewBinding.cdColorRight.getLayoutParams().width = sideLength;
		viewBinding.cdColorRight.getLayoutParams().height = sideLength;
	}

	private void nextQuestion() {
		viewBinding.tvAnswer.setText(null);
		
		float[] color = ColorUtils.randomHSBColor(0, difficulty.minSBPercent / 100f, difficulty.minSBPercent / 100f);

		leftColor[0] = color[0];
		leftColor[1] = color[1];
		leftColor[2] = color[2];

		float colorDifferencePercent;;
		
		int diffColorIndex = new Random().nextInt(3);
		float nextColor;
		switch (diffColorIndex) {
			case 0:
				colorDifferencePercent = difficulty.colorHDifferencePercent * 1.0f;
				nextColor = color[0] + colorDifferencePercent * 360 / 100f;
				if (nextColor > 360) {
					rightColor[0] = color[0] - colorDifferencePercent * 360 / 100f;
				} else {
					rightColor[0] = nextColor;
				}
				rightColor[1] = leftColor[1];
				rightColor[2] = leftColor[2];
				break;
			case 1:
				colorDifferencePercent = difficulty.colorSDifferencePercent * 1.0f;
				nextColor = color[1] + colorDifferencePercent / 100f;
				if (nextColor > 1) {
					rightColor[1] = color[1] - colorDifferencePercent / 100f;
				} else {
					rightColor[1] = nextColor;
				}
				rightColor[0] = leftColor[0];
				rightColor[2] = leftColor[2];
				break;
			case 2:
				colorDifferencePercent = difficulty.colorBDifferencePercent * 1.0f;
				nextColor = color[2] + colorDifferencePercent / 100f;
				if (nextColor > 1) {
					rightColor[2] = color[2] - colorDifferencePercent / 100f;
				} else {
					rightColor[2] = nextColor;
				}
				rightColor[0] = leftColor[0];
				rightColor[1] = leftColor[1];
				break;
			default:
				break;
		}

		centerColor[0] = (leftColor[0] + rightColor[0]) / 2;
		centerColor[1] = (leftColor[1] + rightColor[1]) / 2;
		centerColor[2] = (leftColor[2] + rightColor[2]) / 2;

		Log.d(TAG, "nextQuestion-left: " + Arrays.toString(leftColor));
		Log.d(TAG, "nextQuestion-center: " + Arrays.toString(centerColor));
		Log.d(TAG, "nextQuestion-next: " + Arrays.toString(rightColor));

		viewBinding.cdColorLeft.setCardBackgroundColor(Color.HSVToColor(leftColor));
//		viewBinding.cdColorCenter.setCardBackgroundColor(Color.HSVToColor(centerColor));
		viewBinding.hsbColorSelector.setEnabled(true);
		viewBinding.hsbColorSelector.reset(50);
		viewBinding.cdColorRight.setCardBackgroundColor(Color.HSVToColor(rightColor));
	}

	private void showAnswer() {
		int showHLeft = (int) leftColor[0];
		int showSLeft = (int) (leftColor[1] * 100);
		int showBLeft = (int) (leftColor[2] * 100);

		int showHCenter = (int) centerColor[0];
		int showSCenter = (int) (centerColor[1] * 100);
		int showBCenter = (int) (centerColor[2] * 100);

		int showHRight = (int) rightColor[0];
		int showSRight = (int) (rightColor[1] * 100);
		int showBRight = (int) (rightColor[2] * 100);
		viewBinding.hsbColorSelector.setEnabled(false);
		viewBinding.tvAnswer.setText(String.format(Locale.getDefault(), "左侧: H: %d度  S: %d%%  B: %d%%\n中间: H: %d度  S: %d%%  B: %d%%\n右侧: H: %d度  S: %d%%  B: %d%%\n", 
				showHLeft, showSLeft, showBLeft,
				showHCenter, showSCenter, showBCenter,
				showHRight, showSRight, showBRight));
//		TestResultDataBase.getInstance(this).recordScore(showH, showS, showB, hsbColorSelector.getProgressH(), hsbColorSelector.getProgressS(), hsbColorSelector.getProgressB());
//		showScore(Runtime.testResultNumber);
//		checkAnswer();
		float difH = Math.abs(viewBinding.hsbColorSelector.getColorH() - centerColor[0]) * 100f / 360f;
		float difS = Math.abs(viewBinding.hsbColorSelector.getColorS() - centerColor[1]) * 100f;
		float difB = Math.abs(viewBinding.hsbColorSelector.getColorB() - centerColor[2]) * 100f;
		if (difH > difficulty.allowDeviation || difS > difficulty.allowDeviation || difB > difficulty.allowDeviation) {
			new AlertDialog.Builder(this)
					.setTitle("就这？")
					.setMessage("就这也敢玩地狱模式？")
					.setPositiveButton("再来！", (dialog, which) -> dialog.cancel())
					.create()
					.show();
		} else {
			new AlertDialog.Builder(this)
					.setTitle("哎哟")
					.setMessage("哎哟不错哦")
					.setPositiveButton("不愧是我", (dialog, which) -> dialog.cancel())
					.create()
					.show();
		}
	}

	public enum Difficulty {

		EASY(40, 35, 35, 60, 15),
		MEDIUM(30, 30, 30, 40, 10),
		DIFFICULT(15, 25, 25, 20, 10);

		private int colorHDifferencePercent;
		private int colorSDifferencePercent;
		private int colorBDifferencePercent;
		private int minSBPercent;
		private int allowDeviation;

		Difficulty(int colorHDifferencePercent, int colorSDifferencePercent, int colorBDifferencePercent, int minSBPercent, int allowDeviation) {
			this.colorHDifferencePercent = colorHDifferencePercent;
			this.colorSDifferencePercent = colorSDifferencePercent;
			this.colorBDifferencePercent = colorBDifferencePercent;
			this.minSBPercent = minSBPercent;
			this.allowDeviation = allowDeviation;
		}

		public int getColorHDifferencePercent() {
			return colorHDifferencePercent;
		}

		public int getAllowDeviation() {
			return allowDeviation;
		}
	}
}
