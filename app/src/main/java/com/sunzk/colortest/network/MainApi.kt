package com.sunzk.colortest.network

import androidx.annotation.WorkerThread
import com.sunzk.base.network.annotations.Get
import com.sunzk.colortest.entity.ModeEntity

interface MainApi {

	@Deprecated("此API已过时，数据结构不再兼容")
	@WorkerThread
	@Get("data/SimpleModeList.json")
	suspend fun getModeList(): List<ModeEntity>

	@WorkerThread
	@Get("data/SimpleModeList2.json")
	suspend fun getModeList2(): List<ModeEntity>

}