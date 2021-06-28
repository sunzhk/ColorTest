package com.sunzk.colortest.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.sunzk.colortest.Runtime
import com.sunzk.colortest.entity.TestResult
import java.util.*

class TestResultDataBase private constructor(context: Context?) : SQLiteOpenHelper(
    context,
    DBName,
    null,
    DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        var sql =
            "create table IF NOT EXISTS $TABLE_NAME( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TIME_STAMP NOT NULL DEFAULT (datetime('now','localtime')), %s FLOAT, %s FLOAT, %s FLOAT, %s FLOAT, %s FLOAT, %s FLOAT);"
        sql = String.format(
            Locale.getDefault(),
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

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
    }

    fun recordScore(
        questionH: Float,
        questionS: Float,
        questionB: Float,
        answerH: Float,
        answerS: Float,
        answerB: Float
    ) {
        try {
            writableDatabase.use { database ->
                database.beginTransaction()
                val values: ContentValues
                values = ContentValues()
                values.put(COLUMN_NAME_QUESTION_H, questionH)
                values.put(COLUMN_NAME_QUESTION_S, questionS)
                values.put(COLUMN_NAME_QUESTION_B, questionB)
                values.put(COLUMN_NAME_ANSWER_H, answerH)
                values.put(COLUMN_NAME_ANSWER_S, answerS)
                values.put(COLUMN_NAME_ANSWER_B, answerB)
                database.insert(TABLE_NAME, null, values)
                database.setTransactionSuccessful()
                database.endTransaction()
                Runtime.testResultNumber++
            }
        } catch (t: Throwable) {
            Log.e(TAG, "insert: ", t)
        }
    }

    fun listScore(): List<TestResult> {
        val testResults: MutableList<TestResult> =
            ArrayList()
        try {
            writableDatabase.use { database ->
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
                var testResult: TestResult
                while (query.moveToNext()) {
                    testResult = TestResult(
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
            }
        } catch (t: Throwable) {
            Log.e(TAG, "insert: ", t)
        }
        return testResults
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

    companion object {
        private const val TAG = "TestResultDataBase"
        private var testResultDataBase: TestResultDataBase? = null
        private const val DBName = "test_result.db"
        private const val TABLE_NAME = "TEST_RESULT"
        private const val DB_VERSION = 1
        private const val COLUMN_NAME_ID = "_id"
        private const val COLUMN_NAME_DATE = "DATE"
        private const val COLUMN_NAME_QUESTION_H = "questionH"
        private const val COLUMN_NAME_QUESTION_S = "questionS"
        private const val COLUMN_NAME_QUESTION_B = "questionB"
        private const val COLUMN_NAME_ANSWER_H = "answerH"
        private const val COLUMN_NAME_ANSWER_S = "answerS"
        private const val COLUMN_NAME_ANSWER_B = "answerB"
        private val ALL_COLUMN = arrayOf<String>()
        fun getInstance(context: Context?): TestResultDataBase {
            if (testResultDataBase == null) {
                testResultDataBase = TestResultDataBase(context)
            }
            return testResultDataBase!!
        }
    }
}