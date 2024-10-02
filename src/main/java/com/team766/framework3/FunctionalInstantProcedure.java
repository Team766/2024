package com.team766.framework3;

import java.util.Set;

public final class FunctionalInstantProcedure extends InstantProcedure {
    private final Runnable runnable;

    public FunctionalInstantProcedure(Set<Mechanism<?>> reservations, Runnable runnable) {
        this(runnable.toString(), reservations, runnable);
    }

    public FunctionalInstantProcedure(
            String name, Set<Mechanism<?>> reservations, Runnable runnable) {
        super(name, reservations);
        this.runnable = runnable;
    }

    @Override
    public void run() {
        runnable.run();
    }
}
