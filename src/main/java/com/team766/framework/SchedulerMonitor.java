package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import java.util.stream.Collectors;

public class SchedulerMonitor {
    private static Thread c_thread = null;
    private static Command c_currentCommand = null;
    private static int c_iterationCount = 0;

    public static void start() {
        if (c_thread != null) {
            Commands.run(
                    () -> {
                        ++c_iterationCount;
                    });
            CommandScheduler.getInstance()
                    .onCommandBeforeExecute(command -> c_currentCommand = command);
            CommandScheduler.getInstance().onCommandExecute(command -> c_currentCommand = null);
            c_thread = new Thread(SchedulerMonitor::monitor);
            c_thread.setDaemon(true);
            c_thread.start();
        }
    }

    /* package */ static Command getCurrentCommand() {
        if (c_thread == null) {
            throw new IllegalStateException(
                    "Tried to retrieve current command, but SchedulerMonitor is not running");
        }
        return c_currentCommand;
    }

    private static void monitor() {
        int lastIterationCount = 0;
        Command lastRunningCommand = null;
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            final Command running = c_currentCommand;
            if (c_iterationCount == lastIterationCount && running == lastRunningCommand) {
                final String commandName = running != null ? running.getName() : "non-Command code";
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
            lastRunningCommand = running;
        }
    }
}
