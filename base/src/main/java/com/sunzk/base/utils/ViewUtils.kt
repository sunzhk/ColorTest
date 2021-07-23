package com.sunzk.base.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.sunzk.base.utils.AppUtils.getActivity

/**
 * 控件工具类
 * Created by sunzhk on 2018/6/7.
 */
object ViewUtils {
    /**
     * 判断child控件是否是parent控件的子控件(直接、间接)
     *
     * @param child  子控件
     * @param parent 父控件
     * @return true 如果child是parent的子控件(直接、间接)
     */
    fun isChildOf(child: View, parent: View): Boolean {
        val activity = getActivity(parent.context)
        return isChildOf(activity!!, child, parent)
    }

    /**
     * 判断child控件是否是parent控件的子控件(直接、间接)
     *
     * @param activity 所属Activity
     * @param child    子控件
     * @param parent   父控件
     * @return true 如果child是parent的子控件(直接、间接)
     */
    fun isChildOf(activity: Activity, child: View, parent: View): Boolean {
        if (child === parent || parent !is ViewGroup) {
            return false
        }
        val rootView = activity.window.decorView
        var viewParent = child.parent
        while (rootView !== viewParent) {
            if (viewParent === parent) {
                return true
            }
            viewParent = viewParent.parent
        }
        return false
    }
}