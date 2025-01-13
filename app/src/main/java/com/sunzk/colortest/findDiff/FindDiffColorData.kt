package com.sunzk.colortest.findDiff

data class FindDiffColorData(
	val level: Int,
	val countPerSide: Int,
	val baseColor: FloatArray,
	val diffColor: FloatArray,
	val diffIndex: Int
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as FindDiffColorData

		if (level != other.level) return false
		if (countPerSide != other.countPerSide) return false
		if (!baseColor.contentEquals(other.baseColor)) return false
		if (!diffColor.contentEquals(other.diffColor)) return false
		if (diffIndex != other.diffIndex) return false

		return true
	}

	override fun hashCode(): Int {
		var result = level
		result = 31 * result + countPerSide
		result = 31 * result + baseColor.contentHashCode()
		result = 31 * result + diffColor.contentHashCode()
		result = 31 * result + diffIndex
		return result
	}
}
