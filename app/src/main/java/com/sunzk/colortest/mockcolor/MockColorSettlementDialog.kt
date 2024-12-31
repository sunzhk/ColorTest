package com.sunzk.colortest.mockcolor

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.blankj.utilcode.util.ScreenUtils
import com.sunzk.base.LifecycleDialog
import com.sunzk.base.expand.bindView
import com.sunzk.base.expand.onClick
import com.sunzk.colortest.R
import com.sunzk.colortest.databinding.DialogMockColorSettlementBinding
import com.sunzk.colortest.db.bean.MockColorResult

class MockColorSettlementDialog(
	context: Context, 
	private val question: FloatArray,
	private val answer: FloatArray,
	private val difficulty: MockColorResult.Difficulty) : LifecycleDialog(context) {
	companion object {
		private const val TAG: String = "MockColorSettlementDialog"
	}

	private val viewBinding by bindView<DialogMockColorSettlementBinding>()
	
	var onConfirmClickListener: (() -> Unit)? = null
	var onCancelClickListener: (() -> Unit)? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		window?.setLayout(ScreenUtils.getScreenWidth() * 2 / 3, ViewGroup.LayoutParams.WRAP_CONTENT)
		setCancelable(false)
		setCanceledOnTouchOutside(false)
		initView()
	}

	private fun initView() = with(viewBinding) {
		val isRight = difficulty.isRight(question, answer)
		ivIcon.setImageResource(if (isRight) R.mipmap.icon_answer_success else R.mipmap.icon_answer_fail)
		tvTitle.text = if (isRight) "答对了，可喜可贺" else "打错了，再接再厉"
		btCancel.onClick {
			dismiss()
			onCancelClickListener?.invoke()
		}
		btConfirm.onClick {
			dismiss()
			onConfirmClickListener?.invoke()
		}
	}

}