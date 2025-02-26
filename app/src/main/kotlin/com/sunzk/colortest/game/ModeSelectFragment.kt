package com.sunzk.colortest.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.sunzk.colortest.BaseFragment
import com.sunzk.colortest.R
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.tools.ext.onClick

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
	
	@Composable
	private fun Page() = ConstraintLayout(Modifier.padding(20.dp)) { 
		val (modeList, hint) = createRefs()
		ModeList(Modifier.constrainAs(modeList) {
			top.linkTo(parent.top)
			bottom.linkTo(hint.top)
		})
		Hint(Modifier
			.constrainAs(hint) {
				bottom.linkTo(parent.bottom)
			}
			.padding(bottom = 30.dp))
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
	private fun Hint(modifier: Modifier) {
		Text(text = stringResource(R.string.hint_send_suggestion_to_mail),
			fontSize = 18.sp,
			color = colorResource(R.color.common_text_hint),
			textAlign = TextAlign.Center,
			modifier = modifier.then(Modifier
				.fillMaxWidth()
				.wrapContentHeight()))
	}

}