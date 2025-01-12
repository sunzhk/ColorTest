package com.sunzk.colortest.tools

import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.arcsoft.closeli.utils.takeIfIs
import com.blankj.utilcode.util.ScreenUtils
import com.sunzk.base.expand.emitBy
import com.sunzk.base.expand.onClick
import com.sunzk.colortest.R
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.databinding.FloatingSettingBinding
import com.sunzk.demo.tools.ext.dp2px
import java.lang.ref.SoftReference

/**
 * 悬浮设置窗管理器
 */
object FloatingSettingWindowManager {
	
	private const val TAG: String = "FloatingSettingWindowManager"

	private val X_START = 10f.dp2px
	private val Y_START = 10f.dp2px
	private val X_END = ScreenUtils.getScreenWidth() - X_START
	private val Y_END = ScreenUtils.getScreenHeight() - Y_START
	private var floatingView: SoftReference<FloatingSettingBinding>? = null

	/**
	 * 记录一下定位，在重建的时候还原现场
	 */
	private val location = Point(X_START, Y_START + 40.dp2px)

	/**
	 * 记录一下展开状态，在重建的时候还原现场
	 */
	private var isFold = true

	@SuppressLint("ClickableViewAccessibility")
	private val onTouch = View.OnTouchListener { v, event ->
		floatingView?.get()?.root?.let { view ->
			val rect = Rect()
			view.getGlobalVisibleRect(rect)
			if (!rect.contains(event.x.toInt(), event.y.toInt())) {
				fold()
			}
		}
		false
	}
	
	/**
	 * 把组件挂载到Activity上
	 */
	fun attach(activity: ComponentActivity) {
		Log.d(TAG, "FloatingSettingWindowManager#attach")
		val view = floatingView?.get()?.root?.takeIf { it.tag == activity.hashCode() } ?: createNewView(activity).root
		if (view.parent != null) {
			return
		}
		Log.d(TAG, "FloatingSettingWindowManager#attach- real attach")
		val layoutParams = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
		layoutParams.marginStart = location.x
		layoutParams.topMargin = location.y
		activity.window.decorView.findViewById<FrameLayout>(android.R.id.content)?.addView(view, layoutParams)
		activity.window.decorView.setOnTouchListener(onTouch) 
	}

	/**
	 * 把组件从Activity上卸载
	 */
	fun detach(activity: ComponentActivity) {
		Log.d(TAG, "FloatingSettingWindowManager#detach")
		activity.window.decorView.findViewById<ConstraintLayout>(R.id.cl_floating_setting)
			?.takeIf { it.tag == activity.hashCode() }
			?.let { clFloatingSetting ->
				activity.window.decorView.setOnTouchListener(null)
				clFloatingSetting.parent.takeIfIs<ViewGroup>()?.removeView(clFloatingSetting)
			}
	}
	
	private fun createNewView(activity: ComponentActivity): FloatingSettingBinding {
		Log.d(TAG, "FloatingSettingWindowManager#createNewView")
		return FloatingSettingBinding.inflate(LayoutInflater.from(activity)).also { viewBinding ->
			floatingView = SoftReference(viewBinding)
			initView(viewBinding)
			viewBinding.root.tag = activity.hashCode()
		}
	}

	private fun initView(viewBinding: FloatingSettingBinding) = with(viewBinding) {
		val listener = ItemViewTouchListener(root)
		root.setOnTouchListener(listener)
		ivSetting.setOnTouchListener(listener)
		llSetting.setOnTouchListener(listener)
		if(isFold) {
			fold()
		} else {
			unfold()
		}
		ivClose.onClick { fold() }
		ivSetting.onClick {
			unfold()
		}
		llBackgroundMusic.isSelected = Runtime.globalBGMSwitch.value
		llBackgroundMusic.onClick { 
			llBackgroundMusic.isSelected = !Runtime.globalBGMSwitch.value
			Runtime.toggleGlobalBGMSwitch()
		}
		tvDarkMode.text = "深色模式：${Runtime.darkMode.text}"
		llDarkMode.onClick { 
			Runtime.nextDarkMode()
			tvDarkMode.text = "深色模式：${Runtime.darkMode.text}"
		}
	}
	
	private fun fold() {
		floatingView?.get()?.apply {
			isFold = true
			ivSetting.isVisible = true
			llSetting.isVisible = false
		}
	}
	
	private fun unfold() {
		floatingView?.get()?.apply {
			isFold = false
			ivSetting.isVisible = false
			llSetting.isVisible = true
		}
	}

	class ItemViewTouchListener(private val floatView: View) : View.OnTouchListener {
		private val TAG: String = "ItemViewTouchListener"
		private var x = 0
		private var y = 0
		private var movingFlag = false

		private var startX = 0
		private var startY = 0
		override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
			Log.d(TAG, "ItemViewTouchListener#onTouch- action=${motionEvent.action}")
			when (motionEvent.action) {
				MotionEvent.ACTION_DOWN -> {
					x = motionEvent.rawX.toInt()
					y = motionEvent.rawY.toInt()
					startX = x
					startY = y
					movingFlag = false
				}
				MotionEvent.ACTION_MOVE -> {
					val nowX = motionEvent.rawX.toInt()
					val nowY = motionEvent.rawY.toInt()
					val movedX = nowX - x
					val movedY = nowY - y
					x = nowX
					y = nowY
					floatView.updateLayoutParams<MarginLayoutParams> { 
						marginStart += movedX
						topMargin += movedY
						marginStart = marginStart.coerceIn(X_START, X_END)
						topMargin = topMargin.coerceIn(Y_START, Y_END)
						location.x = marginStart
						location.y = topMargin
						Log.d(TAG, "ItemViewTouchListener#onTouch- marginStart=$marginStart, topMargin=$topMargin")
					}
					movingFlag = true
				}
				MotionEvent.ACTION_UP -> {
					val nowX = motionEvent.rawX.toInt()
					val nowY = motionEvent.rawY.toInt()
					if ((nowX - startX) < 5 && (nowY - startY) < 5) {
						movingFlag = false
						return false
					}
					if (movingFlag) {
						movingFlag = false
						return true
					}
				}
				else -> {

				}
			}
			return false
		}
	}
}