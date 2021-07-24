package com.sunzk.colortest.entity

import com.sunzk.colortest.Constant

data class ModeEntity(val title: String, val className: String, val bundle: Map<String, String>? = null, val action: String = Constant.ModeAction.activity.name) 