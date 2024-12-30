package com.sunzk.colortest.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.sunzk.base.expand.bindView
import com.sunzk.base.utils.AppUtils
import com.sunzk.base.utils.ColorUtils
import com.sunzk.base.utils.DisplayUtil
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.activity.history.MockColorHistoryActivity
import com.sunzk.colortest.databinding.ActivityMockColorBinding
import com.sunzk.colortest.db.MockColorResultTable
import com.sunzk.colortest.db.bean.MockColorResult
import com.sunzk.demo.tools.coroutine.GlobalDispatchers
import kotlinx.coroutines.*
import java.util.*

@Route(path = RouteInfo.PATH_ACTIVITY_MOCK_COLOR, group = RouteInfo.GROUP_GAME)
class MockColorActivity : BaseActivity() {

	companion object {
		private const val TAG = "MockColorActivity"
		private const val ANSWER_STANDARD = 5
	}

	private val viewBinding by bindView<ActivityMockColorBinding>()
	private lateinit var currentHSB: FloatArray
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		lifecycleScope.launch(GlobalDispatchers.IO) {
			Log.d(TAG, "onCreate: try to read db data")
			val testResult = MockColorResultTable.queryAll()
			withContext(GlobalDispatchers.Main) {
				Log.d(TAG, "onNext: " + testResult?.size)
				Runtime.testResultNumber = testResult?.size ?: 0
				showScore(Runtime.testResultNumber)
			}
		}
		initView()
		initColorArea()
		nextProblem()
	}
	
	private fun initView() = with(viewBinding) {
		btHistory.setOnClickListener { startActivity(Intent(this@MockColorActivity, MockColorHistoryActivity::class.java)) }
		btNext.setOnClickListener { v: View? -> nextProblem() }
		btAnswer.setOnClickListener { v: View? ->
			showAnswer(v)
		}
		hsbColorSelector.setOnColorSelectedListener { h: Float, s: Float, b: Float ->
			val color = Color.HSVToColor(floatArrayOf(h, s, b))
			viewResult.setBackgroundColor(color)
		}
	}

	override fun needBGM(): Boolean {
		return true
	}

	/**
	 * 对比答案
	 */
	private fun checkAnswer() {
		val h = currentHSB[0]
		val s = currentHSB[1]
		val b = currentHSB[2]
		val difH = Math.abs(h - viewBinding.hsbColorSelector.colorH)
		val difS =
			Math.abs(s * 100 - viewBinding.hsbColorSelector.colorS * 100)
		val difB =
			Math.abs(b * 100 - viewBinding.hsbColorSelector.colorB * 100)
		if (difH < ANSWER_STANDARD && difS < ANSWER_STANDARD && difB < ANSWER_STANDARD) {
			// TODO: 2020/7/11 显示奖励效果，这里先显示一个Toast顶替一下
			Toast.makeText(
				this,
				String.format(
					Locale.getDefault(),
					"各项值的误差不超过%d，真棒",
					ANSWER_STANDARD
				),
				Toast.LENGTH_LONG
			).show()
		}
	}

	/**
	 * 初始化颜色区域
	 */
	private fun initColorArea() {
		val screenSize = AppUtils.getScreenWidth(applicationContext)
		val spacing = DisplayUtil.dip2px(this, 20f)
		val cardSideLength = (screenSize - spacing * 4) / 2
		val radius = cardSideLength / 2
		viewBinding.cvDemo.radius = radius.toFloat()
		viewBinding.cvDemo.layoutParams.width = cardSideLength
		viewBinding.cvDemo.layoutParams.height = cardSideLength
		val cdDemoLayoutParams = viewBinding.cvDemo.layoutParams
		if (cdDemoLayoutParams is FrameLayout.LayoutParams) {
			cdDemoLayoutParams.leftMargin = spacing
			cdDemoLayoutParams.rightMargin = spacing
		}
		viewBinding.cvResult.radius = radius.toFloat()
		viewBinding.cvResult.layoutParams.width = cardSideLength
		viewBinding.cvResult.layoutParams.height = cardSideLength
		val cdResultLayoutParams = viewBinding.cvResult.layoutParams
		if (cdResultLayoutParams is FrameLayout.LayoutParams) {
			cdResultLayoutParams.leftMargin = spacing
			cdResultLayoutParams.rightMargin = spacing
		}
	}

	private fun showAnswer(v: View?) {
		val showH = currentHSB[0].toInt()
		val showS = (currentHSB[1] * 100).toInt()
		val showB = (currentHSB[2] * 100).toInt()
		viewBinding.tvAnswer.text = String.format(
			Locale.getDefault(),
			"H: %d度  S: %d%%  B: %d%%",
			showH,
			showS,
			showB
		)
		lifecycleScope.launch {
			saveResult(showH, showS, showB)
			showScore(Runtime.testResultNumber)
		}
		checkAnswer()
	}

	private suspend fun saveResult(showH: Int, showS: Int, showB: Int) = withContext(GlobalDispatchers.IO) {
		val result = MockColorResultTable.add(MockColorResult(
			questionH = showH.toFloat(),
			questionS = showS.toFloat(),
			questionB = showB.toFloat(),
			answerH = viewBinding.hsbColorSelector.progressH.toFloat(),
			answerS = viewBinding.hsbColorSelector.progressS.toFloat(),
			answerB = viewBinding.hsbColorSelector.progressB.toFloat()
		))
		Log.d(TAG, "MockColorActivity#saveResult- $result")
	}

	private fun nextProblem() {
		currentHSB = ColorUtils.randomHSBColor(0f, 0.2f, 0.2f)
		val color = Color.HSVToColor(currentHSB)
		Log.d(
			TAG,
			"nextProblem: " + currentHSB[0] + "," + currentHSB[1] + "," + currentHSB[2] + "->" + color
		)
		viewBinding.viewDemo.setBackgroundColor(color)
		viewBinding.tvAnswer.text = null
		viewBinding.hsbColorSelector.isEnabled = true
		viewBinding.hsbColorSelector.reset(50)
	}

	private fun showScore(number: Int) {
		viewBinding.tvScore.text = String.format(
			Locale.getDefault(),
			"总计完成测试: %d次",
			number
		)
	}
}