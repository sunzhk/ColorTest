package com.sunzk.colortest.tools

import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.updateLayoutParams
import com.blankj.utilcode.util.ScreenUtils
import com.sunzk.base.expand.takeIfIs
import com.sunzk.colortest.compose.ui.FloatingSettingScreen
import com.sunzk.colortest.tools.ext.dp2px
import java.lang.ref.SoftReference

/**
 * 悬浮设置窗管理器
 */
object FloatingSettingWindowManager {

    private const val TAG: String = "FloatingSettingWindowManager"

    private val X_START = 10f.dp2px
    private val Y_START = 10f.dp2px
    private val X_END = ScreenUtils.getAppScreenWidth() - X_START
    private val Y_END = ScreenUtils.getAppScreenHeight() - Y_START
    private var floatingView: SoftReference<ComposeView>? = null

    private val isFoldState = mutableStateOf(true)

    /**
     * 记录一下定位，在重建的时候还原现场
     */
    private val location = Point(X_START, Y_START + 50.dp2px)

    /**
     * 记录一下展开状态，在重建的时候还原现场
     */
    private var isFold = true

    @SuppressLint("ClickableViewAccessibility")
    private val onTouch = View.OnTouchListener { _, event ->
        floatingView?.get()?.let { view ->
            val rect = Rect()
            view.getGlobalVisibleRect(rect)
            if (!rect.contains(event.x.toInt(), event.y.toInt())) {
                fold()
            }
        }
        false
    }

    /**
     * 把组件挂载到Activity上
     */
    fun attach(activity: ComponentActivity) {
        Log.d(TAG, "FloatingSettingWindowManager#attach")
        val view = floatingView?.get()?.takeIf { it.tag == activity.hashCode() } ?: createNewView(activity)
        if (view.parent != null) {
            return
        }
        Log.d(TAG, "FloatingSettingWindowManager#attach- real attach")
        val layoutParams = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.marginStart = location.x
        layoutParams.topMargin = location.y
        activity.window.decorView.findViewById<FrameLayout>(android.R.id.content)?.addView(view, layoutParams)
        activity.window.decorView.setOnTouchListener(onTouch)
    }

    /**
     * 把组件从Activity上卸载
     */
    fun detach(activity: ComponentActivity) {
        Log.d(TAG, "FloatingSettingWindowManager#detach")
        floatingView?.get()
            ?.takeIf { it.tag == activity.hashCode() }
            ?.let { composeView ->
                activity.window.decorView.setOnTouchListener(null)
                composeView.parent.takeIfIs<ViewGroup>()?.removeView(composeView)
            }
    }

    private fun createNewView(activity: ComponentActivity): ComposeView {
        Log.d(TAG, "FloatingSettingWindowManager#createNewView")
        val composeView = ComposeView(activity)
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        composeView.setContent {
            val folded by isFoldState
            FloatingSettingScreen(
                isFold = folded,
                onFold = { fold() },
                onUnfold = { unfold() },
            )
        }
        floatingView = SoftReference(composeView)
        composeView.tag = activity.hashCode()
        val listener = ItemViewTouchListener(composeView)
        composeView.setOnTouchListener(listener)
        if (isFold) {
            fold()
        } else {
            unfold()
        }
        return composeView
    }

    private fun fold() {
        isFold = true
        isFoldState.value = true
    }

    private fun unfold() {
        isFold = false
        isFoldState.value = false
        floatingView?.get()?.post {
            val root = floatingView?.get() ?: return@post
            root.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            )
            Log.d(
                TAG,
                "FloatingSettingWindowManager#unfold- measure size=[${root.measuredWidth}, ${root.measuredHeight}]",
            )
            if (location.x + root.measuredWidth > X_END) {
                root.updateLayoutParams<MarginLayoutParams> {
                    marginStart = X_END - root.measuredWidth
                    location.x = marginStart
                }
            }
            if (location.y + root.measuredHeight > Y_END) {
                root.updateLayoutParams<MarginLayoutParams> {
                    topMargin = Y_END - root.measuredHeight
                    location.y = topMargin
                }
            }
        }
    }

    class ItemViewTouchListener(private val floatView: View) : View.OnTouchListener {
        private val tagLocal: String = "ItemViewTouchListener"
        private var x = 0
        private var y = 0
        private var movingFlag = false

        private var startX = 0
        private var startY = 0

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            Log.d(tagLocal, "ItemViewTouchListener#onTouch- action=${motionEvent.action}")
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = motionEvent.rawX.toInt()
                    y = motionEvent.rawY.toInt()
                    startX = x
                    startY = y
                    movingFlag = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val nowX = motionEvent.rawX.toInt()
                    val nowY = motionEvent.rawY.toInt()
                    val movedX = nowX - x
                    val movedY = nowY - y
                    x = nowX
                    y = nowY
                    floatView.updateLayoutParams<MarginLayoutParams> {
                        marginStart += movedX
                        topMargin += movedY
                        marginStart = marginStart.coerceIn(X_START, X_END - floatView.width)
                        topMargin = topMargin.coerceIn(Y_START, Y_END - floatView.height)
                        location.x = marginStart
                        location.y = topMargin
                        Log.d(
                            tagLocal,
                            "ItemViewTouchListener#onTouch- marginStart=$marginStart, topMargin=$topMargin",
                        )
                    }
                    movingFlag = true
                }
                MotionEvent.ACTION_UP -> {
                    val nowX = motionEvent.rawX.toInt()
                    val nowY = motionEvent.rawY.toInt()
                    if ((nowX - startX) < 5 && (nowY - startY) < 5) {
                        movingFlag = false
                        return false
                    }
                    if (movingFlag) {
                        movingFlag = false
                        return true
                    }
                }
                else -> {
                }
            }
            return false
        }
    }
}
