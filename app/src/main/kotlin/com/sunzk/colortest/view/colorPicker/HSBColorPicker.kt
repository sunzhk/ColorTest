package com.sunzk.colortest.view.colorPicker

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.sunzk.base.expand.collect
import com.sunzk.base.utils.ColorUtils
import com.sunzk.base.utils.NumberUtils
import com.sunzk.colortest.R
import com.sunzk.colortest.entity.HSB
import com.sunzk.colortest.tools.ext.dp2px

class HSBColorPicker : LinearLayout, IColorPicker {

    companion object {
        private const val TAG = "HSBColorPicker"
    }

    private val hueGradientDrawable: GradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        ColorUtils.HUE_COLOR_LIST,
    )

    private val saturationGradientDrawable: GradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(-0x1000000, -0x1000000),
    )

    private val valueGradientDrawable: GradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(-0x1000000, -0x1000000),
    )

    private var colorData: HBSColorPickerData = HBSColorPickerData()

    private lateinit var sbH: SeekBar
    private lateinit var sbS: SeekBar
    private lateinit var sbB: SeekBar
    private lateinit var tvH: EditText
    private lateinit var tvS: EditText
    private lateinit var tvB: EditText
    private lateinit var llHContainer1: LinearLayout
    private lateinit var llHContainer2: LinearLayout
    private lateinit var llSContainer1: LinearLayout
    private lateinit var llSContainer2: LinearLayout
    private lateinit var llBContainer1: LinearLayout
    private lateinit var llBContainer2: LinearLayout
    private lateinit var ivHFineTuningLeft: ImageView
    private lateinit var ivHFineTuningRight: ImageView
    private lateinit var ivSFineTuningLeft: ImageView
    private lateinit var ivSFineTuningRight: ImageView
    private lateinit var ivBFineTuningLeft: ImageView
    private lateinit var ivBFineTuningRight: ImageView

    override val pickerView: View
        get() = this

    override val hsb: HSB
        get() = HSB(
            sbH.progress.toFloat(),
            sbS.progress.toFloat(),
            sbB.progress.toFloat(),
        )

    override var onColorPick: ((HSB) -> Unit)? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs,
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        orientation = VERTICAL
        buildUi()
        initSeekBarArea()
    }

    private fun buildUi() {
        val ctx = context
        val seekBarHeight = resources.getDimensionPixelSize(R.dimen.view_hsb_color_selector_seek_bar_height)
        val seekBarPadding = resources.getDimensionPixelSize(R.dimen.view_hsb_color_selector_seek_bar_padding)
        val marginBottomRow = resources.getDimensionPixelSize(R.dimen.view_hsb_color_selector_seek_bar_margin_bottom)
        val marginBottomInput = resources.getDimensionPixelSize(R.dimen.view_hsb_color_selector_seek_bar_input_margin_bottom)
        val textSizePx = resources.getDimension(R.dimen.view_hsb_color_selector_seek_bar_text_size)
        val inputWidth = resources.getDimensionPixelSize(R.dimen.view_hsb_color_selector_seek_bar_input_width)
        val stepSize = resources.getDimensionPixelSize(R.dimen.view_hsb_color_selector_step_button_size)
        val stepPad = resources.getDimensionPixelSize(R.dimen.view_hsb_color_selector_step_button_padding)
        val fineMargin = resources.getDimensionPixelSize(R.dimen.view_hsb_color_selector_seek_bar_fine_tuning_margin)
        val marginEndH = (8f * resources.displayMetrics.density + 0.5f).toInt()
        val marginEndS = (10f * resources.displayMetrics.density + 0.5f).toInt()

        fun createSeekBar(maxProgress: Int): SeekBar = SeekBar(ctx).apply {
            id = View.generateViewId()
            max = maxProgress
            setPadding(seekBarPadding, seekBarPadding, seekBarPadding, seekBarPadding)
            ContextCompat.getDrawable(ctx, R.drawable.thumb2)?.let { thumb = it }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                splitTrack = false
            }
        }

        fun createStepButton(drawableRes: Int): ImageView = ImageView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(stepSize, stepSize).apply {
                marginStart = fineMargin
                marginEnd = fineMargin
            }
            setPadding(stepPad, stepPad, stepPad, stepPad)
            setImageResource(drawableRes)
            isClickable = true
            isFocusable = true
        }

        fun createChannelInput(): EditText = EditText(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(inputWidth, LayoutParams.WRAP_CONTENT)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
            setSingleLine(true)
            maxLines = 1
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            gravity = Gravity.CENTER
        }

        llHContainer1 = LinearLayout(ctx).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            clipToPadding = false
            clipChildren = false
        }
        val labelH = TextView(ctx).apply {
            text = "色相(H)　"
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
        }
        llHContainer1.addView(
            labelH,
            LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                marginEnd = marginEndH
            },
        )
        sbH = createSeekBar(360)
        llHContainer1.addView(
            sbH,
            LinearLayout.LayoutParams(0, seekBarHeight, 1f),
        )
        addView(
            llHContainer1,
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = marginBottomRow
            },
        )

        llHContainer2 = LinearLayout(ctx).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        ivHFineTuningLeft = createStepButton(R.drawable.seek_fine_tuning_left)
        tvH = createChannelInput()
        val degreeH = TextView(ctx).apply {
            text = "度"
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
        }
        ivHFineTuningRight = createStepButton(R.drawable.seek_fine_tuning_right)
        llHContainer2.addView(ivHFineTuningLeft)
        llHContainer2.addView(tvH)
        llHContainer2.addView(degreeH)
        llHContainer2.addView(ivHFineTuningRight)
        addView(
            llHContainer2,
            LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = marginBottomInput
            },
        )

        llSContainer1 = LinearLayout(ctx).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            clipToPadding = false
            clipChildren = false
        }
        val labelS = TextView(ctx).apply {
            text = "饱和度(S)"
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
        }
        llSContainer1.addView(
            labelS,
            LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                marginEnd = marginEndS
            },
        )
        sbS = createSeekBar(100)
        llSContainer1.addView(sbS, LinearLayout.LayoutParams(0, seekBarHeight, 1f))
        addView(
            llSContainer1,
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = marginBottomRow
            },
        )

        llSContainer2 = LinearLayout(ctx).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        ivSFineTuningLeft = createStepButton(R.drawable.seek_fine_tuning_left)
        tvS = createChannelInput()
        val percentS = TextView(ctx).apply {
            text = "%"
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
        }
        ivSFineTuningRight = createStepButton(R.drawable.seek_fine_tuning_right)
        llSContainer2.addView(ivSFineTuningLeft)
        llSContainer2.addView(tvS)
        llSContainer2.addView(percentS)
        llSContainer2.addView(ivSFineTuningRight)
        addView(
            llSContainer2,
            LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = marginBottomInput
            },
        )

        llBContainer1 = LinearLayout(ctx).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            clipToPadding = false
            clipChildren = false
        }
        val labelB = TextView(ctx).apply {
            text = "明度(B)　"
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
        }
        llBContainer1.addView(
            labelB,
            LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                marginEnd = marginEndS
            },
        )
        sbB = createSeekBar(100)
        llBContainer1.addView(sbB, LinearLayout.LayoutParams(0, seekBarHeight, 1f))
        addView(
            llBContainer1,
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = marginBottomRow
            },
        )

        llBContainer2 = LinearLayout(ctx).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        ivBFineTuningLeft = createStepButton(R.drawable.seek_fine_tuning_left)
        tvB = createChannelInput().apply {
            gravity = Gravity.CENTER
        }
        val percentB = TextView(ctx).apply {
            text = "%"
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
        }
        ivBFineTuningRight = createStepButton(R.drawable.seek_fine_tuning_right)
        llBContainer2.addView(ivBFineTuningLeft)
        llBContainer2.addView(tvB)
        llBContainer2.addView(percentB)
        llBContainer2.addView(ivBFineTuningRight)
        addView(
            llBContainer2,
            LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = marginBottomInput
            },
        )
    }

    private fun initSeekBarArea() {
        val strokeWidth = 1.dp2px
        val dimension = resources.getDimension(R.dimen.view_hsb_color_selector_seek_bar_height)
        hueGradientDrawable.cornerRadius = dimension / 2
        hueGradientDrawable.setStroke(strokeWidth, Color.BLACK)
        sbH.progressDrawable = hueGradientDrawable
        saturationGradientDrawable.cornerRadius = dimension / 2
        saturationGradientDrawable.setStroke(strokeWidth, Color.BLACK)
        sbS.progressDrawable = saturationGradientDrawable
        valueGradientDrawable.cornerRadius = dimension / 2
        valueGradientDrawable.setStroke(strokeWidth, Color.BLACK)
        sbB.progressDrawable = valueGradientDrawable

        tvH.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            colorData.setColorH(NumberUtils.parse(v.text.toString(), -1))
            false
        }
        tvS.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            colorData.setColorS(NumberUtils.parse(v.text.toString(), -1))
            false
        }
        tvB.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            colorData.setColorB(NumberUtils.parse(v.text.toString(), -1))
            false
        }
        sbH.max = 360
        sbS.max = 100
        sbB.max = 100
        val onSeekBarChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                i: Int,
                b: Boolean,
            ) {
                Log.d(
                    TAG,
                    "HSBColorPicker#onProgressChanged- ${resources.getResourceName(seekBar.id)} - $i - $b",
                )
                when (seekBar) {
                    sbH -> onSeekBarHDrag()
                    sbS -> onSeekBarSDrag()
                    sbB -> onSeekBarBDrag()
                    else -> {}
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
        sbH.setOnSeekBarChangeListener(onSeekBarChangeListener)
        sbS.setOnSeekBarChangeListener(onSeekBarChangeListener)
        sbB.setOnSeekBarChangeListener(onSeekBarChangeListener)
        ivHFineTuningLeft.setOnClickListener {
            Log.d(TAG, "initSeekBarArea: color H left click")
            colorData.colorH.value?.let {
                Log.d(TAG, "initSeekBarArea: color H = $it")
                colorData.setColorH(it - HSB.COLOR_H_INTERVAL)
            }
        }
        ivHFineTuningRight.setOnClickListener {
            Log.d(TAG, "initSeekBarArea: color H right click")
            colorData.colorH.value?.let {
                Log.d(TAG, "initSeekBarArea: color H = $it")
                colorData.setColorH(it + HSB.COLOR_H_INTERVAL)
            }
        }
        ivSFineTuningLeft.setOnClickListener {
            Log.d(TAG, "initSeekBarArea: color S left click")
            colorData.colorS.value?.let {
                Log.d(TAG, "initSeekBarArea: color S = $it")
                colorData.setColorS(it - HSB.COLOR_S_INTERVAL)
            }
        }
        ivSFineTuningRight.setOnClickListener {
            Log.d(TAG, "initSeekBarArea: color S right click")
            colorData.colorS.value?.let {
                Log.d(TAG, "initSeekBarArea: color S = $it")
                colorData.setColorS(it + HSB.COLOR_S_INTERVAL)
            }
        }
        ivBFineTuningLeft.setOnClickListener {
            Log.d(TAG, "initSeekBarArea: color B left click")
            colorData.colorB.value?.let {
                Log.d(TAG, "initSeekBarArea: color B = $it")
                colorData.setColorB(it - HSB.COLOR_B_INTERVAL)
            }
        }
        ivBFineTuningRight.setOnClickListener {
            Log.d(TAG, "initSeekBarArea: color B right click")
            colorData.colorB.value?.let {
                Log.d(TAG, "initSeekBarArea: color B = $it")
                colorData.setColorB(it + HSB.COLOR_B_INTERVAL)
            }
        }
    }

    private fun onSeekBarHDrag() {
        val h = sbH.progress
        val s = sbS.progress
        val b = sbB.progress
        val color = Color.HSVToColor(floatArrayOf(h.toFloat(), s.toFloat(), b.toFloat()))
        Log.d(TAG, "onSeekBarHDrag-resetResultColor: $h,$s,$b->${Integer.toHexString(color)}")
        colorData.setColorH(h)
        onColorPick?.invoke(HSB(h.toFloat(), s.toFloat(), b.toFloat()))
    }

    private fun onSeekBarSDrag() {
        val h = sbH.progress
        val s = sbS.progress
        val b = sbB.progress
        val color = Color.HSVToColor(floatArrayOf(h.toFloat(), s.toFloat(), b.toFloat()))
        Log.d(TAG, "onSeekBarSDrag-resetResultColor: $h,$s,$b->${Integer.toHexString(color)}")
        colorData.setColorS(s)
        onColorPick?.invoke(HSB(h.toFloat(), s.toFloat(), b.toFloat()))
    }

    private fun onSeekBarBDrag() {
        val h = sbH.progress
        val s = sbS.progress
        val b = sbB.progress
        val color = Color.HSVToColor(floatArrayOf(h.toFloat(), s.toFloat(), b.toFloat()))
        Log.d(TAG, "onSeekBarBDrag-resetResultColor: $h,$s,$b->${Integer.toHexString(color)}")
        colorData.setColorB(b)
        onColorPick?.invoke(HSB(h.toFloat(), s.toFloat(), b.toFloat()))
    }

    private fun resetSeekBarProgressBackground() {
        val colorH = colorData.colorH.value.toFloat()
        val colorS = colorData.colorS.value / 100f
        val colorB = colorData.colorB.value / 100f
        val sStartColor = floatArrayOf(colorH, 0.0f, colorB)
        val sEndColor = floatArrayOf(colorH, 1.0f, colorB)

        val bStartColor = FloatArray(3)
        val bEndColor = floatArrayOf(colorH, colorS, 1.0f)

        Color.colorToHSV(0xFF000000.toInt(), bStartColor)
        saturationGradientDrawable.colors = intArrayOf(
            Color.HSVToColor(sStartColor),
            Color.HSVToColor(sEndColor),
        )
        valueGradientDrawable.colors = intArrayOf(
            Color.HSVToColor(bStartColor),
            Color.HSVToColor(bEndColor),
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bindDataToView()
    }

    private fun bindDataToView() {
        findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            Log.d(TAG, "bindDataToView: bind to live data")
            colorData.colorH.collect(lifecycleOwner.lifecycleScope) { t: Int ->
                sbH.progress = t
                tvH.setText(String.format("%d", t))
                resetSeekBarProgressBackground()
            }
            colorData.colorS.collect(lifecycleOwner.lifecycleScope) { t: Int ->
                Log.d(TAG, "HSBColorPicker#bindDataToView- colorS: $t")
                sbS.progress = t
                tvS.setText(String.format("%d", t))
                resetSeekBarProgressBackground()
            }
            colorData.colorB.collect(lifecycleOwner.lifecycleScope) { t: Int ->
                sbB.progress = t
                tvB.setText(String.format("%d", t))
                resetSeekBarProgressBackground()
            }
        }
    }

    val progressH: Int
        get() = sbH.progress

    val progressS: Int
        get() = sbS.progress

    val progressB: Int
        get() = sbB.progress

    @JvmOverloads
    fun reset(percent: Int = 0) {
        if (percent < 0 || percent > 100) {
            return
        }
        Log.d(
            TAG,
            "HSBColorPicker#reset- to $percent%: [${HSB.COLOR_H_MAX * percent / 100}, " +
                "${HSB.COLOR_S_MAX * percent / 100}, ${HSB.COLOR_B_MAX * percent / 100}]",
        )
        colorData.setColorH(HSB.COLOR_H_MAX * percent / 100)
        colorData.setColorS(HSB.COLOR_S_MAX * percent / 100)
        colorData.setColorB(HSB.COLOR_B_MAX * percent / 100)
    }

    fun updateH(h: Int) {
        Log.d(TAG, "HSBColorPicker#setHValue- h: $h")
        colorData.setColorH(h)
    }

    fun updateS(s: Int) {
        Log.d(TAG, "HSBColorPicker#setSValue- s: $s")
        colorData.setColorS(s)
    }

    fun updateB(b: Int) {
        Log.d(TAG, "HSBColorPicker#setBValue- b: $b")
        colorData.setColorB(b)
    }

    override fun updateHSB(h: Float, s: Float, b: Float) {
        Log.d(TAG, "HSBColorPicker#updateHSB- h: $h, s: $s, b: $b")
        colorData.setColorH(h.toInt())
        colorData.setColorB(b.toInt())
        colorData.setColorS(s.toInt())
    }

    override fun setEnabled(enabled: Boolean) {
        sbH.isEnabled = enabled
        sbH.isClickable = enabled
        sbH.isFocusable = enabled
        sbS.isEnabled = enabled
        sbS.isClickable = enabled
        sbS.isFocusable = enabled
        sbB.isEnabled = enabled
        sbB.isClickable = enabled
        sbB.isFocusable = enabled
        tvH.isEnabled = enabled
        tvS.isEnabled = enabled
        tvB.isEnabled = enabled
    }

    override fun setLock(index: Int, lock: Boolean) {
        when (index) {
            0 -> {
                llHContainer1.isVisible = !lock
                llHContainer2.isVisible = !lock
            }
            1 -> {
                llSContainer1.isVisible = !lock
                llSContainer2.isVisible = !lock
            }
            2 -> {
                llBContainer1.isVisible = !lock
                llBContainer2.isVisible = !lock
            }
        }
    }

    fun lockAll() {
        setLock(0, true)
        setLock(1, true)
        setLock(2, true)
    }

    fun unLockAll() {
        setLock(0, false)
        setLock(1, false)
        setLock(2, false)
    }
}
