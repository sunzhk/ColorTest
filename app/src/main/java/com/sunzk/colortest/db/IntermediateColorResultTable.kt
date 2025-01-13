package com.sunzk.colortest.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.db.bean.IntermediateColorResult
import com.sunzk.colortest.entity.HSB
import java.util.ArrayList

/**
 * 找中间颜色结果表
 */
object IntermediateColorResultTable: ITable<IntermediateColorResult> {
	
	private const val TAG: String = "IntermediateColorResultTable"

	private const val TABLE_NAME = "INTERMEDIATE_COLOR_TEST_RESULT"
	private const val COLUMN_NAME_ID = "_id"
	private const val COLUMN_NAME_DATE = "DATE"
	private const val COLUMN_NAME_DIFFICULTY = "DIFFICULTY"
	private const val COLUMN_NAME_QUESTION_LEFT_H = "questionLeftH"
	private const val COLUMN_NAME_QUESTION_LEFT_S = "questionLeftS"
	private const val COLUMN_NAME_QUESTION_LEFT_B = "questionLeftB"
	private const val COLUMN_NAME_QUESTION_RIGHT_H = "questionRightH"
	private const val COLUMN_NAME_QUESTION_RIGHT_S = "questionRightS"
	private const val COLUMN_NAME_QUESTION_RIGHT_B = "questionRightB"
	private const val COLUMN_NAME_ANSWER_H = "answerH"
	private const val COLUMN_NAME_ANSWER_S = "answerS"
	private const val COLUMN_NAME_ANSWER_B = "answerB"

	override fun onCreate(db: SQLiteDatabase) {
		var sql = "create table IF NOT EXISTS $TABLE_NAME( $COLUMN_NAME_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"$COLUMN_NAME_DATE TIME_STAMP NOT NULL DEFAULT (datetime('now','localtime')), " +
				"$COLUMN_NAME_DIFFICULTY TEXT, " +
				"$COLUMN_NAME_QUESTION_LEFT_H FLOAT, " +
				"$COLUMN_NAME_QUESTION_LEFT_S FLOAT, " +
				"$COLUMN_NAME_QUESTION_LEFT_B FLOAT, " +
				"$COLUMN_NAME_QUESTION_RIGHT_H FLOAT, " +
				"$COLUMN_NAME_QUESTION_RIGHT_S FLOAT, " +
				"$COLUMN_NAME_QUESTION_RIGHT_B FLOAT, " +
				"$COLUMN_NAME_ANSWER_H FLOAT, " +
				"$COLUMN_NAME_ANSWER_S FLOAT, " +
				"$COLUMN_NAME_ANSWER_B FLOAT);"
		db.execSQL(sql)
	}

	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		
	}

	override fun queryAll(): List<IntermediateColorResult>? {
		try {
			val testResults: MutableList<IntermediateColorResult> = ArrayList()
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
				while (query.moveToNext()) {
					testResults.add(IntermediateColorResult(
						query.getInt(0),
						query.getString(1),
						IntermediateColorResult.Difficulty.entries.find { it.name == query.getString(2) }
							?: IntermediateColorResult.Difficulty.Normal,
						HSB(query.getFloat(3),
							query.getFloat(4),
							query.getFloat(5)),
						HSB(query.getFloat(6),
							query.getFloat(7),
							query.getFloat(8)),
						HSB(query.getFloat(9),
							query.getFloat(10),
							query.getFloat(11))
					))
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

	override fun delete(bean: IntermediateColorResult): Boolean {
		TODO("Not yet implemented")
	}

	override fun add(bean: IntermediateColorResult): Boolean {
		var result = false
		try {
			ColorTestCommonDataBase.writableDatabase.use { database ->
				database.beginTransaction()
				val values: ContentValues = ContentValues()
				values.put(COLUMN_NAME_DIFFICULTY, bean.difficulty.name)
				values.put(COLUMN_NAME_QUESTION_LEFT_H, bean.questionLeftH)
				values.put(COLUMN_NAME_QUESTION_LEFT_S, bean.questionLeftS)
				values.put(COLUMN_NAME_QUESTION_LEFT_B, bean.questionLeftB)
				values.put(COLUMN_NAME_QUESTION_RIGHT_H, bean.questionRightH)
				values.put(COLUMN_NAME_QUESTION_RIGHT_S, bean.questionRightS)
				values.put(COLUMN_NAME_QUESTION_RIGHT_B, bean.questionRightB)
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