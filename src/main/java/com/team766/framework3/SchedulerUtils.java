package com.team766.framework3;

import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class SchedulerUtils {
    public static void reset() {
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().unregisterAllSubsystems();
    }
}
