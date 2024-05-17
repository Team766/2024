package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.LoggerExceptionUtils;

public abstract class LightsBase implements LoggingBase {
    protected abstract void dispatch(Statuses statuses);

    @Override
    public Category getLoggerCategory() {
        return Category.OPERATOR_INTERFACE;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    public final void run() {
        try {
            final var statuses = Statuses.getInstance();
            dispatch(statuses);
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
    }
}
