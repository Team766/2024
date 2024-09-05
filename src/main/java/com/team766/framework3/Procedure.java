package com.team766.framework3;

import com.google.common.collect.Sets;
import com.team766.framework.LoggingBase;
import java.util.Set;

public abstract class Procedure extends LoggingBase implements RunnableWithContext {
    // A reusable Procedure that does nothing.
    private static final class NoOpProcedure extends Procedure {
        @Override
        public void run(final Context context) {}
    }

    public static final Procedure NO_OP = new NoOpProcedure();

    private static int c_idCounter = 0;

    private static synchronized int createNewId() {
        return c_idCounter++;
    }

    protected final int m_id;
    private final Set<Mechanism<?>> reservations = Sets.newHashSet();

    Procedure() {
        m_id = createNewId();
    }

    @Override
    public String getName() {
        return this.getClass().getName() + "/" + m_id;
    }

    @Override
    public <M extends Mechanism<?>> M reserve(M m) {
        reservations.add(m);
        return m;
    }

    @Override
    public void release(Mechanism<?> m) {
        reservations.remove(m);
    }

    @Override
    public Set<Mechanism<?>> reservations() {
        return reservations;
    }

    @Override
    public String toString() {
        return getName();
    }
}
