package com.sunzk.colortest.intermediateColor

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.arcsoft.closeli.utils.takeIfIs
import com.sunzk.base.expand.bindView
import com.sunzk.base.expand.collect
import com.sunzk.base.expand.coroutines.GlobalDispatchers
import com.sunzk.base.expand.onClick
import com.sunzk.base.utils.AppUtils
import com.sunzk.base.utils.DisplayUtil
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.R
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.databinding.ActivityGuessColorBinding
import com.sunzk.colortest.db.IntermediateColorResultTable
import com.sunzk.colortest.dialog.CommonSettlementDialog
import com.sunzk.colortest.entity.HSB
import kotlinx.coroutines.launch

/**
 * 猜两个颜色的中间色
 */
@Route(path = RouteInfo.PATH_ACTIVITY_GUESS_COLOR)
class IntermediateColorActivity : BaseActivity() {

	companion object {
		private const val TAG: String = "IntermediateColorActivity"
	}

	private val viewBinding by bindView<ActivityGuessColorBinding>()
	private val viewModel: IntermediateColorViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initView()
		bindData()
	}

	override fun needBGM(): Boolean {
		return true
	}
	
	private fun initView() = with(viewBinding) {
		initDifficultyUI()
		hsbColorPicker.onColorPick = { hsb ->
			viewModel.updateAnswerColor(hsb)
			cdColorCenter.setCardBackgroundColor(hsb.rgbColor)
		}
		btHistory.onClick { startActivity(Intent(this@IntermediateColorActivity, IntermediateColorHistoryActivity::class.java)) }
		btNext.setOnClickListener { viewModel.nextQuestion() }
		btAnswer.setOnClickListener { showAnswer() }
		initColorContentView()
	}

	private fun initDifficultyUI() = with(viewBinding) {
		btDifficulty.adapter = ArrayAdapter(this@IntermediateColorActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, IntermediateColorResult.Difficulty.entries.map { it.text })
		btDifficulty.setSelection(IntermediateColorResult.Difficulty.entries.indexOf(viewModel.pageData.value.difficulty))
		btDifficulty.onItemSelectedListener = object : OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				Log.d(TAG, "IntermediateColorActivity#onItemSelected- position: $position")
				viewModel.switchDifficulty(IntermediateColorResult.Difficulty.entries[position])
			}

			override fun onNothingSelected(parent: AdapterView<*>?) {
			}

		}
	}

	private fun initColorContentView() {
		val spacing = DisplayUtil.dip2px(this, 25f)
		val sideLength = (AppUtils.getScreenWidth(this) - spacing * 4) / 3
		var radius = sideLength / 8
		val minRadius = DisplayUtil.dip2px(this, 10f)
		if (radius < minRadius) {
			radius = minRadius
		}
		viewBinding.flContent.setPadding(spacing, 0, spacing, 0)
		viewBinding.cdColorLeft.radius = radius.toFloat()
		viewBinding.cdColorLeft.layoutParams.width = sideLength
		viewBinding.cdColorLeft.layoutParams.height = sideLength
		viewBinding.cdColorCenter.radius = radius.toFloat()
		viewBinding.cdColorCenter.layoutParams.width = sideLength
		viewBinding.cdColorCenter.layoutParams.height = sideLength
		val layoutParams = viewBinding.cdColorCenter.layoutParams
		if (layoutParams is LinearLayout.LayoutParams) {
			layoutParams.leftMargin = spacing
			layoutParams.rightMargin = spacing
		}
		viewBinding.cdColorRight.radius = radius.toFloat()
		viewBinding.cdColorRight.layoutParams.width = sideLength
		viewBinding.cdColorRight.layoutParams.height = sideLength
	}

	private fun bindData() {
		viewModel.pageData.collect(lifecycleScope) { (difficulty, questionLeftColor, questionRightColor, answerColor, randomIndex, hsbBarLock) ->
			bindDifficulty(difficulty)
			bindColors(questionLeftColor, questionRightColor, answerColor)
			lockHSBSelector(hsbBarLock, questionLeftColor)
		}
	}

	private fun bindDifficulty(difficulty: IntermediateColorResult.Difficulty) {
		val color = when (difficulty) {
			IntermediateColorResult.Difficulty.Hard -> R.color.difficulty_hard
			IntermediateColorResult.Difficulty.Normal -> R.color.difficulty_normal
			IntermediateColorResult.Difficulty.Easy -> R.color.difficulty_easy
		}
		ResourcesCompat.getDrawable(resources, R.drawable.bg_common_bt, null)
			?.mutate()
			?.takeIfIs<GradientDrawable>()
			?.let { drawable ->
				drawable.setColor(resources.getColor(color, null))
				viewBinding.btDifficulty.background = drawable
			}
	}

	private fun bindColors(questionLeftColor: HSB, questionRightColor: HSB, answerColor: HSB) {
		viewBinding.cdColorLeft.setCardBackgroundColor(questionLeftColor.rgbColor)
		viewBinding.cdColorRight.setCardBackgroundColor(questionRightColor.rgbColor)
		viewBinding.cdColorCenter.setCardBackgroundColor(answerColor.rgbColor)
		viewBinding.hsbColorPicker.updateHSB(answerColor.h, answerColor.s, answerColor.b)
	}

	private fun lockHSBSelector(hsbBarLock: BooleanArray, targetColor: HSB) = with(viewBinding) {
		Log.d(TAG, "IntermediateColorActivity#lockHSBSelector- hsbBarLock: $hsbBarLock, targetColor: $targetColor")
		hsbBarLock.forEachIndexed { index, lock ->
			hsbColorPicker.setLock(index, lock)
		}
	}

	private fun showAnswer() {
		val leftColor = viewModel.pageData.value.questionLeftColor
		val rightColor = viewModel.pageData.value.questionRightColor
		val answerColor = viewModel.pageData.value.answerColor
		saveToDB(leftColor, rightColor, answerColor)
		checkRight(leftColor, rightColor, answerColor)
	}

	private fun saveToDB(leftColor: HSB, rightColor: HSB, answerColor: HSB) {
		lifecycleScope.launch(GlobalDispatchers.IO) {
			IntermediateColorResultTable.add(IntermediateColorResult(
				difficulty = viewModel.pageData.value.difficulty,
				questionLeft = leftColor,
				questionRight = rightColor,
				answer = answerColor
			))
		}
	}

	private fun checkRight(leftColor: HSB, rightColor: HSB, answerColor: HSB) {
		val isRight = viewModel.pageData.value.difficulty.isRight(leftColor, rightColor, answerColor)
		val dialog = CommonSettlementDialog(this, isRight)
		dialog.onCancelClickListener = {
			finish()
		}
		dialog.onConfirmClickListener = {
			 viewModel.nextQuestion()
		}
		dialog.show()
	}

}