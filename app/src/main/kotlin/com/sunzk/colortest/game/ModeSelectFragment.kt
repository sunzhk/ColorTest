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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import com.alibaba.android.arouter.launcher.ARouter
import com.sunzk.base.expand.bindView
import com.sunzk.colortest.BaseFragment
import com.sunzk.colortest.R
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.databinding.FragmentModeSelectBinding
import com.sunzk.colortest.entity.ModeEntity
import com.sunzk.colortest.tools.ext.onClick
import java.util.ArrayList

class ModeSelectFragment: BaseFragment() {

	companion object {
		private const val TAG: String = "ModeSelectFragment"
	}

	private val viewBinding by bindView<FragmentModeSelectBinding>()
	private val modeEntityList: ArrayList<ModeEntity> = ArrayList()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initModeList()
	}

	private fun initModeList() = modeEntityList.addAll(Runtime.modeList)

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
		Hint(Modifier.constrainAs(hint) {
			bottom.linkTo(parent.bottom)
		}.padding(bottom = 30.dp))
	}
	
	@Composable
	private fun ModeList(modifier: Modifier) {
		LazyColumn(modifier) { 
			items(modeEntityList.size) { index -> 
				val modeEntity = modeEntityList[index]
				Text(text = modeEntity.title,
					fontSize = 21.sp,
					color = colorResource(R.color.common_bt_text),
					textAlign = TextAlign.Center,
					modifier = Modifier
						.padding(top = if (index == 0) 0.dp else 6.dp, bottom = if (index == modeEntityList.size - 1) 0.dp else 6.dp)
						.fillMaxWidth()
						.wrapContentHeight()
						.background(colorResource(R.color.common_bt_bg), RoundedCornerShape(5.dp))
						.border(1.dp, colorResource(R.color.common_bt_stroke), RoundedCornerShape(5.dp))
						.padding(10.dp)
						.onClick {
							val route = RouteInfo.RouteMap.valueOf(modeEntity.path)
							if (route == RouteInfo.RouteMap.ActivityMockColor) {
								findNavController().navigate(R.id.action_modeSelect_to_mockColor)
								return@onClick
							}
							Log.d(TAG, "handleModeByAction: route=$route, path=${route.path}")
							var postcard = ARouter.getInstance().build(route.path)
							postcard.group = route.group
							modeEntity.bundle?.forEach { entry: Map.Entry<String, String> ->
								postcard = postcard.withString(entry.key, entry.value)
							}
							postcard.navigation(requireContext())
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