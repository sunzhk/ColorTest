package com.sunzk.colortest.findDiff

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.sunzk.base.expand.bindView
import com.sunzk.base.expand.collect
import com.sunzk.base.expand.coroutines.GlobalDispatchers
import com.sunzk.base.expand.livedata.dec
import com.sunzk.base.expand.livedata.inc
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.databinding.ActivityFindDiffColorBinding
import com.sunzk.colortest.entity.HSB
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
		bindData()
	}

	private fun initViews() = with(viewBinding) {
		initControllerView()
		btChange.setOnClickListener { v: View? -> resetColor() }
		findDiffColor.setOnDiffColorViewClickListener { view, result ->
			Log.d(TAG, "onClick: $result")
			showResult(result)
		}
	}

	private fun initControllerView() {
		viewBinding.ivLevelLeft.setOnClickListener {
			val currentLevel: Int = viewModel.currentGameData.value.level
			if (currentLevel > FindDiffColorViewModel.MIN_LEVEL) {
				viewModel.changeLevel(currentLevel - 1)
			}
		}
		viewBinding.ivLevelRight.setOnClickListener {
			val currentLevel: Int = viewModel.currentGameData.value.level
			if (currentLevel < FindDiffColorViewModel.MAX_LEVEL) {
				viewModel.changeLevel(currentLevel + 1)
			}
		}
	}

	private fun bindData() {
		viewModel.currentGameData.collect(lifecycleScope) { data ->
			Log.d(TAG, "FindDiffColorActivity#bindData- new data: $data")
			viewBinding.tvLevel.text = "${data.level}"
			if (viewBinding.findDiffColor.countPerSide != data.countPerSide) {
				viewBinding.findDiffColor.resetCount(data.countPerSide)
			}
			viewBinding.findDiffColor.resetColor(data.baseColor, data.diffColor, data.diffIndex)
		}
	}
	
	private fun resetColor() {
		Log.d(TAG, "FindDiffColorActivity#resetColor")
		viewModel.nextRandomData()
	}

	private fun showResult(result: Boolean) {
		if (result) {
			viewBinding.findDiffColor.showResult()
			lifecycleScope.launch(GlobalDispatchers.Main) {
				delay(600)
				resetColor()
			}
//			new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
//					.setTitle("被发现啦").setMessage("厉害呀~这都能找到~")
//					.setPositiveButton("继续", (dialog, which) -> dialog.cancel())
//					.setOnCancelListener(dialog -> resetColor())
//					.create().show();
		}
	}

	override fun needBGM(): Boolean {
		return true
	}

}