package com.team766.framework3;

import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;
import java.util.function.Consumer;

public final class FunctionalProcedure extends Procedure {
    private final Consumer<Context> runnable;

    public FunctionalProcedure(Set<Subsystem> reservations, Consumer<Context> runnable) {
        super(runnable.toString(), reservations);
        this.runnable = runnable;
    }

    @Override
    public void run(Context context) {
        runnable.accept(context);
    }
}
