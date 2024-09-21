package com.team766.framework3;

import com.team766.framework.StackTraceUtils;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import java.util.stream.Collectors;

public class SchedulerMonitor {
    /*
     * The currently-executing Command (Context/InstantCommand).
     *
     * This is maintained for things like checking Mechanism ownership, but
     * intentionally only has package-private visibility - code outside of the
     * framework should pass around references to the current context object
     * rather than cheating with this static accessor.
     */
    static Command currentCommand = null;

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
                final String commandName =
                        currentCommand != null ? currentCommand.getName() : "non-Procedure code";
                Logger.get(Category.FRAMEWORK)
                        .logRaw(
                                Severity.ERROR,
                                "The code has gotten stuck in "
                                        + commandName
                                        + ". You probably have an unintended infinite loop or need to add a call to context.yield()");
                Logger.get(Category.FRAMEWORK)
                        .logRaw(
                                Severity.INFO,
                                Thread.getAllStackTraces().entrySet().stream()
                                        .map(
                                                e ->
                                                        e.getKey().getName()
                                                                + ":\n"
                                                                + StackTraceUtils.getStackTrace(
                                                                        e.getValue()))
                                        .collect(Collectors.joining("\n")));
            }

            lastIterationCount = c_iterationCount;
        }
    }
}
