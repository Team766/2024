package com.team766.framework3;

import com.team766.framework.LaunchedContext;
import com.team766.framework.StackTraceUtils;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class Scheduler implements Runnable {
    private static final Scheduler c_instance;
    private static final Thread c_monitor;

    static {
        c_instance = new Scheduler();
        c_monitor = new Thread(Scheduler::monitor);
        c_monitor.setDaemon(true);
        c_monitor.start();
    }

    public static Scheduler getInstance() {
        return c_instance;
    }

    private static void monitor() {
        int lastIterationCount = 0;
        Runnable lastRunning = null;
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            if (c_instance.m_running != null
                    && c_instance.m_iterationCount == lastIterationCount
                    && c_instance.m_running == lastRunning) {
                Logger.get(Category.FRAMEWORK)
                        .logRaw(
                                Severity.ERROR,
                                "The code has gotten stuck in "
                                        + c_instance.m_running.toString()
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

            lastIterationCount = c_instance.m_iterationCount;
            lastRunning = c_instance.m_running;
        }
    }

    private LinkedList<Runnable> m_runnables = new LinkedList<Runnable>();
    private int m_iterationCount = 0;
    private Runnable m_running = null;
    private Map<Mechanism<?>, RunnableWithContext> reservedMechanisms = new HashMap<>();

    public void add(final Runnable runnable) {
        m_runnables.add(runnable);
    }

    public void cancel(final Runnable runnable) {
        m_runnables.remove(runnable);
    }

    public void reset() {
        m_runnables.clear();
    }

    public LaunchedContext startAsync(final RunnableWithContext func) {
        return new ContextImpl(func::run);
    }

    public LaunchedContext startAsync(final Runnable func) {
        return new ContextImpl(func);
    }

    public void run() {
        ++m_iterationCount;
        for (Runnable runnable : new LinkedList<Runnable>(m_runnables)) {
            try {
                m_running = runnable;
                runnable.run();
            } catch (Exception ex) {
                ex.printStackTrace();
                LoggerExceptionUtils.logException(ex);
            } finally {
                m_running = null;
            }
        }
    }
}
