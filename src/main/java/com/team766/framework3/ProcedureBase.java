package com.team766.framework3;

import com.google.common.collect.Sets;
import com.team766.logging.Category;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;

/* package */ class ProcedureBase implements LoggingBase {
    // A reusable Procedure that does nothing.
    private static final class NoOpProcedure extends InstantProcedure {
        @Override
        public void run() {}
    }

    public static final InstantRunnable NO_OP = new NoOpProcedure();

    private static int c_idCounter = 0;

    private static synchronized int createNewId() {
        return c_idCounter++;
    }

    private final String name;
    private final Set<Subsystem> reservations = Sets.newHashSet();
    protected Category loggerCategory = Category.PROCEDURES;

    protected ProcedureBase() {
        this.name = this.getClass().getName() + "/" + createNewId();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Category getLoggerCategory() {
        return loggerCategory;
    }

    protected <M extends Subsystem> M reserve(M m) {
        reservations.add(m);
        return m;
    }

    public Set<Subsystem> reservations() {
        return reservations;
    }

    @Override
    public String toString() {
        return getName();
    }
}
