package com.team766.framework3;

import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;
import java.util.function.Consumer;

public final class FunctionalRunnableWithContext implements RunnableWithContext {
    private final Consumer<Context> runnable;
    private final Set<Subsystem> reservations;

    public FunctionalRunnableWithContext(Set<Subsystem> reservations, Consumer<Context> runnable) {
        this.runnable = runnable;
        this.reservations = reservations;
    }

    @Override
    public void run(Context context) {
        runnable.accept(context);
    }

    @Override
    public Set<Subsystem> reservations() {
        return reservations;
    }

    @Override
    public String toString() {
        return runnable.toString();
    }
}
