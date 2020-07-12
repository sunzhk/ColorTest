package com.sunzk.colortest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sunzk.colortest.Runtime;
import com.sunzk.colortest.entity.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

public class TestResultDataBase extends SQLiteOpenHelper {

	private static final String TAG = "TestResultDataBase";

	private static TestResultDataBase testResultDataBase = null;
	private final static String DBName = "test_result.db";
	private final static String TABLE_NAME = "TEST_RESULT";
	private final static int DB_VERSION = 1;

	private static final String COLUMN_NAME_ID = "_id";
	private static final String COLUMN_NAME_DATE = "DATE";
	private static final String COLUMN_NAME_QUESTION_H = "questionH";
	private static final String COLUMN_NAME_QUESTION_S = "questionS";
	private static final String COLUMN_NAME_QUESTION_B = "questionB";
	private static final String COLUMN_NAME_ANSWER_H = "answerH";
	private static final String COLUMN_NAME_ANSWER_S = "answerS";
	private static final String COLUMN_NAME_ANSWER_B = "answerB";
	
	private static final String[] ALL_COLUMN = new String[]{};

	private TestResultDataBase(@Nullable Context context) {
		super(context, DBName, null, DB_VERSION);
	}

	public static TestResultDataBase getInstance(@Nullable Context context) {
		if (testResultDataBase == null) {
			testResultDataBase = new TestResultDataBase(context);
		}
		return testResultDataBase;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table IF NOT EXISTS " + TABLE_NAME + "( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TIME_STAMP NOT NULL DEFAULT (datetime('now','localtime')), %s FLOAT, %s FLOAT, %s FLOAT, %s FLOAT, %s FLOAT, %s FLOAT);";
		sql = String.format(Locale.getDefault(), sql, COLUMN_NAME_ID, COLUMN_NAME_DATE, COLUMN_NAME_QUESTION_H, COLUMN_NAME_QUESTION_S, COLUMN_NAME_QUESTION_B, COLUMN_NAME_ANSWER_H, COLUMN_NAME_ANSWER_S, COLUMN_NAME_ANSWER_B);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	public void recordScore(float questionH, float questionS, float questionB, float answerH, float answerS, float answerB) {
		try (SQLiteDatabase database = getWritableDatabase()) {
			database.beginTransaction();
			ContentValues values;
			values = new ContentValues();
			values.put(COLUMN_NAME_QUESTION_H, questionH);
			values.put(COLUMN_NAME_QUESTION_S, questionS);
			values.put(COLUMN_NAME_QUESTION_B, questionB);
			values.put(COLUMN_NAME_ANSWER_H, answerH);
			values.put(COLUMN_NAME_ANSWER_S, answerS);
			values.put(COLUMN_NAME_ANSWER_B, answerB);
			database.insert(TABLE_NAME, null, values);
			database.setTransactionSuccessful();
			database.endTransaction();
			Runtime.testResultNumber++;
		} catch (Throwable t) {
			Log.e(TAG, "insert: ", t);
		}
	}
	
	public List<TestResult> listScore() {
		List<TestResult> testResults = new ArrayList<>();
		try (SQLiteDatabase database = getWritableDatabase()) {
			database.beginTransaction();
			Cursor query = database.query(TABLE_NAME, null, null, null, null, null, null);
			TestResult testResult;
			while (query.moveToNext()) {
				testResult = new TestResult(query.getInt(0),
						query.getString(1),
						query.getFloat(2),
						query.getFloat(3),
						query.getFloat(4),
						query.getFloat(5),
						query.getFloat(6),
						query.getFloat(7));
				testResults.add(testResult);
			}
			query.close();
			database.setTransactionSuccessful();
			database.endTransaction();
		} catch (Throwable t) {
			Log.e(TAG, "insert: ", t);
		}
		return testResults;
	}
	
	private void tryToOpenDatabase(OnDatabaseOpenListener listener) {
		if (listener == null) {
			return;
		}
		try (SQLiteDatabase database = getWritableDatabase()) {
			listener.handleDatabase(database);
		} catch (Throwable t) {
			Log.e(TAG, "insert: ", t);
		}
	}
	
	interface OnDatabaseOpenListener {
		void handleDatabase(SQLiteDatabase sqLiteDatabase);
	}
}
