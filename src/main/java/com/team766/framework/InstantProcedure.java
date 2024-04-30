package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collection;

public abstract class InstantProcedure extends ProcedureBase implements ProcedureInterface {
    public InstantProcedure(Collection<Subsystem> reservations) {
        super(reservations);
    }

    public abstract void run();

    @Override
    public final void run(Context context) {
        run();
    }

    @Override
    public final void initialize() {}

    @Override
    public final void execute() {
        run();
    }

    @Override
    public final void end(boolean interrupted) {}

    @Override
    public final boolean isFinished() {
        return true;
    }

    @Override
    public boolean runsWhenDisabled() {
        return ContextImpl.RUNS_WHEN_DISABLED;
    }
}
