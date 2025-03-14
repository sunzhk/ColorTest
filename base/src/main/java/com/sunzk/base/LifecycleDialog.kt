package com.sunzk.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * 实现了生命周期的dialog
 */
open class LifecycleDialog : AppCompatDialog, LifecycleOwner {

	companion object {
		private const val TAG: String = "LifecycleDialog"
	}

	private val mLifecycleRegistry by lazy { LifecycleRegistry(this) }

	constructor(context: Context) : super(context, R.style.LifecycleDialogStyle)
	constructor(context: Context, theme: Int) : super(context, theme)
	constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener) : super(context, cancelable, cancelListener)

	init {
		mLifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mLifecycleRegistry.currentState = Lifecycle.State.CREATED
	}

	override fun onStart() {
		super.onStart()
		mLifecycleRegistry.currentState = Lifecycle.State.STARTED
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		mLifecycleRegistry.currentState = Lifecycle.State.RESUMED
	}

	override fun cancel() {
		super.cancel()
		mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED
	}

	override fun dismiss() {
		super.dismiss()
		mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED
	}

	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED
	}

	override val lifecycle: Lifecycle
		get() = mLifecycleRegistry

	fun setGravityCenter(width: Int, height: Int) {
		setGravity(width, height, Gravity.CENTER)
	}

	fun setGravity(width: Int, height: Int, gravity: Int) {
		window?.let { window ->
			window.decorView.setPadding(0, 0, 0, 0)
			val layoutParams = window.attributes
			layoutParams.width = width
			layoutParams.height = height
			layoutParams.horizontalMargin = 0f
			layoutParams.gravity = gravity
			window.attributes = layoutParams
		}
	}
}