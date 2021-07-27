package com.sunzk.base.network

data class ServerSetting(val callTimeout: Long = 0,
                         val connectTimeout: Long = 0,
                         val readTimeout: Long = 0,
                         val writeTimeout: Long = 0)
