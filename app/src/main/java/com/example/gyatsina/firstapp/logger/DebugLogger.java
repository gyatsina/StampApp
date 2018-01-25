package com.example.gyatsina.firstapp.logger;

import android.util.Log;

public class DebugLogger {

    private static boolean sIsDebug;

    // private
    private DebugLogger() {
        // empty
    }

    // private
    private DebugLogger(boolean isDebug) {
        sIsDebug = isDebug;
    }

    public static class DebugLoggerHolder {
        private static DebugLogger sDebugLogger = new DebugLogger(true);
    }

    /**
     * Enable logging
     */
    public static void enableLogging() {
        DebugLogger debugLogger = DebugLoggerHolder.sDebugLogger;
        DebugLogger.d(DebugLogger.class.getSimpleName(), debugLogger + " is enabled for logging");
    }

    private static final String NULL = "NULL";

    public static void d(String tag, String message) {
        if (sIsDebug) {
            Log.d(tag, message != null ? message : NULL);
        }
    }

    public static void e(String tag, String message) {
        if (sIsDebug) {
            Log.e(tag, message != null ? message : NULL);
        }
    }

    public static void e(String tag, String message, Throwable e) {
        if (sIsDebug) {
            Log.e(tag, message != null ? message : NULL, e);
        }
    }

    public static void w(String tag, String message) {
        if (sIsDebug) {
            Log.w(tag, message != null ? message : NULL);
        }
    }

    public static void v(String tag, String message) {
        if (sIsDebug) {
            Log.v(tag, message != null ? message : NULL);
        }
    }

    public static void i(String tag, String message) {
        if (sIsDebug) {
            Log.i(tag, message != null ? message : NULL);
        }
    }

    public static void wtf(String tag, String message) {
        if (sIsDebug) {
            Log.wtf(tag, message != null ? message : NULL);
        }
    }
}
