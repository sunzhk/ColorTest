package com.sunzk.colortest.utils;


import java.io.Closeable;


/**
 * @author liuyongjie create on 2018/12/4.
 */
public class CloseUtils {

    private static final String TAG = "CloseUtils";

    /**
     * 关闭cursor、IO等，让代码整洁
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Throwable e) {
            Logger.e(TAG, "close error", e);
        }
    }
}
