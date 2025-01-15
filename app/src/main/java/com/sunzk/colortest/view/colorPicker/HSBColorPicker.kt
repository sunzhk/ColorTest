package com.sunzk.colortest.view.colorPicker

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.sunzk.base.expand.bindView
import com.sunzk.base.expand.collect
import com.sunzk.base.utils.ColorUtils
import com.sunzk.base.utils.DisplayUtil
import com.sunzk.base.utils.NumberUtils
import com.sunzk.colortest.R
import com.sunzk.colortest.databinding.MergeHsbColorPickerBinding
import com.sunzk.colortest.entity.HSB

class HSBColorPicker : LinearLayout, IColorPicker {

    companion object {
        private const val TAG = "HSBColorPicker"
    }

    /**
     * 色相进度条的背景色，固定的
     */
    private val hueGradientDrawable: GradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        ColorUtils.HUE_COLOR_LIST
    )

    /**
     * 饱和度进度条的背景色，根据色相变化
     */
    private val saturationGradientDrawable: GradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(-0x1000000, -0x1000000)
    )

    /**
     * 明度进度条的颜色，根据色相和饱和度变化
     */
    private val valueGradientDrawable: GradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(-0x1000000, -0x1000000)
    )

    private var colorData: HBSColorPickerData = HBSColorPickerData()
    private val viewBinding by bindView<MergeHsbColorPickerBinding>()

    override val pickerView: View
        get() = this
    
    override val hsb: HSB
        get() = HSB(viewBinding.sbH.progress.toFloat(),
            viewBinding.sbS.progress.toFloat(),
            viewBinding.sbB.progress.toFloat())
    override var onColorPick: ((HSB) -> Unit)? = null

    // <editor-fold desc="构造方法">
    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }
    // </editor-fold>

    private fun init() {
        this.orientation = VERTICAL
        initSeekBarArea()
    }

    private fun initSeekBarArea() {
        val strokeWidth = DisplayUtil.dip2px(context, 1f)
        val dimension = resources.getDimension(R.dimen.view_hsb_color_selector_seek_bar_height)
        hueGradientDrawable.cornerRadius = dimension / 2
        hueGradientDrawable.setStroke(strokeWidth, Color.BLACK)
        viewBinding.sbH.progressDrawable = hueGradientDrawable
        saturationGradientDrawable.cornerRadius = dimension / 2
        saturationGradientDrawable.setStroke(strokeWidth, Color.BLACK)
        viewBinding.sbS.progressDrawable = saturationGradientDrawable
        valueGradientDrawable.cornerRadius = dimension / 2
        valueGradientDrawable.setStroke(strokeWidth, Color.BLACK)
        viewBinding.sbB.progressDrawable = valueGradientDrawable

        viewBinding.tvH.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            colorData.setColorH(NumberUtils.parse(v.text.toString(), -1))
            false
        }
        viewBinding.tvS.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            colorData.setColorS(NumberUtils.parse(v.text.toString(), -1))
            false
        }
        viewBinding.tvB.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            colorData.setColorB(NumberUtils.parse(v.text.toString(), -1))
            false
        }
        viewBinding.sbH.max = 360
        viewBinding.sbS.max = 100
        viewBinding.sbB.max = 100
        val onSeekBarChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                i: Int,
                b: Boolean
            ) {
                Log.d(TAG, "HSBColorPicker#onProgressChanged- ${resources.getResourceName(seekBar.id)} - $i - $b")
                when (seekBar) {
                    viewBinding.sbH -> {
                        onSeekBarHDrag()
                    }
                    viewBinding.sbS -> {
                        onSeekBarSDrag()
                    }
                    viewBinding.sbB -> {
                        onSeekBarBDrag()
                    }
                    else -> {
                    }
                }
                
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
        viewBinding.sbH.setOnSeekBarChangeListener(onSeekBarChangeListener)
        viewBinding.sbS.setOnSeekBarChangeListener(onSeekBarChangeListener)
        viewBinding.sbB.setOnSeekBarChangeListener(onSeekBarChangeListener)
        viewBinding.ivHFineTuningLeft.setOnClickListener { v: View? ->
            Log.d(TAG, "initSeekBarArea: color H left click")
            colorData.colorH.value?.let {
                Log.d(TAG, "initSeekBarArea: color H = $it")
                colorData.setColorH(it - HSB.COLOR_H_INTERVAL)
            }
        }
        viewBinding.ivHFineTuningRight.setOnClickListener { v: View? ->
            Log.d(TAG, "initSeekBarArea: color H right click")
            colorData.colorH.value?.let {
                Log.d(TAG, "initSeekBarArea: color H = $it")
                colorData.setColorH(it + HSB.COLOR_H_INTERVAL)
            }
        }
        viewBinding.ivSFineTuningLeft.setOnClickListener { v: View? ->
            Log.d(TAG, "initSeekBarArea: color S left click")
            colorData.colorS.value?.let {
                Log.d(TAG, "initSeekBarArea: color S = $it")
                colorData.setColorS(it - HSB.COLOR_S_INTERVAL)
            }
        }
        viewBinding.ivSFineTuningRight.setOnClickListener { v: View? ->
            Log.d(TAG, "initSeekBarArea: color S right click")
            colorData.colorS.value?.let {
                Log.d(TAG, "initSeekBarArea: color S = $it")
                colorData.setColorS(it + HSB.COLOR_S_INTERVAL)
            }
        }
        viewBinding.ivBFineTuningLeft.setOnClickListener { v: View? ->
            Log.d(TAG, "initSeekBarArea: color B left click")
            colorData.colorB.value?.let {
                Log.d(TAG, "initSeekBarArea: color B = $it")
                colorData.setColorB(it - HSB.COLOR_B_INTERVAL)
            }
        }
        viewBinding.ivBFineTuningRight.setOnClickListener { v: View? ->
            Log.d(TAG, "initSeekBarArea: color B right click")
            colorData.colorB.value?.let {
                Log.d(TAG, "initSeekBarArea: color B = $it")
                colorData.setColorB(it + HSB.COLOR_B_INTERVAL)
            }
        }
    }
    
    private fun onSeekBarHDrag() {
        val h = viewBinding.sbH.progress
        val s = viewBinding.sbS.progress
        val b = viewBinding.sbB.progress
        val color = Color.HSVToColor(floatArrayOf(h.toFloat(), s.toFloat(), b.toFloat()))
        Log.d(TAG, "onSeekBarHDrag-resetResultColor: $h,$s,$b->${Integer.toHexString(color)}")
        colorData.setColorH(h)
        onColorPick?.invoke(HSB(h.toFloat(), s.toFloat(), b.toFloat()))
    }
    private fun onSeekBarSDrag() {
        val h = viewBinding.sbH.progress
        val s = viewBinding.sbS.progress
        val b = viewBinding.sbB.progress
        val color = Color.HSVToColor(floatArrayOf(h.toFloat(), s.toFloat(), b.toFloat()))
        Log.d(TAG, "onSeekBarSDrag-resetResultColor: $h,$s,$b->${Integer.toHexString(color)}")
        colorData.setColorS(s)
        onColorPick?.invoke(HSB(h.toFloat(), s.toFloat(), b.toFloat()))
    }
    private fun onSeekBarBDrag() {
        val h = viewBinding.sbH.progress
        val s = viewBinding.sbS.progress
        val b = viewBinding.sbB.progress
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
        saturationGradientDrawable.colors = intArrayOf(Color.HSVToColor(sStartColor), Color.HSVToColor(sEndColor))
        valueGradientDrawable.colors = intArrayOf(Color.HSVToColor(bStartColor), Color.HSVToColor(bEndColor))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bindDataToView()
    }

    private fun bindDataToView() {
        findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            Log.d(TAG, "bindDataToView: bind to live data")
            colorData.colorH.collect(lifecycleOwner.lifecycleScope) { t: Int ->
                viewBinding.sbH.progress = t
                viewBinding.tvH.setText(String.format("%d", t))
                resetSeekBarProgressBackground()
            }
            colorData.colorS.collect(lifecycleOwner.lifecycleScope) { t: Int ->
                Log.d(TAG, "HSBColorPicker#bindDataToView- colorS: $t")
                viewBinding.sbS.progress = t
                viewBinding.tvS.setText(String.format("%d", t))
                resetSeekBarProgressBackground()
            }
            colorData.colorB.collect(lifecycleOwner.lifecycleScope) { t: Int ->
                viewBinding.sbB.progress = t
                viewBinding.tvB.setText(String.format("%d", t))
                resetSeekBarProgressBackground()
            }
        }
    }

    val progressH: Int
        get() = viewBinding.sbH.progress

    val progressS: Int
        get() = viewBinding.sbS.progress

    val progressB: Int
        get() = viewBinding.sbB.progress

    @JvmOverloads
    fun reset(percent: Int = 0) {
        if (percent < 0 || percent > 100) {
            return
        }
        Log.d(TAG, "HSBColorPicker#reset- to $percent%: [${HSB.COLOR_H_MAX * percent / 100}, ${HSB.COLOR_S_MAX * percent / 100}, ${HSB.COLOR_B_MAX * percent / 100}]")
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
        viewBinding.sbH.isEnabled = enabled
        viewBinding.sbH.isClickable = enabled
        viewBinding.sbH.isFocusable = enabled
        viewBinding.sbS.isEnabled = enabled
        viewBinding.sbS.isClickable = enabled
        viewBinding.sbS.isFocusable = enabled
        viewBinding.sbB.isEnabled = enabled
        viewBinding.sbB.isClickable = enabled
        viewBinding.sbB.isFocusable = enabled
        viewBinding.tvH.isEnabled = enabled
        viewBinding.tvS.isEnabled = enabled
        viewBinding.tvB.isEnabled = enabled
    }

    override fun setLock(index: Int, lock: Boolean) {
        when (index) {
            0 -> {
                viewBinding.llHContainer1.isVisible = !lock
                viewBinding.llHContainer2.isVisible = !lock
            }
            1 -> {
                viewBinding.llSContainer1.isVisible = !lock
                viewBinding.llSContainer2.isVisible = !lock
            }
            2 -> {
                viewBinding.llBContainer1.isVisible = !lock
                viewBinding.llBContainer2.isVisible = !lock
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