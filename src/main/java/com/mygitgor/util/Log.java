package com.mygitgor.util;

public class Log {
    private static final boolean DEBUG_ENABLED = true;

    private Log() {}

    public static void info(String message) {
        System.out.println("[INFO] " + getCallerInfo() + message);
    }

    public static void error(String message) {
        System.err.println("[ERROR] " + getCallerInfo() + message);
    }

    public static void debug(String message) {
        if (DEBUG_ENABLED) {
            System.out.println("[DEBUG] " + getCallerInfo() + message);
        }
    }

    public static void warn(String message) {
        System.out.println("[WARN] " + getCallerInfo() + message);
    }

    private static String getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            StackTraceElement caller = stackTrace[3];
            return caller.getClassName() + "." + caller.getMethodName() + "() - ";
        }
        return "";
    }
}
