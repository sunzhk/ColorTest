package com.sunzk.colortest.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.sunzk.base.utils.AppUtils
import com.sunzk.base.utils.ColorUtils
import com.sunzk.base.utils.DisplayUtil
import com.sunzk.base.utils.NumberUtils
import com.sunzk.colortest.R
import com.sunzk.colortest.databinding.MergeHsbColorSelectorBinding
import com.sunzk.colortest.entity.HSB
import java.util.*

class HSBColorSelector : LinearLayout {

    private var hueGradientDrawable: GradientDrawable? = null
    private var saturationGradientDrawable: GradientDrawable? = null
    private var valueGradientDrawable: GradientDrawable? = null

    private var colorData: HBSColorSelectorData = HBSColorSelectorData()
    private lateinit var viewBinding: MergeHsbColorSelectorBinding
    private var onColorSelectedListener: ((Float, Float, Float) -> Unit)? = null

    /* ---------------- 构造方法开始 ---------------- */
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

    /* ---------------- 构造方法结束 ---------------- */
    private fun init() {
        this.orientation = VERTICAL
        viewBinding = MergeHsbColorSelectorBinding.inflate(
            LayoutInflater.from(context), this
        )
        initSeekBarArea()
    }

    private fun initSeekBarArea() {
        val strokeWidth = DisplayUtil.dip2px(context, 1f)
        hueGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            ColorUtils.HUE_COLOR_LIST
        )
        val dimension =
            resources.getDimension(R.dimen.view_hsb_color_selector_seek_bar_height)
        hueGradientDrawable!!.cornerRadius = dimension / 2
        hueGradientDrawable!!.setStroke(strokeWidth, Color.BLACK)
        viewBinding.sbH.progressDrawable = hueGradientDrawable
        saturationGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(-0x1000000, -0x1000000)
        )
        saturationGradientDrawable!!.cornerRadius = dimension / 2
        saturationGradientDrawable!!.setStroke(strokeWidth, Color.BLACK)
        viewBinding.sbS.progressDrawable = saturationGradientDrawable
        valueGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(-0x1000000, -0x1000000)
        )
        valueGradientDrawable!!.cornerRadius = dimension / 2
        valueGradientDrawable!!.setStroke(strokeWidth, Color.BLACK)
        viewBinding.sbB.progressDrawable = valueGradientDrawable

        viewBinding.tvH.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            var value = NumberUtils.parse(v.text.toString(), -1)
            colorData.setColorH(value)
            false
        }
        viewBinding.tvS.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            var value = NumberUtils.parse(v.text.toString(), -1f)
            colorData.setColorS(value / 100)
            false
        }
        viewBinding.tvB.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            var value = NumberUtils.parse(v.text.toString(), -1f)
            colorData.setColorB(value / 100)
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
                colorData.setColorH(it - HBSColorSelectorData.COLOR_H_INTERVAL)
            }
        }
        viewBinding.ivHFineTuningRight.setOnClickListener { v: View? ->
            Log.d(TAG, "initSeekBarArea: color H right click")
            colorData.colorH.value?.let {
                Log.d(TAG, "initSeekBarArea: color H = $it")
                colorData.setColorH(it + HBSColorSelectorData.COLOR_H_INTERVAL)
            }
        }
        viewBinding.ivSFineTuningLeft.setOnClickListener { v: View? ->
            Log.d(TAG, "initSeekBarArea: color S left click")
            colorData.colorS.value?.let {
                Log.d(TAG, "initSeekBarArea: color S = $it")
                colorData.setColorS(it - HBSColorSelectorData.COLOR_S_INTERVAL)
            }
        }
        viewBinding.ivSFineTuningRight.setOnClickListener { v: View? ->
            Log.d(TAG, "initSeekBarArea: color S right click")
            colorData.colorS.value?.let {
                Log.d(TAG, "initSeekBarArea: color S = $it")
                colorData.setColorS(it + HBSColorSelectorData.COLOR_S_INTERVAL)
            }
        }
        viewBinding.ivBFineTuningLeft.setOnClickListener { v: View? ->
            Log.d(TAG, "initSeekBarArea: color B left click")
            colorData.colorB.value?.let {
                Log.d(TAG, "initSeekBarArea: color B = $it")
                colorData.setColorB(it - HBSColorSelectorData.COLOR_B_INTERVAL)
            }
        }
        viewBinding.ivBFineTuningRight.setOnClickListener { v: View? ->
            Log.d(TAG, "initSeekBarArea: color B right click")
            colorData.colorB.value?.let {
                Log.d(TAG, "initSeekBarArea: color B = $it")
                colorData.setColorB(it + HBSColorSelectorData.COLOR_B_INTERVAL)
            }
        }
    }
    
    private fun onSeekBarHDrag() {
        val h = viewBinding.sbH.progress * 1.0f
        val s = viewBinding.sbS.progress * 1.0f / 100
        val b = viewBinding.sbB.progress * 1.0f / 100
        val color = Color.HSVToColor(floatArrayOf(h, s, b))
        Log.d(TAG, "resetResultColor: $h,$s,$b->${Integer.toHexString(color)}")

        colorData.setColorH(h.toInt())
        onColorSelectedListener?.invoke(h, s, b)
    }
    private fun onSeekBarSDrag() {
        val h = viewBinding.sbH.progress * 1.0f
        val s = viewBinding.sbS.progress * 1.0f / 100
        val b = viewBinding.sbB.progress * 1.0f / 100
        val color = Color.HSVToColor(floatArrayOf(h, s, b))
        Log.d(TAG, "resetResultColor: $h,$s,$b->${Integer.toHexString(color)}")

        colorData.setColorS(s)

        onColorSelectedListener?.invoke(h, s, b)
    }
    private fun onSeekBarBDrag() {
        val h = viewBinding.sbH.progress * 1.0f
        val s = viewBinding.sbS.progress * 1.0f / 100
        val b = viewBinding.sbB.progress * 1.0f / 100
        val color = Color.HSVToColor(floatArrayOf(h, s, b))
        Log.d(TAG, "resetResultColor: $h,$s,$b->${Integer.toHexString(color)}")

        colorData.setColorB(b)

        onColorSelectedListener?.invoke(h, s, b)
    }

    private fun resetSeekBarProgressBackground() {
        val sStartColor = floatArrayOf(colorH, 0.0f, colorB)
        val sEndColor = floatArrayOf(colorH, 1.0f, colorB)

        val bStartColor = FloatArray(3)
        val bEndColor = floatArrayOf(colorH, colorS, 1.0f)
        
        Color.colorToHSV(0xFF000000.toInt(), bStartColor)
        saturationGradientDrawable?.colors = intArrayOf(Color.HSVToColor(sStartColor), Color.HSVToColor(sEndColor))
        valueGradientDrawable?.colors = intArrayOf(Color.HSVToColor(bStartColor), Color.HSVToColor(bEndColor))
    }

    val colorRGB: Int
        get() = Color.HSVToColor(
            floatArrayOf(
                colorH,
                colorS,
                colorB
            )
        )

    fun setColor(color: HSB) {
        setColor(color.h, color.s, color.b)
    }

    fun setColor(h: Float, s: Float, b: Float) {
        colorData.setColorH(h.toInt())
        colorData.setColorS(s)
        colorData.setColorB(b)
        onColorSelectedListener?.invoke(h, s, b)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bindDataToView()
    }

    private fun bindDataToView() {
        findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            Log.d(TAG, "bindDataToView: bind to live data")
            colorData.colorH.observe(lifecycleOwner, androidx.lifecycle.Observer { t: Int ->
                viewBinding.sbH.progress = t
                viewBinding.tvH.setText(String.format("%d", t))
                resetSeekBarProgressBackground()
            })
            colorData.colorS.observe(lifecycleOwner, androidx.lifecycle.Observer { t: Float ->
                (t * 100).toInt().let { colorS ->
                    viewBinding.sbS.progress = colorS
                    viewBinding.tvS.setText(String.format("%d", colorS))
                }
                resetSeekBarProgressBackground()
            })
            colorData.colorB.observe(lifecycleOwner, androidx.lifecycle.Observer { t: Float ->
                (t * 100).toInt().let { colorB ->
                    viewBinding.sbB.progress = colorB
                    viewBinding.tvB.setText(String.format("%d", colorB))
                }
                resetSeekBarProgressBackground()
            })
        }
    }

    val colorH: Float
        get() = colorData.colorH.value!!.toFloat()

    val colorS: Float
        get() = colorData.colorS.value!!

    val colorB: Float
        get() = colorData.colorB.value!!

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
        Log.d(TAG, "reset to $percent%")
        colorData.setColorH(HBSColorSelectorData.COLOR_H_MAX * percent / 100)
        colorData.setColorS(HBSColorSelectorData.COLOR_S_MAX * percent / 100)
        colorData.setColorB(HBSColorSelectorData.COLOR_B_MAX * percent / 100)
    }

    fun setOnColorSelectedListener(onColorSelectedListener: (Float, Float, Float) -> Unit) {
        this.onColorSelectedListener = onColorSelectedListener
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

    companion object {
        private const val TAG = "HSBColorSelector"
    }
}