package com.sunzk.colortest.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.db.bean.MockColorResult
import java.util.ArrayList
import java.util.Locale

/**
 * 模仿颜色结果表
 */
object MockColorResultTable: ITable<MockColorResult> {
	
	private const val TAG: String = "MockColorResultTable"

	private const val TABLE_NAME = "MOCK_COLOR_TEST_RESULT"
	private const val COLUMN_NAME_ID = "_id"
	private const val COLUMN_NAME_DATE = "DATE"
	private const val COLUMN_NAME_QUESTION_H = "questionH"
	private const val COLUMN_NAME_QUESTION_S = "questionS"
	private const val COLUMN_NAME_QUESTION_B = "questionB"
	private const val COLUMN_NAME_ANSWER_H = "answerH"
	private const val COLUMN_NAME_ANSWER_S = "answerS"
	private const val COLUMN_NAME_ANSWER_B = "answerB"

	override fun onCreate(db: SQLiteDatabase) {
		var sql =
			"create table IF NOT EXISTS $TABLE_NAME( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TIME_STAMP NOT NULL DEFAULT (datetime('now','localtime')), %s FLOAT, %s FLOAT, %s FLOAT, %s FLOAT, %s FLOAT, %s FLOAT);"
		sql = String.format(
			Locale.US,
			sql,
			COLUMN_NAME_ID,
			COLUMN_NAME_DATE,
			COLUMN_NAME_QUESTION_H,
			COLUMN_NAME_QUESTION_S,
			COLUMN_NAME_QUESTION_B,
			COLUMN_NAME_ANSWER_H,
			COLUMN_NAME_ANSWER_S,
			COLUMN_NAME_ANSWER_B
		)
		db.execSQL(sql)
	}

	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		
	}

	override fun queryAll(): List<MockColorResult>? {
		try {
			val testResults: MutableList<MockColorResult> = ArrayList()
			ColorTestCommonDataBase.readableDatabase.use { database ->
				database.beginTransaction()
				val query = database.query(
					TABLE_NAME,
					null,
					null,
					null,
					null,
					null,
					null
				)
				var testResult: MockColorResult
				while (query.moveToNext()) {
					testResult = MockColorResult(
						query.getInt(0),
						query.getString(1),
						query.getFloat(2),
						query.getFloat(3),
						query.getFloat(4),
						query.getFloat(5),
						query.getFloat(6),
						query.getFloat(7)
					)
					testResults.add(testResult)
				}
				query.close()
				database.setTransactionSuccessful()
				database.endTransaction()
				return testResults
			}
		} catch (t: Throwable) {
			Log.e(TAG, "insert: ", t)
		}
		return null
	}

	override fun delete(bean: MockColorResult): Boolean {
		TODO("Not yet implemented")
	}

	override fun add(bean: MockColorResult): Boolean {
		var result = false
		try {
			ColorTestCommonDataBase.writableDatabase.use { database ->
				database.beginTransaction()
				val values: ContentValues
				values = ContentValues()
				values.put(COLUMN_NAME_QUESTION_H, bean.questionH)
				values.put(COLUMN_NAME_QUESTION_S, bean.questionS)
				values.put(COLUMN_NAME_QUESTION_B, bean.questionB)
				values.put(COLUMN_NAME_ANSWER_H, bean.answerH)
				values.put(COLUMN_NAME_ANSWER_S, bean.answerS)
				values.put(COLUMN_NAME_ANSWER_B, bean.answerB)
				result = database.insert(TABLE_NAME, null, values) > -1
				database.setTransactionSuccessful()
				database.endTransaction()
				Runtime.testResultNumber++
			}
		} catch (t: Throwable) {
			Log.e(TAG, "insert: ", t)
		}
		return result
	}
}