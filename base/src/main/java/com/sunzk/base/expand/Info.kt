package com.sunzk.base.expand

fun threadInfo(): String {
	return "thread=${Thread.currentThread().id}-${Thread.currentThread().name}"
}