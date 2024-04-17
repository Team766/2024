package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Commands;
import java.util.stream.Collectors;

public class SchedulerMonitor {
    private static Thread c_thread;
    private static Thread c_mainThread;
    private static int c_iterationCount = 0;

    public static void start() {
        if (c_thread != null) {
            c_mainThread = Thread.currentThread();
            Commands.run(
                    () -> {
                        ++c_iterationCount;
                    });
            c_thread = new Thread(SchedulerMonitor::monitor);
            c_thread.setDaemon(true);
            c_thread.start();
        }
    }

    private static void monitor() {
        int lastIterationCount = 0;
        Context lastRunningContext = null;
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            final ContextImpl<?> running = ContextImpl.currentContext();
            if (c_iterationCount == lastIterationCount && running == lastRunningContext) {
                final String activeStackTrace =
                        running != null
                                ? running.toString() + "\n" + running.getStackTrace()
                                : "main thread\n" + StackTraceUtils.getStackTrace(c_mainThread);
                Logger.get(Category.FRAMEWORK)
                        .logRaw(
                                Severity.ERROR,
                                "The code has gotten stuck in "
                                        + activeStackTrace
                                        + " You probably have an unintended infinite loop or need to add a call to context.yield()");
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
            lastRunningContext = running;
        }
    }
}
