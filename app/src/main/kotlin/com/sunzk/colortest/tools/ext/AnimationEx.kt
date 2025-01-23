package com.sunzk.demo.tools.ext

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import androidx.annotation.AnimRes

fun View.startAnimation(@AnimRes resId: Int, setting: ((animation: Animation) -> Unit?)? = null) {
	val animation = AnimationUtils.loadAnimation(context, resId)
	setting?.invoke(animation)
	startAnimation(animation)
}

fun View.bottomIn(duration: Long, interceptor: ((animation: Animation) -> Boolean)? = null) {
	this.animation?.let {
		if (!it.hasEnded()) {
			return@bottomIn
		}
	}
	val animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
		Animation.RELATIVE_TO_SELF, 0f,
		Animation.RELATIVE_TO_SELF, 1.0f,
		Animation.RELATIVE_TO_SELF, 0f)
	animation.duration = duration
	interceptor?.let {
		if (it.invoke(animation)) {
			return@bottomIn
		}
	}
	this.visibility = View.VISIBLE
	startAnimation(animation)
}

fun View.bottomOut(duration: Long, interceptor: ((animation: Animation) -> Boolean)? = null) {
	this.animation?.let { 
		if (!it.hasEnded()) {
			return@bottomOut
		}
	}
	val animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, 
		Animation.RELATIVE_TO_SELF, 0f, 
		Animation.RELATIVE_TO_SELF, 0f, 
		Animation.RELATIVE_TO_SELF, 1.0f)
	animation.duration = duration
	animation.setAnimationListener(object: Animation.AnimationListener {
		override fun onAnimationStart(animation: Animation?) {}

		override fun onAnimationEnd(animation: Animation?) {
			visibility = View.GONE
		}

		override fun onAnimationRepeat(animation: Animation?) {}

	})
	interceptor?.let {
		if (it.invoke(animation)) {
			return@bottomOut
		}
	}
	startAnimation(animation)
}