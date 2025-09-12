package com.sunzk.colortest.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Spannable
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.StringRes
import com.sunzk.base.expand.takeIfIs
import com.sunzk.base.LifecycleDialog
import com.sunzk.base.expand.bindView
import com.sunzk.colortest.R
import com.sunzk.colortest.databinding.DialogCommonAlertBinding
import com.sunzk.demo.tools.ext.dp2px

/**
 * 通用提示框，有一个确认按钮和一个取消按钮
 */
class CommonAlertDialog(context: Context, build: Builder.() -> Unit) : LifecycleDialog(context,
                                                                                         R.style.LifecycleDialogStyle) {

	val viewBinding by bindView<DialogCommonAlertBinding>()
	val builder: Builder = Builder(context)

	init {
		build.invoke(builder)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		context.resources.run {
			setGravityCenter(builder.width, builder.height)
		}
		viewBinding.tvTitle.text = builder.title
		viewBinding.tvTitle.gravity = builder.titleGravity
		viewBinding.tvMessage.text = builder.message
		viewBinding.tvMessage.gravity = builder.messageGravity
		if (builder.message is Spannable) {
			viewBinding.tvMessage.movementMethod = LinkMovementMethod.getInstance()
		}
		viewBinding.btCancel.text = builder.leftButtonText
		viewBinding.btConfirm.text = builder.rightButtonText
		if (TextUtils.isEmpty(builder.message)) {
			viewBinding.tvMessage.layoutParams.takeIfIs<ViewGroup.MarginLayoutParams>()?.topMargin = 5.dp2px
		}
		val dialogInterface = object : DialogInterface {
			override fun cancel() {
				this@CommonAlertDialog.cancel()
			}
			override fun dismiss() {
				this@CommonAlertDialog.dismiss()
			}
		}
		viewBinding.btCancel.setOnClickListener { view ->
			builder.leftButtonOnClickListener?.invoke(view, dialogInterface) ?: dismiss()
		}
		viewBinding.btConfirm.setOnClickListener { view ->
			builder.rightButtonOnClickListener?.invoke(view, dialogInterface) ?: dismiss()
		}
	}

	class Builder(val context: Context) {

		var width: Int = context.resources.getDimension(R.dimen.common_dialog_width).toInt()
		var height: Int = WindowManager.LayoutParams.WRAP_CONTENT
		var title: String? = null
		var titleGravity: Int = Gravity.CENTER
		var message: CharSequence? = null
		var messageGravity: Int = Gravity.CENTER
		var rightButtonText: String? = context.resources.getString(R.string.common_confirm)
		var rightButtonOnClickListener: ((view: View, dialog: DialogInterface) -> Unit)? = null
		var leftButtonText: String? = context.resources.getString(R.string.common_cancel)
		var leftButtonOnClickListener: ((view: View, dialog: DialogInterface) -> Unit)? = null

		fun setTitle(@StringRes stringId: Int) {
			this.title = context.resources.getString(stringId)
		}

		fun setMessage(@StringRes stringId: Int) {
			this.message = context.resources.getString(stringId)
		}

		fun setRightButtonText(@StringRes stringId: Int) {
			this.rightButtonText = context.resources.getString(stringId)
		}

		fun setLeftButtonText(@StringRes stringId: Int) {
			this.leftButtonText = context.resources.getString(stringId)
		}
		
	}

}