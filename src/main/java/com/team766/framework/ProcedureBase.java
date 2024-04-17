package com.team766.framework;

import com.team766.logging.Category;

abstract class ProcedureBase implements LoggingBase {
    private static int c_idCounter = 0;

    private static synchronized int createNewId() {
        return c_idCounter++;
    }

    protected final int m_id;
    protected Category loggerCategory = Category.MECHANISMS;

    ProcedureBase() {
        m_id = createNewId();
    }

    public String getName() {
        return this.getClass().getName() + "/" + m_id;
    }

    @Override
    public final Category getLoggerCategory() {
        return loggerCategory;
    }

    @Override
    public String toString() {
        return getName();
    }
}
