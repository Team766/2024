package com.team766.framework3;

import com.google.common.collect.Sets;
import com.team766.logging.Category;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
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
    private final Set<Subsystem> reservations;
    protected Category loggerCategory = Category.PROCEDURES;

    protected Procedure() {
        this.name = this.getClass().getName() + "/" + createNewId();
        this.reservations = Sets.newHashSet();
    }

    protected Procedure(String name, Set<Subsystem> reservations) {
        this.name = name;
        this.reservations = reservations;
    }

    public abstract void run(Context context);

    Command createCommand() {
        return new ContextImpl(this);
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public Category getLoggerCategory() {
        return loggerCategory;
    }

    protected final <M extends Subsystem> M reserve(M m) {
        reservations.add(m);
        return m;
    }

    protected final void reserve(Subsystem... ms) {
        for (var m : ms) {
            reservations.add(m);
        }
    }

    protected final void reserve(Collection<? extends Subsystem> ms) {
        reservations.addAll(ms);
    }

    public final Set<Subsystem> reservations() {
        return reservations;
    }

    @Override
    public final String toString() {
        return getName();
    }
}
