package com.sunzk.colortest.findSame

data class FindSameColorResult(
	val difficulty: FindSameColorResult.Difficulty
) {
	
	enum class Difficulty {
		Easy,
		Normal,
		Hard;
		
	}

}
