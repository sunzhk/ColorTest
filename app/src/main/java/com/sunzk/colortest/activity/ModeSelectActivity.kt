package com.sunzk.colortest.activity

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.sunzk.base.expand.bindView
import com.sunzk.base.utils.AppUtils
import com.sunzk.base.utils.DisplayUtil
import com.sunzk.base.utils.Logger
import com.sunzk.colortest.*
import com.sunzk.colortest.databinding.ActivityModeSelectBinding
import com.sunzk.colortest.databinding.ItemModeBinding
import com.sunzk.colortest.entity.ModeEntity
import com.sunzk.colortest.view.DragSwipeCallback
import com.sunzk.colortest.view.IDragSwipe
import com.sunzk.colortest.view.SpaceItemDecoration
import java.util.*

@Route(path = RouteInfo.PATH_ACTIVITY_MODE_SELECT)
class ModeSelectActivity : BaseActivity() {

	companion object {
		private const val TAG = "ModeSelectActivity"
	}

	private val viewBinding by bindView<ActivityModeSelectBinding>()
	private val modeEntityList: ArrayList<ModeEntity> = ArrayList()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (!checkAccess()) {
			showAlertDialog()
			return
		}
		showVersionUpgradeDialog()
		initModeList()
	}

	private fun initModeList() {
		modeEntityList.addAll(Runtime.modeList)
		viewBinding.rvModeList.layoutManager = LinearLayoutManager(
			this,
			LinearLayoutManager.VERTICAL,
			false
		)
		val itemDecoration = DisplayUtil.dip2px(this, 6f)
		viewBinding.rvModeList.addItemDecoration(
			SpaceItemDecoration(
				0,
				itemDecoration,
				0,
				itemDecoration
			)
		)
		val adapter: RecyclerView.Adapter<ModeViewHolder> =
			object : RecyclerView.Adapter<ModeViewHolder>() {
				override fun onCreateViewHolder(
					parent: ViewGroup,
					viewType: Int
				): ModeViewHolder {
					val inflate = LayoutInflater.from(this@ModeSelectActivity)
						.inflate(R.layout.item_mode, parent, false)
					return ModeViewHolder(inflate)
				}

				override fun onBindViewHolder(
					holder: ModeViewHolder,
					position: Int
				) {
					holder.bindData(modeEntityList[position])
				}

				override fun getItemCount(): Int {
					return modeEntityList.size
				}
			}
		viewBinding.rvModeList.adapter = adapter
//		val callback: ItemTouchHelper.Callback = DragSwipeCallback(object : IDragSwipe {
//			override fun onItemSwapped(fromPosition: Int, toPosition: Int) {
//				Logger.d(
//					TAG,
//					"ModeSelectActivity#onItemSwapped- ",
//					fromPosition,
//					toPosition
//				)
//				if (fromPosition < toPosition) {
//					for (i in fromPosition until toPosition) {
//						Collections.swap(modeEntityList, i, i + 1)
//					}
//				} else {
//					for (i in fromPosition downTo toPosition + 1) {
//						Collections.swap(modeEntityList, i, i - 1)
//					}
//				}
//				adapter.notifyItemMoved(fromPosition, toPosition)
//				refreshModeEntityListInDataStore()
//			}
//
//			override fun onItemDeleted(position: Int) {
//				Logger.d(
//					TAG,
//					"ModeSelectActivity#onItemDeleted- ",
//					position
//				)
//			}
//
//			override fun onItemDone(position: Int) {
//				Logger.d(
//					TAG,
//					"ModeSelectActivity#onItemDone- ",
//					position
//				)
//				modeEntityList.removeAt(position)
//				adapter.notifyItemRemoved(position)
//				refreshModeEntityListInDataStore()
//			}
//
//			private fun refreshModeEntityListInDataStore() {
//				Runtime.modeList = modeEntityList
//			}
//		})
//		val touchHelper = ItemTouchHelper(callback)
//		touchHelper.attachToRecyclerView(viewBinding.rvModeList)
	}

	private fun checkAccess(): Boolean {
		//		Date dateNow = new Date(System.currentTimeMillis());
		//		Calendar compare = Calendar.getInstance();
		//		compare.set(2021, 9, 18, 0, 0);
		//		Date dateCompare = compare.getTime();
		//		Log.d(TAG, "checkAccess: " + dateNow + " , " + dateCompare);
		//		return dateNow.before(dateCompare);
		return true
	}

	/**
	 * 测试版本的情况下，显示提示框
	 */
	private fun showAlertDialog() {
		val alertDialog =
			AlertDialog.Builder(this).setTitle("过期啦")
				.setMessage("本程序为试用版，请联系开发者获取最新版本\nmail:312797831@qq.com").setPositiveButton(
					"好吧"
				) { dialog: DialogInterface?, which: Int -> finish() }.create()
		alertDialog.setCancelable(false)
		alertDialog.setCanceledOnTouchOutside(false)
		alertDialog.show()
	}

	private fun showVersionUpgradeDialog() {
		val sharedPreferences =
			getSharedPreferences("versionHint", Context.MODE_PRIVATE)
		val versionCode = AppUtils.getVersionCode(this)
		val lastHintVersionCode = sharedPreferences.getInt("lastHintVersionCode", -1)
		//		String upgradeMessage = "    已更新至正式版，可以尽情使用了";
//		val upgradeMessage = """    1. 大幅度修改了项目结构，为以后的更新做准备~
//    2. 找中间色功能增加了寂书予定制版
//    再次感谢可爱的寂书予~"""
		val upgradeMessage = """    1. 增加了历史记录功能~
    再次感谢可爱的寂书予~"""
		if (versionCode > lastHintVersionCode) {
			AlertDialog.Builder(this)
				.setTitle("v" + AppUtils.getVersionName(this))
				.setMessage("更新说明:\n$upgradeMessage\n\n    design by 寂书予\n    develop by 作死菌")
				.setPositiveButton(
					"知道了"
				) { dialog: DialogInterface, which: Int -> dialog.dismiss() }.create().show()
		}
		sharedPreferences.edit().putInt("lastHintVersionCode", versionCode).apply()
	}

	internal inner class ModeViewHolder(itemView: View) :
		RecyclerView.ViewHolder(itemView) {
		private val itemViewBinding: ItemModeBinding = ItemModeBinding.bind(itemView)
		fun bindData(modeEntity: ModeEntity) {
			itemViewBinding.tvTitle.text = modeEntity.title
			itemView.setOnClickListener {
				handleModeByAction(modeEntity)
			}
		}

		private fun handleModeByAction(modeEntity: ModeEntity) {
			val route = RouteInfo.RouteMap.valueOf(modeEntity.path)
			Log.d(TAG, "handleModeByAction: route=$route, path=${route.path}")
			var postcard = ARouter.getInstance().build(route.path)
			postcard.group = route.group
			modeEntity.bundle?.forEach { entry: Map.Entry<String, String> ->
				postcard = postcard.withString(entry.key, entry.value)
			}

			postcard.navigation(this@ModeSelectActivity)
		}

	}
}