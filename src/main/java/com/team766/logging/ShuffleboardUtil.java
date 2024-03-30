package com.team766.logging;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class ShuffleboardUtil {

    private static boolean ENABLE_LOGGING = false;

    // private static MatchType matchType = null;

    private ShuffleboardUtil() {}

    private static boolean shouldLog() {
        return ENABLE_LOGGING;

        // if (ENABLE_LOGGING_IF_NOT_COMP) {
        //     return false;
        // }

        // TODO: test this more carefully when we have time.
        // synchronized (matchType) {
        //     if (matchType == MatchType.None) {
        //         matchType = DriverStation.getMatchType();
        //     }
        // }

        // return matchType != MatchType.None;
    }

    public static void putNumber(String key, double value) {
        if (shouldLog()) SmartDashboard.putNumber(key, value);
    }

    public static void putBoolean(String key, boolean value) {
        if (shouldLog()) SmartDashboard.putBoolean(key, value);
    }

    public static void putString(String key, String value) {
        if (shouldLog()) SmartDashboard.putString(key, value);
    }
}
