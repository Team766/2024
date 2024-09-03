package com.team766.framework;

public class StackTraceUtils {
    public static String getStackTrace(final Thread thread) {
        StackTraceElement[] stackTrace;
        try {
            stackTrace = thread.getStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return getStackTrace(stackTrace);
    }

    public static String getStackTrace(final StackTraceElement[] stackTrace) {
        String repr = "";
        for (var stackFrame : stackTrace) {
            repr += " at " + stackFrame.getClassName() + "." + stackFrame.getMethodName();
            if (stackFrame.getFileName() != null) {
                repr += " (" + stackFrame.getFileName() + ":" + stackFrame.getLineNumber() + ")\n";
            }
        }
        return repr;
    }
}
