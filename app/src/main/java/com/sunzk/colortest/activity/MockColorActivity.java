package com.sunzk.colortest.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.sunzk.colortest.BaseActivity;
import com.sunzk.colortest.Runtime;
import com.sunzk.colortest.databinding.ActivityMockColorBinding;
import com.sunzk.colortest.db.TestResultDataBase;
import com.sunzk.colortest.entity.TestResult;
import com.sunzk.base.utils.AppUtils;
import com.sunzk.base.utils.ColorUtils;
import com.sunzk.base.utils.DisplayUtil;

import java.util.List;
import java.util.Locale;

public class MockColorActivity extends BaseActivity {
	
	private static final String TAG = "MockColorActivity";
	
	private static final int ANSWER_STANDARD = 5;
	
	private ActivityMockColorBinding viewBinding;

	private float[] currentHSB;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		viewBinding = ActivityMockColorBinding.inflate(getLayoutInflater());
		setContentView(viewBinding.getRoot());
		
		Observable.create((ObservableOnSubscribe<List<TestResult>>) emitter -> {
			try {
				List<TestResult> testResultList = TestResultDataBase.getInstance(MockColorActivity.this).listScore();
				emitter.onNext(testResultList);
				emitter.onComplete();
			} catch (Throwable throwable) {
				emitter.onError(throwable);
			}
			
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<List<TestResult>>() {
					@Override
					public void onSubscribe(Disposable d) {
						
					}

					@Override
					public void onNext(List<TestResult> testResults) {
						Log.d(TAG, "onNext: " + testResults.size());
						Runtime.testResultNumber = testResults.size();
						showScore(Runtime.testResultNumber);
						for (TestResult testResult : testResults) {
//							Log.d(TAG, "onNext: line: " + testResult);
						}
					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onComplete() {

					}
				});

		viewBinding.btNext.setOnClickListener(v -> nextProblem());

		viewBinding.btAnswer.setOnClickListener(this::showAnswer);

		viewBinding.hsbColorSelector.setOnColorSelectedListener((h, s, b) -> {
			int color = Color.HSVToColor(new float[]{h, s, b});
			viewBinding.viewResult.setBackgroundColor(color);
		});

		initColorArea();

		nextProblem();
	}
	
	@Override
	protected boolean needBGM() {
		return true;
	}
	
	/**
	 * 对比答案
	 */
	private void checkAnswer() {
		float h = currentHSB[0];
		float s = currentHSB[1];
		float b = currentHSB[2];
		float difH = Math.abs(h - viewBinding.hsbColorSelector.getColorH());
		float difS = Math.abs(s * 100 - viewBinding.hsbColorSelector.getColorS() * 100);
		float difB = Math.abs(b * 100 - viewBinding.hsbColorSelector.getColorB() * 100);
		if (difH < ANSWER_STANDARD && difS < ANSWER_STANDARD && difB < ANSWER_STANDARD) {
			// TODO: 2020/7/11 显示奖励效果，这里先显示一个Toast顶替一下
			Toast.makeText(this, String.format(Locale.getDefault(), "各项值的误差不超过%d，真棒", ANSWER_STANDARD), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 初始化颜色区域
	 */
	private void initColorArea() {
		int screenSize = AppUtils.getScreenWidth(getApplicationContext());

		int spacing = DisplayUtil.dip2px(this, 20);
		
		int cardSideLength = (screenSize - spacing * 4) / 2;
		int radius = cardSideLength / 2;

		viewBinding.cvDemo.setRadius(radius);
		viewBinding.cvDemo.getLayoutParams().width = cardSideLength;
		viewBinding.cvDemo.getLayoutParams().height = cardSideLength;
		ViewGroup.LayoutParams cdDemoLayoutParams = viewBinding.cvDemo.getLayoutParams();
		if (cdDemoLayoutParams instanceof FrameLayout.LayoutParams) {
			((FrameLayout.LayoutParams) cdDemoLayoutParams).leftMargin = spacing;
			((FrameLayout.LayoutParams) cdDemoLayoutParams).rightMargin = spacing;
		}

		viewBinding.cvResult.setRadius(radius);
		viewBinding.cvResult.getLayoutParams().width = cardSideLength;
		viewBinding.cvResult.getLayoutParams().height = cardSideLength;
		ViewGroup.LayoutParams cdResultLayoutParams = viewBinding.cvResult.getLayoutParams();
		if (cdResultLayoutParams instanceof FrameLayout.LayoutParams) {
			((FrameLayout.LayoutParams) cdResultLayoutParams).leftMargin = spacing;
			((FrameLayout.LayoutParams) cdResultLayoutParams).rightMargin = spacing;
		}
	}

	public void showAnswer(View v) {
		int showH = (int) currentHSB[0];
		int showS = (int) (currentHSB[1] * 100);
		int showB = (int) (currentHSB[2] * 100);
		viewBinding.hsbColorSelector.setEnabled(false);
		viewBinding.tvAnswer.setText(String.format(Locale.getDefault(), "H: %d度  S: %d%%  B: %d%%", showH, showS, showB));
		TestResultDataBase.getInstance(this).recordScore(showH, showS, showB, viewBinding.hsbColorSelector.getProgressH(), viewBinding.hsbColorSelector.getProgressS(), viewBinding.hsbColorSelector.getProgressB());
		showScore(Runtime.testResultNumber);
		checkAnswer();
	}

	private void nextProblem() {
		currentHSB = ColorUtils.randomHSBColor();
		int color = Color.HSVToColor(currentHSB);
		Log.d(TAG, "nextProblem: " + currentHSB[0] + "," + currentHSB[1] + "," + currentHSB[2] + "->" + color);
		viewBinding.viewDemo.setBackgroundColor(color);

		viewBinding.tvAnswer.setText(null);

		viewBinding.hsbColorSelector.setEnabled(true);
		viewBinding.hsbColorSelector.reset(50);
	}
	
	private void showScore(int number) {
		viewBinding.tvScore.setText(String.format(Locale.getDefault(), "总计完成测试: %d次", number));
	}
	
}
