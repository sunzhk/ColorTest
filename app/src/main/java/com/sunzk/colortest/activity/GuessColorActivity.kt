package com.sunzk.colortest.activity

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.sunzk.base.expand.livedata.get
import com.sunzk.base.expand.livedata.set
import com.sunzk.base.utils.AppUtils
import com.sunzk.base.utils.ColorUtils
import com.sunzk.base.utils.DisplayUtil
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.databinding.ActivityGuessColorBinding
import java.util.*

class GuessColorActivity : BaseActivity() {

	companion object {
		const val INTENT_KEY_DIFFICULTY = "difficulty"
		const val INTENT_KEY_DIFFICULTY_NAME = "difficultyName"
		const val INTENT_KEY_COLOR_H_DIFF = "colorHDifferencePercent"
		const val INTENT_KEY_COLOR_S_DIFF = "colorSDifferencePercent"
		const val INTENT_KEY_COLOR_B_DIFF = "colorBDifferencePercent"
		const val INTENT_KEY_MIN_S_B = "minSBPercent"
		const val INTENT_KEY_MIN_ALLOW_DEVIATION = "allowDeviation"
	}

	private val TAG: String = "GuessColorActivity"

	private lateinit var viewBinding: ActivityGuessColorBinding
	private val viewModel: GuessColorViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewBinding = ActivityGuessColorBinding.inflate(layoutInflater)
		setContentView(viewBinding.root)

		initDifficulty()

		Log.d(TAG, "onCreate: ${viewModel.difficulty}")
		viewBinding.hsbColorSelector.setOnColorSelectedListener { h: Float, s: Float, b: Float ->
			val colorArray = floatArrayOf(h, s, b)
			viewModel.centerColor.postValue(colorArray)
		}
		viewBinding.btNext.setOnClickListener { nextQuestion() }
		viewBinding.btAnswer.setOnClickListener { showAnswer() }
		viewModel.showAnswerFlag.observe(this, androidx.lifecycle.Observer { t ->
			viewBinding.tvAnswer.visibility = if (t) View.VISIBLE else View.INVISIBLE
		})
		initColorContentView()
		nextQuestion()
	}

	/**
	 * 从Intent中初始化难度数据
	 */
	private fun initDifficulty() {
		try {
			val dif: Any? = intent.getSerializableExtra(INTENT_KEY_DIFFICULTY)
			if (dif is Difficulty) {
				viewModel.difficulty = DifficultyEntity(dif)
				Log.d(TAG, "initDifficulty by diff enum ${dif.name}")
			} else if (dif is String) {
				viewModel.difficulty = DifficultyEntity(Difficulty.valueOf(dif))
				Log.d(TAG, "initDifficulty by diff name $dif")
			} else if (dif == null) {
				val difficultyName = intent.getStringExtra(INTENT_KEY_DIFFICULTY_NAME)
				val colorHDifferencePercent = intent.getStringExtra(INTENT_KEY_COLOR_H_DIFF)
				val colorSDifferencePercent = intent.getStringExtra(INTENT_KEY_COLOR_S_DIFF)
				val colorBDifferencePercent = intent.getStringExtra(INTENT_KEY_COLOR_B_DIFF)
				val minSBPercent = intent.getStringExtra(INTENT_KEY_MIN_S_B)
				val allowDeviation = intent.getStringExtra(INTENT_KEY_MIN_ALLOW_DEVIATION)
				viewModel.difficulty = DifficultyEntity(
					difficultyName!!,
					colorHDifferencePercent!!.toInt(),
					colorSDifferencePercent!!.toInt(),
					colorBDifferencePercent!!.toInt(),
					minSBPercent!!.toInt(),
					allowDeviation!!.toInt())
				Log.d(TAG, "initDifficulty by params: $difficultyName, " +
						"$colorHDifferencePercent, " +
						"$colorSDifferencePercent, " +
						"$colorBDifferencePercent, " +
						"$minSBPercent, " +
						"$allowDeviation")
			}
		} catch (t: Throwable) {
			Log.d(TAG, "initDifficulty: error", t)
			viewModel.difficulty = DifficultyEntity(Difficulty.EASY)
		}
	}

	override fun needBGM(): Boolean {
		return true
	}

	private fun initColorContentView() {
		val spacing = DisplayUtil.dip2px(this, 25f)
		val sideLength = (AppUtils.getScreenWidth(this) - spacing * 4) / 3
		var radius = sideLength / 8
		val minRadius = DisplayUtil.dip2px(this, 10f)
		if (radius < minRadius) {
			radius = minRadius
		}
		viewBinding.flContent.setPadding(spacing, 0, spacing, 0)
		viewBinding.cdColorLeft.radius = radius.toFloat()
		viewBinding.cdColorLeft.layoutParams.width = sideLength
		viewBinding.cdColorLeft.layoutParams.height = sideLength
		viewBinding.cdColorCenter.radius = radius.toFloat()
		viewBinding.cdColorCenter.layoutParams.width = sideLength
		viewBinding.cdColorCenter.layoutParams.height = sideLength
		val layoutParams = viewBinding.cdColorCenter.layoutParams
		if (layoutParams is LinearLayout.LayoutParams) {
			layoutParams.leftMargin = spacing
			layoutParams.rightMargin = spacing
		}
		viewBinding.cdColorRight.radius = radius.toFloat()
		viewBinding.cdColorRight.layoutParams.width = sideLength
		viewBinding.cdColorRight.layoutParams.height = sideLength

		viewModel.leftColor.observe(this, androidx.lifecycle.Observer { t ->
			viewBinding.cdColorLeft.setCardBackgroundColor(Color.HSVToColor(t))
		})
		viewModel.rightColor.observe(this, androidx.lifecycle.Observer { t ->
			viewBinding.cdColorRight.setCardBackgroundColor(Color.HSVToColor(t))
		})
		viewModel.centerColor.observe(this, androidx.lifecycle.Observer { t -> 
			viewBinding.cdColorCenter.setCardBackgroundColor(Color.HSVToColor(t))
		})
	}

	private fun nextQuestion() {
		viewModel.setShowAnswer(false)
		val color = ColorUtils.randomHSBColor(
			0f,
			viewModel.difficulty.minSBPercent / 100f,
			viewModel.difficulty.minSBPercent / 100f
		)
		viewModel.leftColor[0] = color[0]
		viewModel.leftColor[1] = color[1]
		viewModel.leftColor[2] = color[2]

		val colorDifferencePercent: Float
		val diffColorIndex = Random().nextInt(3)
		val nextColor: Float
		when (diffColorIndex) {
			0 -> {
				colorDifferencePercent = viewModel.difficulty.colorHDifferencePercent * 1.0f
				nextColor = color[0] + colorDifferencePercent * 360 / 100f
				if (nextColor > 360) {
					viewModel.rightColor[0] = color[0] - colorDifferencePercent * 360 / 100f
				} else {
					viewModel.rightColor[0] = nextColor
				}
				viewModel.rightColor[1] = viewModel.leftColor[1]
				viewModel.rightColor[2] = viewModel.leftColor[2]
			}
			1 -> {
				colorDifferencePercent = viewModel.difficulty.colorSDifferencePercent * 1.0f
				nextColor = color[1] + colorDifferencePercent / 100f
				if (nextColor > 1) {
					viewModel.rightColor[1] = color[1] - colorDifferencePercent / 100f
				} else {
					viewModel.rightColor[1] = nextColor
				}
				viewModel.rightColor[0] = viewModel.leftColor[0]
				viewModel.rightColor[2] = viewModel.leftColor[2]
			}
			2 -> {
				colorDifferencePercent = viewModel.difficulty.colorBDifferencePercent * 1.0f
				nextColor = color[2] + colorDifferencePercent / 100f
				if (nextColor > 1) {
					viewModel.rightColor[2] = color[2] - colorDifferencePercent / 100f
				} else {
					viewModel.rightColor[2] = nextColor
				}
				viewModel.rightColor[0] = viewModel.leftColor[0]
				viewModel.rightColor[1] = viewModel.leftColor[1]
			}
			else -> {
			}
		}
		viewModel.centerColor[0] = (viewModel.leftColor[0] + viewModel.rightColor[0]) / 2
		viewModel.centerColor[1] = (viewModel.leftColor[1] + viewModel.rightColor[1]) / 2
		viewModel.centerColor[2] = (viewModel.leftColor[2] + viewModel.rightColor[2]) / 2
		Log.d(TAG, "nextQuestion-left: ${Arrays.toString(viewModel.leftColor.value)}")
		Log.d(TAG, "nextQuestion-center: ${Arrays.toString(viewModel.centerColor.value)}")
		Log.d(TAG, "nextQuestion-next: ${Arrays.toString(viewModel.rightColor.value)}")

		//		viewBinding.cdColorCenter.setCardBackgroundColor(Color.HSVToColor(centerColor));
		viewBinding.hsbColorSelector.isEnabled = true
		viewBinding.hsbColorSelector.reset(50)
	}

	private fun showAnswer() {
		val showHLeft = viewModel.leftColor[0].toInt()
		val showSLeft = (viewModel.leftColor[1] * 100).toInt()
		val showBLeft = (viewModel.leftColor[2] * 100).toInt()
		val showHCenter = viewModel.centerColor[0].toInt()
		val showSCenter = (viewModel.centerColor[1] * 100).toInt()
		val showBCenter = (viewModel.centerColor[2] * 100).toInt()
		val showHRight = viewModel.rightColor[0].toInt()
		val showSRight = (viewModel.rightColor[1] * 100).toInt()
		val showBRight = (viewModel.rightColor[2] * 100).toInt()
		viewBinding.hsbColorSelector.isEnabled = false
		viewModel.setShowAnswer(true)
		viewBinding.tvAnswer.text = String.format(
			Locale.getDefault(),
			"左侧: H: %d度  S: %d%%  B: %d%%\n中间: H: %d度  S: %d%%  B: %d%%\n右侧: H: %d度  S: %d%%  B: %d%%\n",
			showHLeft,
			showSLeft,
			showBLeft,
			showHCenter,
			showSCenter,
			showBCenter,
			showHRight,
			showSRight,
			showBRight
		)
		//		TestResultDataBase.getInstance(this).recordScore(showH, showS, showB, hsbColorSelector.getProgressH(), hsbColorSelector.getProgressS(), hsbColorSelector.getProgressB());
		//		showScore(Runtime.testResultNumber);
		//		checkAnswer();
		val difH =
			Math.abs(viewBinding.hsbColorSelector.colorH - viewModel.centerColor[0]) * 100f / 360f
		val difS =
			Math.abs(viewBinding.hsbColorSelector.colorS - viewModel.centerColor[1]) * 100f
		val difB =
			Math.abs(viewBinding.hsbColorSelector.colorB - viewModel.centerColor[2]) * 100f
		if (difH > viewModel.difficulty.allowDeviation || difS > viewModel.difficulty.allowDeviation || difB > viewModel.difficulty.allowDeviation) {
			AlertDialog.Builder(this)
				.setTitle("就这？")
				.setMessage("就这也敢玩地狱模式？")
				.setPositiveButton(
					"再来！"
				) { dialog: DialogInterface, which: Int -> dialog.cancel() }
				.create()
				.show()
		} else {
			AlertDialog.Builder(this)
				.setTitle("哎哟")
				.setMessage("哎哟不错哦")
				.setPositiveButton(
					"不愧是我"
				) { dialog: DialogInterface, which: Int -> dialog.cancel() }
				.create()
				.show()
		}
	}

	enum class Difficulty(
		val colorHDifferencePercent: Int,
		val colorSDifferencePercent: Int,
		val colorBDifferencePercent: Int,
		val minSBPercent: Int,
		val allowDeviation: Int
	) {
		EASY(40, 35, 35, 60, 15), MEDIUM(30, 30, 30, 40, 10), DIFFICULT(15, 25, 25, 20, 5);
	}

	data class DifficultyEntity(
		val name: String,
		val colorHDifferencePercent: Int,
		val colorSDifferencePercent: Int,
		val colorBDifferencePercent: Int,
		val minSBPercent: Int,
		val allowDeviation: Int
	) {
		constructor(dif: Difficulty) : this(
			dif.name,
			dif.colorHDifferencePercent,
			dif.colorSDifferencePercent,
			dif.colorBDifferencePercent,
			dif.minSBPercent,
			dif.allowDeviation
		)
	}
}