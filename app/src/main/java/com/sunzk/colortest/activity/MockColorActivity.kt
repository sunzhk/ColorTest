package com.sunzk.colortest.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.sunzk.base.utils.AppUtils
import com.sunzk.base.utils.ColorUtils
import com.sunzk.base.utils.DisplayUtil
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.RouteInfo
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.databinding.ActivityMockColorBinding
import com.sunzk.colortest.db.TestResultDataBase
import com.sunzk.colortest.entity.TestResult
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

@Route(path = RouteInfo.PATH_ACTIVITY_MOCK_COLOR, group = RouteInfo.GROUP_GAME)
class MockColorActivity : BaseActivity() {
    private lateinit var viewBinding: ActivityMockColorBinding
    private lateinit var currentHSB: FloatArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        viewBinding = ActivityMockColorBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        Observable.create(
            ObservableOnSubscribe { emitter: ObservableEmitter<List<TestResult>?> ->
                try {
                    val testResultList: List<TestResult> =
                        TestResultDataBase.getInstance(this@MockColorActivity).listScore()
                    emitter.onNext(testResultList)
                    emitter.onComplete()
                } catch (throwable: Throwable) {
                    emitter.onError(throwable)
                }
            }
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :
                Observer<List<TestResult?>?> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(testResults: List<TestResult?>) {
                    Log.d(
                        TAG,
                        "onNext: " + testResults.size
                    )
                    Runtime.testResultNumber = testResults.size
                    showScore(Runtime.testResultNumber)
                    for (testResult in testResults) {
//							Log.d(TAG, "onNext: line: " + testResult);
                    }
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
        viewBinding!!.btNext.setOnClickListener { v: View? -> nextProblem() }
        viewBinding!!.btAnswer.setOnClickListener { v: View? ->
            showAnswer(
                v
            )
        }
        viewBinding!!.hsbColorSelector.setOnColorSelectedListener { h: Float, s: Float, b: Float ->
            val color = Color.HSVToColor(floatArrayOf(h, s, b))
            viewBinding!!.viewResult.setBackgroundColor(color)
        }
        initColorArea()
        nextProblem()
    }

    override fun needBGM(): Boolean {
        return true
    }

    /**
     * 对比答案
     */
    private fun checkAnswer() {
        val h = currentHSB[0]
        val s = currentHSB[1]
        val b = currentHSB[2]
        val difH = Math.abs(h - viewBinding!!.hsbColorSelector.colorH)
        val difS =
            Math.abs(s * 100 - viewBinding!!.hsbColorSelector.colorS * 100)
        val difB =
            Math.abs(b * 100 - viewBinding!!.hsbColorSelector.colorB * 100)
        if (difH < ANSWER_STANDARD && difS < ANSWER_STANDARD && difB < ANSWER_STANDARD) {
            // TODO: 2020/7/11 显示奖励效果，这里先显示一个Toast顶替一下
            Toast.makeText(
                this,
                String.format(
                    Locale.getDefault(),
                    "各项值的误差不超过%d，真棒",
                    ANSWER_STANDARD
                ),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * 初始化颜色区域
     */
    private fun initColorArea() {
        val screenSize = AppUtils.getScreenWidth(applicationContext)
        val spacing = DisplayUtil.dip2px(this, 20f)
        val cardSideLength = (screenSize - spacing * 4) / 2
        val radius = cardSideLength / 2
        viewBinding!!.cvDemo.radius = radius.toFloat()
        viewBinding!!.cvDemo.layoutParams.width = cardSideLength
        viewBinding!!.cvDemo.layoutParams.height = cardSideLength
        val cdDemoLayoutParams = viewBinding!!.cvDemo.layoutParams
        if (cdDemoLayoutParams is FrameLayout.LayoutParams) {
            cdDemoLayoutParams.leftMargin = spacing
            cdDemoLayoutParams.rightMargin = spacing
        }
        viewBinding!!.cvResult.radius = radius.toFloat()
        viewBinding!!.cvResult.layoutParams.width = cardSideLength
        viewBinding!!.cvResult.layoutParams.height = cardSideLength
        val cdResultLayoutParams = viewBinding!!.cvResult.layoutParams
        if (cdResultLayoutParams is FrameLayout.LayoutParams) {
            cdResultLayoutParams.leftMargin = spacing
            cdResultLayoutParams.rightMargin = spacing
        }
    }

    fun showAnswer(v: View?) {
        val showH = currentHSB[0].toInt()
        val showS = (currentHSB[1] * 100).toInt()
        val showB = (currentHSB[2] * 100).toInt()
        viewBinding!!.hsbColorSelector.isEnabled = false
        viewBinding!!.tvAnswer.text = String.format(
            Locale.getDefault(),
            "H: %d度  S: %d%%  B: %d%%",
            showH,
            showS,
            showB
        )
        TestResultDataBase.Companion.getInstance(this)!!.recordScore(
            showH.toFloat(),
            showS.toFloat(),
            showB.toFloat(),
            viewBinding!!.hsbColorSelector.progressH.toFloat(),
            viewBinding!!.hsbColorSelector.progressS.toFloat(),
            viewBinding!!.hsbColorSelector.progressB.toFloat()
        )
        showScore(Runtime.testResultNumber)
        checkAnswer()
    }

    private fun nextProblem() {
        currentHSB = ColorUtils.randomHSBColor()
        val color = Color.HSVToColor(currentHSB)
        Log.d(
            TAG,
            "nextProblem: " + currentHSB[0] + "," + currentHSB[1] + "," + currentHSB[2] + "->" + color
        )
        viewBinding!!.viewDemo.setBackgroundColor(color)
        viewBinding!!.tvAnswer.text = null
        viewBinding!!.hsbColorSelector.isEnabled = true
        viewBinding!!.hsbColorSelector.reset(50)
    }

    private fun showScore(number: Int) {
        viewBinding!!.tvScore.text = String.format(
            Locale.getDefault(),
            "总计完成测试: %d次",
            number
        )
    }

    companion object {
        private const val TAG = "MockColorActivity"
        private const val ANSWER_STANDARD = 5
    }
}