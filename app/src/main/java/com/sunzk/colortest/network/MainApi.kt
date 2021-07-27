package com.sunzk.colortest.network

import androidx.annotation.WorkerThread
import com.sunzk.base.network.annotations.Get
import com.sunzk.colortest.entity.ModeEntity

interface MainApi {
	
	@WorkerThread
	@Get("data/SimpleModeList.json")
	suspend fun getModeList(): List<ModeEntity>

	@Get("data/SimpleModeList.json")
	fun getModeList2(): List<ModeEntity>


	@Get("data/SimpleModeList.json")
	suspend fun getModeList3(code: String): List<ModeEntity>

	@Get("data/SimpleModeList.json")
	suspend fun getModeList4(code: String)
	@Get("data/SimpleModeList.json")
	fun getModeList5(code: String)
}