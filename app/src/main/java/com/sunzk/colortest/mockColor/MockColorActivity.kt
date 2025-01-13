package com.sunzk.colortest.mockColor

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.arcsoft.closeli.utils.takeIfIs
import com.sunzk.base.expand.bindView
import com.sunzk.base.expand.collect
import com.sunzk.base.utils.AppUtils
import com.sunzk.base.utils.DisplayUtil
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.R
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.databinding.ActivityMockColorBinding
import com.sunzk.colortest.db.MockColorResultTable
import com.sunzk.colortest.db.bean.MockColorResult
import com.sunzk.base.expand.coroutines.GlobalDispatchers
import com.sunzk.base.expand.emitBy
import com.sunzk.colortest.dialog.CommonSettlementDialog
import kotlinx.coroutines.*
import okhttp3.internal.toHexString
import java.util.*


@Route(path = RouteInfo.PATH_ACTIVITY_MOCK_COLOR, group = RouteInfo.GROUP_GAME)
class MockColorActivity : BaseActivity() {

	companion object {
		private const val TAG = "MockColorActivity"
	}

	private val viewBinding by bindView<ActivityMockColorBinding>()
	private val viewModel by viewModels<MockColorViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
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
		bindData()
		initColorArea()
	}
	
	private fun initView() = with(viewBinding) {
		initDifficulty()
		btHistory.setOnClickListener { startActivity(Intent(this@MockColorActivity, MockColorHistoryActivity::class.java)) }
		btNext.setOnClickListener { v: View? -> viewModel.nextQuestion() }
		btAnswer.setOnClickListener { v: View? ->
			showAnswer(v)
		}
		hsbColorSelector.onColorPick = { hsb ->
			viewModel.currentPickHSB.update(hsb)
			viewResult.setBackgroundColor(hsb.rgbColor)
		}
	}

	private fun initDifficulty() = with(viewBinding) {
		btDifficulty.adapter = ArrayAdapter(this@MockColorActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, MockColorResult.Difficulty.entries.map { it.text })
		btDifficulty.setSelection(MockColorResult.Difficulty.entries.indexOf(viewModel.currentDifficulty.value))
		btDifficulty.onItemSelectedListener = object : OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				viewModel.currentDifficulty.emitBy(MockColorResult.Difficulty.entries[position])
			}

			override fun onNothingSelected(parent: AdapterView<*>?) {
			}

		}
	}

	private fun bindData() {
		viewModel.currentDifficulty.collect(lifecycleScope) {
			val color = when (it) {
				MockColorResult.Difficulty.Hard -> R.color.difficulty_hard
				MockColorResult.Difficulty.Normal -> R.color.difficulty_normal
				MockColorResult.Difficulty.Easy -> R.color.difficulty_easy
			}
			ResourcesCompat.getDrawable(resources, R.drawable.bg_common_bt, null)
				?.mutate()
				?.takeIfIs<GradientDrawable>()
				?.let { drawable ->
					drawable.setColor(resources.getColor(color, null))
					viewBinding.btDifficulty.background = drawable
				}
		}
		viewModel.currentQuestionHSB.collect(lifecycleScope) { currentQuestionHSB ->
			val color = Color.HSVToColor(currentQuestionHSB)
			Log.d(TAG, "MockColorActivity#nextQuestion- ${currentQuestionHSB.joinToString()} -> ${color.toHexString()}")
			viewBinding.viewDemo.setBackgroundColor(color)
			viewBinding.tvAnswer.text = null
			viewBinding.hsbColorSelector.isEnabled = true
			viewBinding.hsbColorSelector.updateHSB(
				viewModel.currentPickHSB.h,
				viewModel.currentPickHSB.s,
				viewModel.currentPickHSB.b
			)
		}
	}

	override fun needBGM(): Boolean {
		return true
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
		val question = viewModel.currentQuestionHSB.value
		val showH = question[0].toInt().toFloat()
		val showS = (question[1] * 100).toInt().toFloat()
		val showB = (question[2] * 100).toInt().toFloat()
		val answerH = viewBinding.hsbColorSelector.progressH.toFloat()
		val answerS = viewBinding.hsbColorSelector.progressS.toFloat()
		val answerB = viewBinding.hsbColorSelector.progressB.toFloat()

		lifecycleScope.launch {
			saveResult(showH.toInt(), showS.toInt(), showB.toInt())
			showScore(Runtime.testResultNumber)
		}

		val isRight = viewModel.currentDifficulty.value.isRight(floatArrayOf(showH, showS, showB), floatArrayOf(answerH, answerS, answerB))
		val dialog = CommonSettlementDialog(this, isRight)
		dialog.onCancelClickListener = {
			finish()
		}
		dialog.onConfirmClickListener = {
			viewModel.nextQuestion()
		}
		dialog.show()
	}

	private suspend fun saveResult(showH: Int, showS: Int, showB: Int) = withContext(GlobalDispatchers.IO) {
		val result = MockColorResultTable.add(MockColorResult(
			difficulty = viewModel.currentDifficulty.value,
			questionH = showH.toFloat(),
			questionS = showS.toFloat(),
			questionB = showB.toFloat(),
			answerH = viewBinding.hsbColorSelector.progressH.toFloat(),
			answerS = viewBinding.hsbColorSelector.progressS.toFloat(),
			answerB = viewBinding.hsbColorSelector.progressB.toFloat()
		))
		Log.d(TAG, "MockColorActivity#saveResult- $result")
	}

	private fun showScore(number: Int) {
		viewBinding.tvScore.text = String.format(
			Locale.getDefault(),
			"总计完成测试: %d次",
			number
		)
	}
}