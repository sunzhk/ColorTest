package com.sunzk.colortest.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.annotation.IntRange
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.sunzk.base.expand.bindView
import com.sunzk.base.expand.livedata.dec
import com.sunzk.base.expand.livedata.inc
import com.sunzk.base.utils.ColorUtils
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.R
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.databinding.ActivityFindDiffColorBinding
import com.sunzk.colortest.entity.HSB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Route(path = RouteInfo.PATH_ACTIVITY_FIND_DIFF_COLOR, group = RouteInfo.GROUP_GAME)
class FindDiffColorActivity : BaseActivity() {

	private val TAG: String = "FindDiffColorActivity"

	private val viewBinding by bindView<ActivityFindDiffColorBinding>()
	private val viewModel: FindDiffColorViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initViews()

		resetLevel(FindDiffColorViewModel.DEFAULT_LEVEL)
	}

	private fun initViews() = with(viewBinding) {
		initControllerView()
		cbAutoNext.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
			if (b && viewBinding.findDiffColor.isShowingResult) {
				checkAutoNext()
			}
		}
		btChange.setOnClickListener { v: View? -> resetColor() }
		switchLight(false)
		btLightSwitch.setOnClickListener { v: View? ->
			switchLight(!viewModel.isLight)
		}

		findDiffColor.setOnDiffColorViewClickListener { view, result ->
			Log.d(TAG, "onClick: $result")
			showResult(result)
		}
	}

	private fun initControllerView() {
		viewModel.currentLevel.observe(this, Observer { level ->
			resetLevel(level)
		})
		viewBinding.ivLevelLeft.setOnClickListener {
			val currentLevel: Int =
				viewModel.currentLevel.value ?: FindDiffColorViewModel.DEFAULT_LEVEL
			if (currentLevel > FindDiffColorViewModel.MIN_LEVEL) {
				viewModel.currentLevel.dec()
			}
		}
		viewBinding.ivLevelRight.setOnClickListener {
			val currentLevel: Int =
				viewModel.currentLevel.value ?: FindDiffColorViewModel.DEFAULT_LEVEL
			if (currentLevel < FindDiffColorViewModel.MAX_LEVEL) {
				viewModel.currentLevel.inc()
			}
		}
	}

	private fun resetLevel(@IntRange(from = 1, to = 10) level: Int) {
		Log.d(TAG, "resetLevel: $level")
		viewBinding.tvLevel.text = level.toString()
		resetDifficulty(countPerSide)
	}

	private fun switchLight(on: Boolean) = with(viewBinding) {
		viewModel.isLight = on
		if (on) {
			flContainer.setBackgroundResource(R.color.bg_activity_find_diff_color_light)
			btLightSwitch.setText(R.string.light_switch_text_turn_off)
			tvLevel.setTextColor(resources.getColor(R.color.text_default_light))
			findDiffColor.setResultStrokeColor(resources.getColor(R.color.text_default_light))

			cbAutoNext.setTextColor(resources.getColor(R.color.common_bt_text, null))
			btLightSwitch.setBackgroundResource(R.drawable.bg_common_bt)
			btLightSwitch.setTextColor(resources.getColor(R.color.common_bt_text, null))
			btChange.setBackgroundResource(R.drawable.bg_common_bt)
			btChange.setTextColor(resources.getColor(R.color.common_bt_text, null))
		} else {
			flContainer.setBackgroundResource(R.color.bg_activity_find_diff_color_dark)
			btLightSwitch.setText(R.string.light_switch_text_turn_on)
			tvLevel.setTextColor(resources.getColor(R.color.text_default_dark))
			findDiffColor.setResultStrokeColor(resources.getColor(R.color.text_default_dark))

			cbAutoNext.setTextColor(resources.getColor(R.color.common_bt_text_dark, null))
			btLightSwitch.setBackgroundResource(R.drawable.bg_common_bt_dark)
			btLightSwitch.setTextColor(resources.getColor(R.color.common_bt_text_dark, null))
			btChange.setBackgroundResource(R.drawable.bg_common_bt_dark)
			btChange.setTextColor(resources.getColor(R.color.common_bt_text_dark, null))
		}
	}

	private fun resetDifficulty(countPerSide: Int) {
		Log.d(TAG, "resetDifficulty: $countPerSide")
		viewBinding.findDiffColor.resetCount(countPerSide)
		resetColor()
	}

	private fun resetColor() {
		val baseColor = ColorUtils.randomHSBColor(0f, 0f, 0.2f)
		val colorDiff = colorDiff
		Log.d(
			TAG,
			"resetColor-colorDiff: $colorDiff"
		)
		val diffSmall = colorDiff / 20
		val diffS =
			if (baseColor[1] > 0.5f) baseColor[1] - diffSmall else baseColor[1] + diffSmall
		val diffB =
			if (baseColor[2] > 0.6f) baseColor[2] - colorDiff else baseColor[2] + colorDiff
		val diffColor = floatArrayOf(baseColor[0], diffS, diffB)
		viewBinding.findDiffColor.resetColor(HSB(baseColor), HSB(diffColor))
	}

	private fun showResult(result: Boolean) {
		//		tvHint.setText(result ? "猜中啦！" : "猜错啦！");
		if (result) {
			viewBinding.findDiffColor.showResult()
			checkAutoNext()
			//			new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
			//					.setTitle("被发现啦").setMessage("厉害呀~这都能找到~")
			//					.setPositiveButton("继续", (dialog, which) -> dialog.cancel())
			//					.setOnCancelListener(dialog -> resetColor())
			//					.create().show();
		}
	}

	private fun checkAutoNext() {
		if (viewBinding.cbAutoNext.isChecked) {
			lifecycleScope.launch {
				delay(600)
				launch(Dispatchers.Main) {
					resetColor()
				}
			}
		}
	}

	private val countPerSide: Int
		get() = viewModel.currentLevel.value!! + 2

	private val colorDiff: Float
		get() {
			val currentLevel = viewModel.currentLevel.value!!
			Log.d(
				TAG,
				"getColorDiff: " + currentLevel + " - " + 0.3f / (currentLevel * 10)
			)
			return 0.02f + 0.5f / (currentLevel * 10)
		}

	override fun needBGM(): Boolean {
		return true
	}

}