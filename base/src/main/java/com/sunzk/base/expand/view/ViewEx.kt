package com.sunzk.base.expand.view

import android.app.Activity
import android.view.View
import com.sunzk.base.utils.AppUtils

object ViewEx {

    fun View.getActivity(): Activity? {
        return AppUtils.getActivity(this.context)
    }

}