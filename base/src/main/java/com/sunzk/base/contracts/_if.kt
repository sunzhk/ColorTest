package com.sunzk.base.contracts

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <T> T.runIf(condition: Boolean, action: () -> Unit): T {
	if (condition) {
		action()
	}
	return this
}


inline fun <T> T.runIf(condition: () -> Boolean, action: () -> Unit): T {
	if (condition()) {
		action()
	}
	return this
}

inline fun <T> T.applyIf(condition: Boolean, action: T.() -> T): T {
	if (condition) {
		action(this)
	}
	return this
}

inline fun <T> T.applyIf(condition: () -> Boolean, action: T.() -> T): T {
	if (condition()) {
		action(this)
	}
	return this
}

@OptIn(ExperimentalContracts::class)
inline fun <reified T> T.buildIf(condition: Boolean, action: (T) -> T): T {
	contract {
		callsInPlace(action, InvocationKind.AT_MOST_ONCE)
	}
	return if (condition) {
		action(this)
	} else {
		this
	}
}

@OptIn(ExperimentalContracts::class)
inline fun <T> T.buildIf(condition: () -> Boolean, action: (T) -> T): T {
	contract {
		callsInPlace(action, InvocationKind.AT_MOST_ONCE)
	}
	return if (condition()) {
		action(this)
	} else {
		this
	}
}