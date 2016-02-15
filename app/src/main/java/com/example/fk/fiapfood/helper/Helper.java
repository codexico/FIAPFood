package com.example.fk.fiapfood.helper;

import android.util.Log;

public class Helper {

    protected static final String TAG = "FIAPFOOOOOOOOOOOOOOOOOD";

    public static void logMethodName(Object o) {
        Log.w(TAG, "method: " + o.getClass().getEnclosingMethod().getName());
    }
}
