package com.team766.framework;

public abstract class ProcedureBase extends LoggingBase {
    private static int c_idCounter = 0;

    private static synchronized int createNewId() {
        return c_idCounter++;
    }

    protected final int m_id;

    ProcedureBase() {
        m_id = createNewId();
    }

    public String getName() {
        return this.getClass().getName() + "/" + m_id;
    }

    @Override
    public String toString() {
        return getName();
    }
}
