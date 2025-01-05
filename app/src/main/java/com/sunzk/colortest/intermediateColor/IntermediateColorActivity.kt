package com.sunzk.colortest.intermediateColor

import android.content.Intent
import android.graphics.Color
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
import com.sunzk.base.expand.emitBy
import com.sunzk.base.expand.onClick
import com.sunzk.base.utils.AppUtils
import com.sunzk.base.utils.ColorUtils
import com.sunzk.base.utils.DisplayUtil
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.R
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.databinding.ActivityGuessColorBinding
import com.sunzk.colortest.db.IntermediateColorResultTable
import com.sunzk.colortest.db.bean.IntermediateColorResult
import com.sunzk.colortest.dialog.CommonSettlementDialog
import kotlinx.coroutines.launch
import java.util.*

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
		Log.d(TAG, "onCreate: ${viewModel.currentDifficulty}")
		initView()
		bindData()
//		nextQuestion()
	}

	override fun needBGM(): Boolean {
		return true
	}
	
	private fun initView() = with(viewBinding) {
		initDifficultyUI()
		hsbColorSelector.onColorSelectedListener = { hsb ->
			viewModel.answerColor.emitBy(hsb.hsbColor)
		}
		btHistory.onClick { startActivity(Intent(this@IntermediateColorActivity, IntermediateColorHistoryActivity::class.java)) }
		btNext.setOnClickListener { nextQuestion() }
		btAnswer.setOnClickListener { showAnswer() }
		initColorContentView()
	}

	private fun initDifficultyUI() = with(viewBinding) {
		btDifficulty.adapter = ArrayAdapter(this@IntermediateColorActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, IntermediateColorResult.Difficulty.entries.map { it.text })
		btDifficulty.setSelection(IntermediateColorResult.Difficulty.entries.indexOf(viewModel.currentDifficulty.value))
		btDifficulty.onItemSelectedListener = object : OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				viewModel.currentDifficulty.emitBy(IntermediateColorResult.Difficulty.entries[position])
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
		viewModel.currentDifficulty.collect(lifecycleScope) {
			val color = when (it) {
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
			nextQuestion()
		}

		viewModel.questionLeftColor.collect(lifecycleScope) { t ->
			viewBinding.cdColorLeft.setCardBackgroundColor(Color.HSVToColor(t))
		}
		viewModel.questionRightColor.collect(lifecycleScope) { t ->
			viewBinding.cdColorRight.setCardBackgroundColor(Color.HSVToColor(t))
		}
		viewModel.answerColor.collect(lifecycleScope) { t ->
			viewBinding.cdColorCenter.setCardBackgroundColor(Color.HSVToColor(t))
		}
	}

	private fun nextQuestion() {
		Log.d(TAG, "IntermediateColorActivity#nextQuestion diff=${viewModel.currentDifficulty.value}")
		val difficulty = viewModel.currentDifficulty.value
		// 先随机个左边的初始色
		val nextLeftColor = ColorUtils.randomHSBColor(0f, difficulty.minSBPercent / 100f, difficulty.minSBPercent / 100f)
		viewModel.questionLeftColor.emitBy(nextLeftColor)
		// 基于左边的颜色，生成一个右侧的颜色
		val nextRightColor = nextLeftColor.clone()
		val colorDifferencePercent: Float
		val nextColor: Float
		when (Random().nextInt(3)) {
			0 -> {
				colorDifferencePercent = difficulty.colorHDifferencePercent * 1.0f
				nextColor = nextLeftColor[0] + colorDifferencePercent * 360 / 100f
				nextRightColor[0] = if (nextColor > 360) {
					nextLeftColor[0] - colorDifferencePercent * 360 / 100f
				} else {
					nextColor
				}
				lockHSBSelector(0, nextLeftColor)
			}
			1 -> {
				colorDifferencePercent = difficulty.colorSDifferencePercent * 1.0f
				nextColor = nextLeftColor[1] + colorDifferencePercent / 100f
				nextRightColor[1] = if (nextColor > 1) {
					nextLeftColor[1] - colorDifferencePercent / 100f
				} else {
					nextColor
				}
				lockHSBSelector(1, nextLeftColor)
			}
			2 -> {
				colorDifferencePercent = difficulty.colorBDifferencePercent * 1.0f
				nextColor = nextLeftColor[2] + colorDifferencePercent / 100f
				nextRightColor[2] = if (nextColor > 1) {
					nextLeftColor[2] - colorDifferencePercent / 100f
				} else {
					nextColor
				}
				lockHSBSelector(2, nextLeftColor)
			}
			else -> {
				lockHSBSelector(-1, nextLeftColor)
			}
		}
		viewModel.questionRightColor.emitBy(nextRightColor)
		Log.d(TAG, "nextQuestion-next left: ${nextLeftColor.contentToString()}")
		Log.d(TAG, "nextQuestion-next right: ${nextRightColor.contentToString()}")

		viewBinding.hsbColorSelector.isEnabled = true
	}
	
	private fun lockHSBSelector(randomIndex: Int, targetColor: FloatArray) = with(viewBinding) {
		Log.d(TAG, "IntermediateColorActivity#lockHSBSelector- randomIndex: $randomIndex, targetColor: ${targetColor.contentToString()}")
		hsbColorSelector.unLockAll()
		hsbColorSelector.reset(50)
		if (randomIndex < 0) {
			return@with
		}
		val scope = arrayListOf(0, 1, 2).also { it.removeAt(randomIndex) }
		val diff = viewModel.currentDifficulty.value
		Log.d(TAG, "IntermediateColorActivity#lockHSBSelector- scope=${scope.joinToString()}, diff=$diff")
		repeat(diff.fixedNumberOfParameters) {
			if (scope.isEmpty()) {
				return@repeat
			}
			val index = scope.removeAt(Random().nextInt(scope.size))
			// 先把值写进去
			when (index) {
				0 -> {
					hsbColorSelector.setHValue(targetColor[0].toInt())
					Log.d(TAG, "IntermediateColorActivity#lockHSBSelector- lock index: $index, value=${targetColor[0].toInt()}")
				}
				1 -> {
					hsbColorSelector.setSValue((targetColor[1] * 100).toInt())
					Log.d(TAG, "IntermediateColorActivity#lockHSBSelector- lock index: $index, value=${(targetColor[1] * 100).toInt()}")
				}
				2 -> {
					hsbColorSelector.setBValue((targetColor[2] * 100).toInt())
					Log.d(TAG, "IntermediateColorActivity#lockHSBSelector- lock index: $index, value=${(targetColor[2] * 100).toInt()}")
				}
				else -> {}
			}
			// 再锁定
			hsbColorSelector.setLock(index, true)
		}
	}

	private fun showAnswer() {
		val leftColor = viewModel.questionLeftColor.value.apply { 
			this[1] = this[1] * 100 
			this[2] = this[2] * 100
		}
		val rightColor = viewModel.questionRightColor.value.apply { 
			this[1] = this[1] * 100 
			this[2] = this[2] * 100
		}
		val answerColor = viewModel.answerColor.value.apply { 
			this[1] = this[1] * 100 
			this[2] = this[2] * 100
		}
		viewBinding.hsbColorSelector.isEnabled = false
		saveToDB(leftColor, rightColor, answerColor)
		checkRight(leftColor, rightColor, answerColor)
	}

	private fun saveToDB(leftColor: FloatArray, rightColor: FloatArray, answerColor: FloatArray) {
		lifecycleScope.launch(GlobalDispatchers.IO) {
			IntermediateColorResultTable.add(IntermediateColorResult(
				difficulty = viewModel.currentDifficulty.value,
				questionLeftH = leftColor[0],
				questionLeftS = leftColor[1],
				questionLeftB = leftColor[2],
				questionRightH = rightColor[0],
				questionRightS = rightColor[1],
				questionRightB = rightColor[2],
				answerH = answerColor[0],
				answerS = answerColor[1],
				answerB = answerColor[2]
			))
		}
	}

	private fun checkRight(leftColor: FloatArray, rightColor: FloatArray, answerColor: FloatArray) {
		val isRight = viewModel.currentDifficulty.value.isRight(leftColor, rightColor, answerColor)
		val dialog = CommonSettlementDialog(this, isRight)
		dialog.onCancelClickListener = {
			finish()
		}
		dialog.onConfirmClickListener = {
			 nextQuestion()
		}
		dialog.show()
	}

}