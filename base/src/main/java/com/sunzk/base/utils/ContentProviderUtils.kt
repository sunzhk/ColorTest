package com.sunzk.base.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class ContentProviderUtils {

    private static final String TAG = "ContentProviderUtils";

    public static String getContentValue(Context context, String database, String tableName, String colNameValue){
        Logger.i(TAG,"getSingleDataFromDatabase:"+database+" "+tableName+" "+colNameValue);
        String result= null;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Uri.parse("content://" + database + "/" + tableName), null, "name=?", new String[]{colNameValue}, null);
            if (cursor == null || cursor.getCount() <= 0) {
                Logger.w(TAG, "contentResolver  cursor is null for content://" + database + "/" + tableName + "/" + colNameValue);
                return null;
            }

            //移动指针到第一行
            if (cursor.moveToNext()){
                result = cursor.getString(2);
            }
            Logger.i(TAG, "contentResolver  query for content://" + database + "/" + tableName + "/" + colNameValue + "  get " + result);
        }catch(Exception e){
            Logger.e(TAG,e.getMessage(),e);
        }finally{
            if (cursor!=null){
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }
}
