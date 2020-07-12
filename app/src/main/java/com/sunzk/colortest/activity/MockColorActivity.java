package com.sunzk.colortest.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.sunzk.colortest.R;
import com.sunzk.colortest.Runtime;
import com.sunzk.colortest.db.TestResultDataBase;
import com.sunzk.colortest.entity.TestResult;
import com.sunzk.colortest.utils.AppUtils;
import com.sunzk.colortest.utils.ColorUtils;
import com.sunzk.colortest.utils.DisplayUtil;
import com.sunzk.colortest.utils.NumberUtils;
import com.sunzk.colortest.view.HSBColorSelector;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MockColorActivity extends AppCompatActivity {
	
	private static final String TAG = "MockColorActivity";
	
	private static final int ANSWER_STANDARD = 5;

	private CardView cdDemo;
	private CardView cdResult;
	private View viewDemo;
	private View viewResult;
	
	private HSBColorSelector hsbColorSelector;
	
	private TextView tvAnswer;
	
	private TextView tvScore;
	
	private float[] currentHSB;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mock_color);
		
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

		cdDemo = findViewById(R.id.activity_mock_color_cv_demo);
		viewDemo = findViewById(R.id.activity_mock_color_view_demo);
		cdResult = findViewById(R.id.activity_mock_color_cv_result);
		viewResult = findViewById(R.id.activity_mock_color_view_result);
		
		findViewById(R.id.activity_mock_color_bt_next).setOnClickListener(v -> nextProblem());

		tvAnswer = findViewById(R.id.activity_mock_color_tv_answer);
		
		findViewById(R.id.activity_mock_color_bt_answer).setOnClickListener(this::showAnswer);
		
		hsbColorSelector = findViewById(R.id.activity_mock_color_hsb_color_selector);
		hsbColorSelector.setOnColorSelectedListener((h, s, b) -> {
			int color = Color.HSVToColor(new float[]{h, s, b});
			viewResult.setBackgroundColor(color);
		});

		tvScore = findViewById(R.id.activity_mock_color_tv_score);
		
		initColorArea();

		nextProblem();
	}

	/**
	 * 对比答案
	 */
	private void checkAnswer() {
		float h = currentHSB[0];
		float s = currentHSB[1];
		float b = currentHSB[2];
		float difH = Math.abs(h - hsbColorSelector.getColorH());
		float difS = Math.abs(s * 100 - hsbColorSelector.getColorS() * 100);
		float difB = Math.abs(b * 100 - hsbColorSelector.getColorB() * 100);
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
		
		cdDemo.setRadius(radius);
		cdDemo.getLayoutParams().width = cardSideLength;
		cdDemo.getLayoutParams().height = cardSideLength;
		ViewGroup.LayoutParams cdDemoLayoutParams = cdDemo.getLayoutParams();
		if (cdDemoLayoutParams instanceof FrameLayout.LayoutParams) {
			((FrameLayout.LayoutParams) cdDemoLayoutParams).leftMargin = spacing;
			((FrameLayout.LayoutParams) cdDemoLayoutParams).rightMargin = spacing;
		}

		cdResult.setRadius(radius);
		cdResult.getLayoutParams().width = cardSideLength;
		cdResult.getLayoutParams().height = cardSideLength;
		ViewGroup.LayoutParams cdResultLayoutParams = cdResult.getLayoutParams();
		if (cdResultLayoutParams instanceof FrameLayout.LayoutParams) {
			((FrameLayout.LayoutParams) cdResultLayoutParams).leftMargin = spacing;
			((FrameLayout.LayoutParams) cdResultLayoutParams).rightMargin = spacing;
		}
	}

	public void showAnswer(View v) {
		int showH = (int) currentHSB[0];
		int showS = (int) (currentHSB[1] * 100);
		int showB = (int) (currentHSB[2] * 100);
		hsbColorSelector.setEnabled(false);
		tvAnswer.setText(String.format(Locale.getDefault(), "H: %d度  S: %d%%  B: %d%%", showH, showS, showB));
		TestResultDataBase.getInstance(this).recordScore(showH, showS, showB, hsbColorSelector.getProgressH(), hsbColorSelector.getProgressS(), hsbColorSelector.getProgressB());
		showScore(Runtime.testResultNumber);
		checkAnswer();
	}

	private void nextProblem() {
		currentHSB = ColorUtils.randomHSBColor();
		int color = Color.HSVToColor(currentHSB);
		Log.d(TAG, "nextProblem: " + currentHSB[0] + "," + currentHSB[1] + "," + currentHSB[2] + "->" + color);
		viewDemo.setBackgroundColor(color);
		
		tvAnswer.setText(null);

		hsbColorSelector.setEnabled(true);
		hsbColorSelector.reset(50);
	}
	
	private void showScore(int number) {
		tvScore.setText(String.format(Locale.getDefault(), "总计完成测试: %d次", number));
	}
	
}
