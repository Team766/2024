package com.team766.framework;

import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class SchedulerUtils {
    public static void reset() {
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().unregisterAllSubsystems();
    }

    public static LaunchedContext startAsync(final RunnableWithContext func) {
        var newContext = new ContextImpl<>(func);
        newContext.schedule();
        return newContext;
    }

    public static <T> LaunchedContextWithValue<T> startAsync(
            final RunnableWithContextWithValue<T> func) {
        var newContext = new ContextImpl<T>(func);
        newContext.schedule();
        return newContext;
    }
}
