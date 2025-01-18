package com.sunzk.colortest.findSameColor

import org.junit.Assert.*

class FindSameColorResultTest {
	
	@org.junit.Test
	fun testAddScore() {
		assertEquals(10, FindSameColorResult.Difficulty.Easy.addScore(1))
		assertEquals(5, FindSameColorResult.Difficulty.Easy.addScore(2))
		assertEquals(3, FindSameColorResult.Difficulty.Easy.addScore(3))
		assertEquals(0, FindSameColorResult.Difficulty.Easy.addScore(4))

		assertEquals(20, FindSameColorResult.Difficulty.Normal.addScore(1))
		assertEquals(10, FindSameColorResult.Difficulty.Normal.addScore(2))
		assertEquals(6, FindSameColorResult.Difficulty.Normal.addScore(3))
		assertEquals(5, FindSameColorResult.Difficulty.Normal.addScore(4))
		assertEquals(4, FindSameColorResult.Difficulty.Normal.addScore(5))
		assertEquals(0, FindSameColorResult.Difficulty.Normal.addScore(6))

		assertEquals(30, FindSameColorResult.Difficulty.Hard.addScore(1))
		assertEquals(15, FindSameColorResult.Difficulty.Hard.addScore(2))
		assertEquals(10, FindSameColorResult.Difficulty.Hard.addScore(3))
		assertEquals(7, FindSameColorResult.Difficulty.Hard.addScore(4))
		assertEquals(6, FindSameColorResult.Difficulty.Hard.addScore(5))
		assertEquals(5, FindSameColorResult.Difficulty.Hard.addScore(6))
		assertEquals(4, FindSameColorResult.Difficulty.Hard.addScore(7))
		assertEquals(3, FindSameColorResult.Difficulty.Hard.addScore(8))
		assertEquals(3, FindSameColorResult.Difficulty.Hard.addScore(9))
		assertEquals(0, FindSameColorResult.Difficulty.Hard.addScore(10))
	}
}