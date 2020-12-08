package com.yt.easyfloat.utils;

import android.util.Log;

public class Logger {
    private static String tag = "EasyFloat";
    private static boolean logEnable = false;

    public static void setTag(String tag) {
        Logger.tag = tag;
    }

    public static void setLogEnable(boolean logEnable) {
        Logger.logEnable = logEnable;
    }

    public static void v(Object msg) {
        v(tag, msg == null ? "null" : msg.toString());
    }

    public static void v(String tag, String msg) {
        if (logEnable && tag != null && msg != null) {
            Log.v(tag, msg);
        }

    }

    public static void i(Object msg) {
        i(tag, msg == null ? "null" : msg.toString());
    }

    public static void i(String tag, String msg) {
        if (logEnable && tag != null && msg != null) {
            Log.i(tag, msg);
        }
    }

    public static void d(Object msg) {
        d(tag, msg == null ? "null" : msg.toString());
    }

    public static void d(String tag, String msg) {
        if (logEnable && tag != null && msg != null) {
            Log.d(tag, msg);
        }
    }

    public static void w(Object msg) {
        w(tag, msg == null ? "null" : msg.toString());
    }

    public static void w(String tag, String msg) {
        if (logEnable && tag != null && msg != null) {
            Log.w(tag, msg);
        }
    }

    public static void e(Object msg) {
        e(tag, msg == null ? "null" : msg.toString());
    }

    public static void e(String tag, String msg) {
        if (logEnable && tag != null && msg != null) {
            Log.e(tag, msg);
        }
    }

}
