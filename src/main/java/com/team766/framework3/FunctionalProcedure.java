package com.team766.framework3;

import java.util.Set;
import java.util.function.Consumer;

public final class FunctionalProcedure extends Procedure {
    private final Consumer<Context> runnable;

    public FunctionalProcedure(Set<Mechanism<?>> reservations, Consumer<Context> runnable) {
        super(runnable.toString(), reservations);
        this.runnable = runnable;
    }

    @Override
    public void run(Context context) {
        runnable.accept(context);
    }
}
