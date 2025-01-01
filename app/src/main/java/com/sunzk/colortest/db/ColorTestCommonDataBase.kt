package com.sunzk.colortest.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.sunzk.colortest.MyApplication

private const val DBName = "color_test_result.db"
private const val DB_VERSION = 1
object ColorTestCommonDataBase : SQLiteOpenHelper(
	MyApplication.instance,
	DBName,
	null,
	DB_VERSION
) {
	private const val TAG: String = "ColorTestCommonDataBase"

	override fun onCreate(db: SQLiteDatabase) {
		MockColorResultTable.onCreate(db)
	}

	override fun onUpgrade(
		db: SQLiteDatabase,
		oldVersion: Int,
		newVersion: Int,
	) {
		MockColorResultTable.onUpgrade(db, oldVersion, newVersion)
	}

	private fun tryToOpenDatabase(listener: OnDatabaseOpenListener?) {
		if (listener == null) {
			return
		}
		try {
			writableDatabase.use { database -> listener.handleDatabase(database) }
		} catch (t: Throwable) {
			Log.e(TAG, "insert: ", t)
		}
	}

	internal interface OnDatabaseOpenListener {
		fun handleDatabase(sqLiteDatabase: SQLiteDatabase?)
	}
}