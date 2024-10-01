package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import java.util.stream.Collectors;

public class SchedulerMonitor {
    private static Thread c_thread = null;
    private static int c_iterationCount = 0;

    public static void start() {
        if (c_thread != null) {
            CommandScheduler.getInstance()
                    .onCommandExecute(
                            __ -> {
                                ++c_iterationCount;
                            });
            c_thread = new Thread(SchedulerMonitor::monitor);
            c_thread.setDaemon(true);
            c_thread.start();
        }
    }

    private static void monitor() {
        int lastIterationCount = 0;
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            if (c_iterationCount == lastIterationCount) {
                Logger.get(Category.FRAMEWORK)
                        .logRaw(
                                Severity.ERROR,
                                "The code has gotten stuck. You probably have an unintended infinite "
                                        + "loop or need to add a call to context.yield() in a Procedure.\n"
                                        + Thread.getAllStackTraces().entrySet().stream()
                                                .map(
                                                        e ->
                                                                e.getKey().getName()
                                                                        + ":\n"
                                                                        + StackTraceUtils
                                                                                .getStackTrace(
                                                                                        e
                                                                                                .getValue()))
                                                .collect(Collectors.joining("\n")));
            }

            lastIterationCount = c_iterationCount;
        }
    }
}
