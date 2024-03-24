package com.team766.logging;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class ShuffleboardUtil {

    private static boolean ENABLE_LOGGING_IF_NOT_COMP = true;
    private static MatchType matchType = null;

    private ShuffleboardUtil() {}

    private static boolean shouldLog() {

        if (ENABLE_LOGGING_IF_NOT_COMP) {
            return false;
        }

        synchronized (matchType) {
            if (matchType == MatchType.None) {
                matchType = DriverStation.getMatchType();
            }
        }

        return matchType != MatchType.None;
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
