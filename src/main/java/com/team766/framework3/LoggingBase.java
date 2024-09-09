package com.team766.framework3;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public interface LoggingBase {
    Category getLoggerCategory();

    default String getName() {
        return this.getClass().getName();
    }

    default void log(final String message) {
        log(Severity.INFO, message);
    }

    default void log(final Severity severity, final String message) {
        Logger.get(getLoggerCategory()).logRaw(severity, getName() + ": " + message);
    }

    default void log(final String format, final Object... args) {
        log(Severity.INFO, format, args);
    }

    default void log(final Severity severity, final String format, final Object... args) {
        Logger.get(getLoggerCategory()).logData(severity, getName() + ": " + format, args);
    }
}
