package com.sunzk.colortest.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.sunzk.colortest.MyApplication

private const val DBName = "color_test_result.db"
private const val DB_VERSION = 2
object ColorTestCommonDataBase : SQLiteOpenHelper(
	MyApplication.instance,
	DBName,
	null,
	DB_VERSION
) {
	private const val TAG: String = "ColorTestCommonDataBase"

	override fun onCreate(db: SQLiteDatabase) {
		MockColorResultTable.onCreate(db)
		IntermediateColorResultTable.onCreate(db)
	}

	override fun onUpgrade(
		db: SQLiteDatabase,
		oldVersion: Int,
		newVersion: Int,
	) {
		MockColorResultTable.onUpgrade(db, oldVersion, newVersion)
		IntermediateColorResultTable.onUpgrade(db, oldVersion, newVersion)
		if (newVersion == DB_VERSION) {
			IntermediateColorResultTable.onCreate(db)
		}
	}

}