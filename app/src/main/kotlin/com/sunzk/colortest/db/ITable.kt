package com.sunzk.colortest.db

import android.database.sqlite.SQLiteDatabase

interface ITable<B> {
	fun onCreate(db: SQLiteDatabase)
	
	fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
	
	fun queryAll(): List<B>?
	
	fun add(bean: B): Boolean
	
	fun delete(bean: B): Boolean
}