package com.team766.framework3;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;

public class InstantCommand extends Command {
    private final Runnable runnable;

    public InstantCommand(InstantRunnable runnable) {
        this(runnable.reservations(), runnable);
    }

    public InstantCommand(Set<Subsystem> requirements, Runnable runnable) {
        this.runnable = runnable;
        m_requirements.addAll(requirements);
        setName(runnable.toString());
    }

    @Override
    public void execute() {
        try {
            SchedulerMonitor.currentCommand = this;
            runnable.run();
        } finally {
            SchedulerMonitor.currentCommand = null;
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
