package com.sunzk.colortest.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * dp、sp 转换为 px 的工具类
 * <p/>
 * Created by chances on 16/6/27.
 */
public class DisplayUtil {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static float[] getDisplayMetrics(Context cx) {
        String str = "";
        DisplayMetrics dm = new DisplayMetrics();
        dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        float density = dm.density;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        return new float[]{screenWidth,screenHeight,density,xdpi,ydpi};

        /*
        str += "The absolute width:" + String.valueOf(screenWidth) + "pixels\n";
        str += "The absolute heightin:" + String.valueOf(screenHeight)
                + "pixels\n";
        str += "The logical density of the display.:" + String.valueOf(density)
                + "\n";
        str += "X dimension :" + String.valueOf(xdpi) + "pixels per inch\n";
        str += "Y dimension :" + String.valueOf(ydpi) + "pixels per inch\n";*/

    }

    /**
     * 是否被遮住显示不全
     * @return
     */
    public static boolean isCover(View v) {
        boolean cover = false;
        Rect rect = new Rect();
        cover = v.getGlobalVisibleRect(rect);

        //可见
        if (cover) {
            if (rect.width() >= v.getMeasuredWidth() && rect.height() >= v.getMeasuredHeight()) {
                //完全可见
                return !cover;
            }
            //被遮挡一部分
            return cover;
        } else {
            //完全被遮挡
            return !cover;
        }
    }

}
