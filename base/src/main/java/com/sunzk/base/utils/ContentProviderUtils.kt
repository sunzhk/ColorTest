package com.sunzk.base.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.sunzk.base.utils.Logger.e
import com.sunzk.base.utils.Logger.i
import com.sunzk.base.utils.Logger.w

object ContentProviderUtils {
    private const val TAG = "ContentProviderUtils"
    fun getContentValue(
        context: Context,
        database: String,
        tableName: String,
        colNameValue: String
    ): String? {
        i(TAG, "getSingleDataFromDatabase:$database $tableName $colNameValue")
        var result: String? = null
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                Uri.parse("content://$database/$tableName"),
                null,
                "name=?",
                arrayOf(colNameValue),
                null
            )
            if (cursor == null || cursor.count <= 0) {
                w(
                    TAG,
                    "contentResolver  cursor is null for content://$database/$tableName/$colNameValue"
                )
                return null
            }

            //移动指针到第一行
            if (cursor.moveToNext()) {
                result = cursor.getString(2)
            }
            i(
                TAG,
                "contentResolver  query for content://$database/$tableName/$colNameValue  get $result"
            )
        } catch (e: Exception) {
            e(TAG, e.message!!, e)
        } finally {
            if (cursor != null) {
                cursor.close()
                cursor = null
            }
        }
        return result
    }
}