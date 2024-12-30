package com.sunzk.demo.tools.ext

fun ByteArray.startWith(array: ByteArray): Boolean {
	if (array.size > this.size) {
		return false
	}
	array.forEachIndexed { index, item ->
		if (this[index] != item) {
			return false
		}
	}
	return true
}

fun CharArray.startWith(array: CharArray): Boolean {
	if (array.size > this.size) {
		return false
	}
	array.forEachIndexed { index, item ->
		if (this[index] != item) {
			return false
		}
	}
	return true
}

fun ShortArray.startWith(array: ShortArray): Boolean {
	if (array.size > this.size) {
		return false
	}
	array.forEachIndexed { index, item ->
		if (this[index] != item) {
			return false
		}
	}
	return true
}

fun IntArray.startWith(array: IntArray): Boolean {
	if (array.size > this.size) {
		return false
	}
	array.forEachIndexed { index, item ->
		if (this[index] != item) {
			return false
		}
	}
	return true
}

fun LongArray.startWith(array: LongArray): Boolean {
	if (array.size > this.size) {
		return false
	}
	array.forEachIndexed { index, item ->
		if (this[index] != item) {
			return false
		}
	}
	return true
}

fun FloatArray.startWith(array: FloatArray): Boolean {
	if (array.size > this.size) {
		return false
	}
	array.forEachIndexed { index, item ->
		if (this[index] != item) {
			return false
		}
	}
	return true
}

fun DoubleArray.startWith(array: DoubleArray): Boolean {
	if (array.size > this.size) {
		return false
	}
	array.forEachIndexed { index, item ->
		if (this[index] != item) {
			return false
		}
	}
	return true
}

fun BooleanArray.startWith(array: BooleanArray): Boolean {
	if (array.size > this.size) {
		return false
	}
	array.forEachIndexed { index, item ->
		if (this[index] != item) {
			return false
		}
	}
	return true
}

fun <T> Array<T>.startWith(array: Array<T>): Boolean {
	if (array.size > this.size) {
		return false
	}
	array.forEachIndexed { index, item ->
		if (this[index] != item) {
			return false
		}
	}
	return true
}