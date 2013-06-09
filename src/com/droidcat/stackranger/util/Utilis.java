package com.droidcat.stackranger.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by cat-lee on 13-5-21.
 */
public class Utilis {
    public static String access_token = "";

    public static void showToast(Context context, String text, int type) {
        Toast.makeText(context, text, type).show();
    }

    public static void showToast(Context context, int resId, int type) {
        Toast.makeText(context, resId, type).show();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
