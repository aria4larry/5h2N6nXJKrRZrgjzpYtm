package com.droidcat.stackranger.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by cat-lee on 13-5-21.
 */
public class Utilis {
    public static void showToast(Context context, String text, int type) {
        Toast.makeText(context, text, type).show();
    }

    public static void showToast(Context context, int resId, int type) {
        Toast.makeText(context, resId, type).show();
    }
}
