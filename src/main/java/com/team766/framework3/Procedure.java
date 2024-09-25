package com.team766.framework3;

import com.google.common.collect.Sets;
import com.team766.logging.Category;
import edu.wpi.first.wpilibj2.command.Command;
import java.util.Collection;
import java.util.Set;

public abstract class Procedure implements LoggingBase {
    // A reusable Procedure that does nothing.
    private static final class NoOpProcedure extends InstantProcedure {
        @Override
        public void run() {}
    }

    public static final InstantProcedure NO_OP = new NoOpProcedure();

    private static int c_idCounter = 0;

    private static synchronized int createNewId() {
        return c_idCounter++;
    }

    private final String name;
    private final Set<Mechanism<?>> reservations;

    protected Procedure() {
        this.name = createName();
        this.reservations = Sets.newHashSet();
    }

    protected Procedure(Set<Mechanism<?>> reservations) {
        this.name = createName();
        this.reservations = reservations;
    }

    protected Procedure(String name, Set<Mechanism<?>> reservations) {
        this.name = name;
        this.reservations = reservations;
    }

    public abstract void run(Context context);

    /* package */ Command createCommand() {
        return new ContextImpl(this);
    }

    private String createName() {
        return this.getClass().getName() + "/" + createNewId();
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public Category getLoggerCategory() {
        return Category.PROCEDURES;
    }

    protected final <M extends Mechanism<?>> M reserve(M m) {
        reservations.add(m);
        return m;
    }

    protected final void reserve(Mechanism<?>... ms) {
        for (var m : ms) {
            reservations.add(m);
        }
    }

    protected final void reserve(Collection<? extends Mechanism<?>> ms) {
        reservations.addAll(ms);
    }

    public final Set<Mechanism<?>> reservations() {
        return reservations;
    }

    @Override
    public final String toString() {
        return getName();
    }
}
