package com.sunzk.base.utils;

import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

/**
 * 动画工具类
 * Created by HePing on 2018/5/11.
 */

public class AnimationCodeUtils {

    //平移
    public static void translate(View view, float fromX, float toX, float fromY,
                                 float toY, long durationMillis) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation ta = new TranslateAnimation(fromX, toX, fromY,
                toY);
        ta.setFillAfter(true);
        ta.setFillBefore(false);
        ta.setDuration(durationMillis);
        animationSet.addAnimation(ta);
        animationSet.setFillAfter(true);
        animationSet.setFillBefore(false);
        view.startAnimation(animationSet);
    }
}
