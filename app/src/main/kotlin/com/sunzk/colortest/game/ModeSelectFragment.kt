package com.sunzk.colortest.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.sunzk.colortest.BaseFragment
import com.sunzk.colortest.BuildConfig
import com.sunzk.colortest.R
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.compose.ui.common.CommonAlertDialog
import com.sunzk.colortest.tools.ext.onClick
import com.sunzk.colortest.update.UpdateDialog
import com.sunzk.colortest.update.UpdateManager
import com.sunzk.colortest.update.bean.VersionInfo
import com.sunzk.compose.clickableWithoutIndication
import kotlinx.coroutines.launch

class ModeSelectFragment: BaseFragment() {

	companion object {
		private const val TAG: String = "ModeSelectFragment"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return ComposeView(requireContext()).apply { 
			setContent {
				Page()
			}
		}
	}
	
	@Preview
	@Composable
	private fun Page() = ConstraintLayout(Modifier.padding(20.dp)) { 
		val (modeList, hint) = createRefs()
		val updateDialogFlag = remember { mutableStateOf<VersionInfo?>(null) }
		val updateScope = rememberCoroutineScope()
		ModeList(Modifier.constrainAs(modeList) {
			top.linkTo(parent.top)
			bottom.linkTo(hint.top)
		})
		Hint(updateDialogFlag, Modifier
			.constrainAs(hint) {
				bottom.linkTo(parent.bottom)
			}
			.padding(bottom = 30.dp))
		updateDialogFlag.value?.let { versionInfo ->
			UpdateDialog(versionInfo, onDismissRequest = { updateDialogFlag.value = null })
		}
	}
	
	@Composable
	private fun ModeList(modifier: Modifier) {
		LazyColumn(modifier) { 
			val gameMap = RouteInfo.GameMap.entries
			items(gameMap.size) { index -> 
				val modeEntity = gameMap[index]
				Text(text = modeEntity.modeName,
					fontSize = 21.sp,
					color = colorResource(R.color.common_bt_text),
					textAlign = TextAlign.Center,
					modifier = Modifier
						.padding(top = if (index == 0) 0.dp else 6.dp, bottom = if (index == gameMap.size - 1) 0.dp else 6.dp)
						.fillMaxWidth()
						.wrapContentHeight()
						.background(colorResource(R.color.common_bt_bg), RoundedCornerShape(5.dp))
						.border(1.dp, colorResource(R.color.common_bt_stroke), RoundedCornerShape(5.dp))
						.padding(10.dp)
						.onClick {
							Log.d(TAG, "handleModeByAction: name=${modeEntity.modeName}, action=${modeEntity.navigationAction}")
							if (modeEntity.navigationAction > 0) {
								findNavController().navigate(modeEntity.navigationAction)
								return@onClick
							}
						})
			}
		}
	}
	
	@Composable
	private fun Hint(updateDialogFlag: MutableState<VersionInfo?>, modifier: Modifier) = Column(modifier = modifier.then(Modifier.fillMaxWidth().wrapContentHeight())) {
		Text(text = stringResource(R.string.hint_send_suggestion_to_mail),
			fontSize = 18.sp,
			color = colorResource(R.color.common_text_hint),
			textAlign = TextAlign.Center,
			modifier = Modifier
				.padding(bottom = 10.dp)
				.fillMaxWidth()
				.wrapContentHeight())
		val updateVersion by UpdateManager.updateVersion.collectAsStateWithLifecycle()
		if (updateVersion == null) {
			Text(text = stringResource(R.string.hint_current_is_latest_version, "${BuildConfig.BUILD_TYPE}_v${BuildConfig.VERSION_NAME}"),
				fontSize = 18.sp,
				color = colorResource(R.color.common_text_hint),
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.wrapContentHeight())
		} else {
			Row(modifier = Modifier.wrapContentSize().align(Alignment.CenterHorizontally)) {
				Box(modifier = Modifier.padding(end = 5.dp).size(7.dp).background(Color.Red, RoundedCornerShape(3.5.dp)).align(Alignment.CenterVertically))
				Text(text = stringResource(R.string.hint_find_new_version, updateVersion?.name ?: "unknown"),
					fontSize = 18.sp,
					color = colorResource(R.color.common_text_hint),
					textAlign = TextAlign.Center,
					modifier = Modifier
						.wrapContentSize()
						.onClick {
							if (UpdateManager.isUpdating) {
								ToastUtils.showShort("正在下载更新，请稍后再试")
							} else {
								updateDialogFlag.value = updateVersion
							}
						})
			}
		}
	}

}