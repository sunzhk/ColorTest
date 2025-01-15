package com.sunzk.colortest.findSameColor

data class FindSameColorResult(
	val difficulty: FindSameColorResult.Difficulty
) {
	
	enum class Difficulty {
		Easy,
		Normal,
		Hard;
		
	}

}
