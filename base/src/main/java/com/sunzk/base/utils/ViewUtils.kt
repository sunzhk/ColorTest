package com.sunzk.base.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;

/**
 * 控件工具类
 * Created by sunzhk on 2018/6/7.
 */
public class ViewUtils {

	/**
	 * 判断child控件是否是parent控件的子控件(直接、间接)
	 *
	 * @param child  子控件
	 * @param parent 父控件
	 * @return true 如果child是parent的子控件(直接、间接)
	 */
	public static boolean isChildOf(@NonNull View child, @NonNull View parent) {
		Activity activity = AppUtils.getActivity(parent.getContext());
		return isChildOf(activity, child, parent);
	}

	/**
	 * 判断child控件是否是parent控件的子控件(直接、间接)
	 *
	 * @param activity 所属Activity
	 * @param child    子控件
	 * @param parent   父控件
	 * @return true 如果child是parent的子控件(直接、间接)
	 */
	public static boolean isChildOf(@NonNull Activity activity, @NonNull View child, @NonNull View parent) {
		if (child == parent || !(parent instanceof ViewGroup)) {
			return false;
		}
		View rootView = activity.getWindow().getDecorView();
		ViewParent viewParent = child.getParent();
		while (rootView != viewParent) {
			if (viewParent == parent) {
				return true;
			}
			viewParent = viewParent.getParent();
		}
		return false;
	}

}
