package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.ReflectionLogging;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collection;
import java.util.Optional;

public abstract class Procedure extends ProcedureBase
        implements LoggingBase, Statuses.StatusSource {
    // A reusable Procedure that does nothing.
    private static final class NoOpProcedure extends Procedure {
        public NoOpProcedure() {
            super(NO_RESERVATIONS);
        }

        @Override
        protected void run(final Context context) {}
    }

    public static Procedure noOp() {
        return new NoOpProcedure();
    }

    private static int c_idCounter = 0;

    private static synchronized int createNewId() {
        return c_idCounter++;
    }

    protected Category loggerCategory = Category.PROCEDURES;

    private boolean isStatusActive = false;

    public Procedure(Collection<Subsystem> reservations) {
        super(reservations);
        final var id = createNewId();
        setName(this.getClass().getName() + "/" + id);
    }

    protected abstract void run(Context context);

    @Override
    public final void execute(Context context) {
        try {
            isStatusActive = true;
            run(context);
        } finally {
            isStatusActive = false;
        }
    }

    @Override
    public Category getLoggerCategory() {
        return loggerCategory;
    }

    protected final void updateStatus(Record status) {
        try {
            ReflectionLogging.recordOutput(
                    status, getName() + "/" + status.getClass().getSimpleName());
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
        Statuses.getInstance().add(status, this);
    }

    protected final <StatusRecord extends Record> Optional<StatusRecord> getStatus(
            Class<StatusRecord> c) {
        return Statuses.getStatus(c);
    }

    @Override
    public final boolean isStatusActive() {
        return isStatusActive;
    }
}
