package com.example.fk.fiapfood.helper;

import android.util.Log;

public class Helper {

    private static final String TAG = "FIAPFOOOOOOOOOOOOOOOOOD";

    public static void logMethodName(Object o) {
        Log.w(TAG, "method: " + o.getClass().getEnclosingMethod().getName());
    }

    public static void logMethodName(String classTag, Object o) {
        Log.w(classTag, "method: " + o.getClass().getEnclosingMethod().getName());
    }
}
