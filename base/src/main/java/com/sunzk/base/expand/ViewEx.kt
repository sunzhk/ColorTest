package com.sunzk.base.expand

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.StateListDrawable
import android.text.Editable
import android.text.TextUtils
import android.view.TouchDelegate
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.sunzk.base.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

fun EditText.bindIconViewVisible(vararg views: View, init: Boolean = true, otherActionOnTextChanged: ((editable: Editable?) -> Unit?)? = null) {
	val action = {
		views.forEach { view ->
			view.visibility = if(this.isFocused && !TextUtils.isEmpty(this.text?.toString())) View.VISIBLE else View.GONE
		}
	}
	if (init) {
		action.invoke()
	}
	this.addTextChangedListener {
		otherActionOnTextChanged?.invoke(it)
		action.invoke()
	}
	this.setOnFocusChangeListener { v, hasFocus ->
		action.invoke()
	}
}

private var lastClickTimeGlobal: Long = 0

fun View.onClick(interval: Long = 500, action: ((v: View) -> Unit)?) {
	onClick(interval, false, action)
}

fun View.onClick(interval: Long = 500, useGlobalClickTime: Boolean = false, action: ((v: View) -> Unit)?) {
	val originView = this
	setOnClickListener(object : View.OnClickListener {
		var lastClickTime: Long = 0

		override fun onClick(v: View?) {
			val currentTime = System.currentTimeMillis()
			if (useGlobalClickTime) {
				if (interval <= 0 || currentTime - lastClickTimeGlobal > interval) {
					lastClickTimeGlobal = currentTime
					action?.invoke(originView)
				}
			} else {
				if (interval <= 0 || currentTime - lastClickTime > interval) {
					lastClickTime = currentTime
					action?.invoke(originView)
				}
			}
		}
	})
}

fun View.onDoubleClick(clickInterval: Long = 500, clickAction: ((v: View) -> Unit)? = null, doubleClickAction: ((v: View) -> Unit)?) {
	val originView = this
	setOnClickListener(object : View.OnClickListener {
		var lastClickTime: Long = 0
		var lastDoubleClickTime: Long = 0

		override fun onClick(v: View?) {
			val currentTime = System.currentTimeMillis()
			if (currentTime - lastClickTime > clickInterval) {
				lastClickTime = currentTime
				clickAction?.invoke(originView)
			} else if (currentTime - lastDoubleClickTime > clickInterval) {
				lastDoubleClickTime = currentTime
				doubleClickAction?.invoke(originView)
			}
		}
	})
}

fun View.onDoubleClick(clickInterval: Long = 500, doubleClickJudgment: Long = 300, clickAction: ((v: View) -> Unit)? = null, doubleClickAction: ((v: View) -> Unit)?) {
	val originView = this
	setOnClickListener(object : View.OnClickListener {
		var lastClickTime: Long = 0
		var lastDoubleClickTime: Long = 0

		override fun onClick(v: View?) {
			val currentTime = System.currentTimeMillis()
			if (currentTime - lastDoubleClickTime > clickInterval) {
				// 上一次双击已经冷却好了，看看能不能判定一下
				if (currentTime - lastClickTime < doubleClickJudgment) {
					// 距离上一次点击不足[doubleClickJudgment]，则判定为双击
					lastClickTime = currentTime
					lastDoubleClickTime = currentTime
					doubleClickAction?.invoke(originView)
					return
				}
			}
			// 不是双击，再判定一下是不是单击
			if (currentTime - lastClickTime > clickInterval) {
				lastClickTime = currentTime
				clickAction?.invoke(originView)
			}
		}
	})
}

fun View.view2Bitmap(): Bitmap {
	val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
	val canvas = Canvas(bitmap)
	draw(canvas)
	return bitmap
}

fun View.expandClickArea(expendSize: Int) {
	expandClickArea(expendSize, expendSize, expendSize, expendSize)
}

fun View.expandClickArea(expendLeft: Int, expendTop: Int, expendRight: Int, expendBottom: Int) {
	val targetView = this
	parent.takeIfIs<View>()
		?.let { parentView ->
			parentView.post {
				val checkBoxRect = Rect()
				targetView.getHitRect(checkBoxRect)
				checkBoxRect.left -= expendLeft
				checkBoxRect.top -= expendTop
				checkBoxRect.right += expendRight
				checkBoxRect.bottom += expendBottom
				parentView.touchDelegate = TouchDelegate(checkBoxRect, targetView)
			}
		}
}

inline fun RecyclerView.addOnScrolledListener(crossinline block: (recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit) {
	addOnScrollListener(object : RecyclerView.OnScrollListener() {
		override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
			block(recyclerView, dx, dy)
		}
	})
}

inline fun RecyclerView.addScrollStateListener(crossinline block: (recyclerView: RecyclerView, newState: Int) -> Unit) {
	addOnScrollListener(object : RecyclerView.OnScrollListener() {
		override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
			block(recyclerView, newState)
		}
	})
}

fun View.startAnimDrawable() {
	this.background?.takeIfIs<AnimationDrawable>()?.start()
	this.takeIfIs<ImageView>()?.drawable?.takeIfIs<AnimationDrawable>()?.start()
}

fun View.stopAnimDrawable() {
	this.background?.takeIfIs<AnimationDrawable>()?.stop()
	this.takeIfIs<ImageView>()?.drawable?.takeIfIs<AnimationDrawable>()?.stop()
}

fun TextView.startAnimDrawable(index:Int) {
	this.compoundDrawables[index]?.takeIfIs<StateListDrawable>()?.current?.takeIfIs<AnimationDrawable>()?.start()
}

fun TextView.stopAnimDrawable(index:Int) {
	this.compoundDrawables[index]?.takeIfIs<StateListDrawable>()?.current?.takeIfIs<AnimationDrawable>()?.stop()
}

fun View.isOverlapping(anotherView: View): Boolean {
	val view1Left = x
	val view1Top = y
	val view1Right = view1Left + width
	val view1Bottom = view1Top + height

	val view2Left = anotherView.x
	val view2Top = anotherView.y
	val view2Right = view2Left + anotherView.width
	val view2Bottom = view2Top + anotherView.height

	val horizontalOverlap = view1Left <= view2Right && view1Right >= view2Left
	val verticalOverlap = view1Top <= view2Bottom && view1Bottom >= view2Top

	return horizontalOverlap && verticalOverlap
}

/**
 * @param scope 支持自动消失时需要配置
 * @param seconds 几秒后自动消失
 */
fun View.showFromBottom(scope: CoroutineScope? = null, seconds:Int = 0):Boolean{
	if(isVisible)
		return false
	this.isVisible = true
	if(measuredHeight == 0){
		measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
		this.animate().setDuration(0).y(measuredHeight.toFloat()).start()
	}
	if(null != scope && seconds != 0){
		val timeJob = countDownByFlow(seconds, scope, onFinish = {
			hideToBottom()
		})
		setTag(R.id.tag_key_anim_job, timeJob)
	}
	setTag(R.id.tag_key_anim_show, "already show")
	this.animate().setDuration(400).translationYBy(-measuredHeight.toFloat()).setListener(null).start()
	return true
}

/**
 * 通过showFromBottom/hideToBottom方式处理的View, 当前是否可见
 */
fun View.isShowByAnim(): Boolean = getTag(R.id.tag_key_anim_show) != null

fun View.hideToBottom(duration: Long = 400):Boolean{
	if(!isVisible)
		return false
	(getTag(R.id.tag_key_anim_job) as? Job)?.cancel()
	setTag(R.id.tag_key_anim_show, null)
	this.animate().setDuration(duration).translationY(this.measuredHeight.toFloat()).setListener(object : AnimatorListenerAdapter() {
		override fun onAnimationEnd(animation: Animator) {
			super.onAnimationEnd(animation)
			this@hideToBottom.isVisible = false
		}
	}).start()
	return true
}

fun View.bringToFirst(){
	parent?.takeIfIs<ViewGroup>()?.let {
		val index = it.indexOfChild(this)
		if(0 == index)
			return@let
		it.removeView(this)
		it.addView( this, 0)
	}
}

@SuppressLint("ResourceType")
fun ViewStub.inflateLayout(@LayoutRes resId: Int): View?{
	return parent?.let {
		layoutResource = resId
		inflate()
	}
}

inline fun <T: View> Array<T>.updateLayoutParams(block: ViewGroup.LayoutParams.() -> Unit) {
	this.forEach { view ->
		view.updateLayoutParams(block)
	}
}