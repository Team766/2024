package com.team766.framework;

import com.google.common.reflect.TypeToken;
import com.team766.framework.annotations.CollectReservations;
import com.team766.library.ReflectionUtils;
import com.team766.logging.Category;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.ReflectionLogging;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;

@CollectReservations
public abstract class MagicProcedure<Reservations> implements LoggingBase, Statuses.StatusSource {
    private static int c_idCounter = 0;

    private static synchronized int createNewId() {
        return c_idCounter++;
    }

    private final String m_name;
    private boolean isStatusActive = false;
    protected Category loggerCategory = Category.PROCEDURES;

    public MagicProcedure() {
        var id = createNewId();
        m_name = this.getClass().getName() + "/" + id;
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

    @Override
    public Category getLoggerCategory() {
        return loggerCategory;
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public String toString() {
        return getName();
    }

    protected final class Context {
        private final com.team766.framework.Context delegate;
        private final Reservations reservations;

        public Context(com.team766.framework.Context delegate, Reservations reservations) {
            this.delegate = delegate;
            this.reservations = reservations;
        }

        public boolean waitForConditionOrTimeout(BooleanSupplier predicate, double timeoutSeconds) {
            return delegate.waitForConditionOrTimeout(predicate, timeoutSeconds);
        }

        public void waitFor(BooleanSupplier predicate) {
            delegate.waitFor(predicate);
        }

        public void yield() {
            delegate.yield();
        }

        public void waitForSeconds(double seconds) {
            delegate.waitForSeconds(seconds);
        }

        public void startAsyncUnchecked(Command procedure) {
            // TODO: Can annotations check that async procedures have disjoint reservations?
            delegate.startAsync(procedure);
        }

        public void runSyncUnchecked(ProcedureInterface procedure) {
            delegate.runSync(procedure);
        }

        public void runParallelUnchecked(Command... procedures) {
            delegate.runParallel(procedures);
        }

        public void runSync(MagicProcedure<? super Reservations> procedure) {
            delegate.runSync(procedure.prepareProcedure(reservations));
        }

        @SafeVarargs
        public final void runParallel(MagicProcedure<? super Reservations>... procedures) {
            // TODO: Can annotations check that parallel sub-procedures have disjoint reservations?
            Command[] commands = new Command[procedures.length];
            for (int i = 0; i < procedures.length; ++i) {
                commands[i] = procedures[i].prepareProcedure(reservations);
            }
            delegate.runParallel(commands);
        }
    }

    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends MagicProcedure>, ReservationsInterface.EntryPoint>
            applyReservations = new HashMap<>();

    @SuppressWarnings("unchecked")
    public final ProcedureBase prepareProcedure(Reservations reservations) {
        var reservationEntryPoint = applyReservations.computeIfAbsent(this.getClass(), clazz -> {
            var procedureType = TypeToken.of(clazz).getSupertype(MagicProcedure.class);
            var reservationsType =
                    ReflectionUtils.getRawType(ReflectionUtils.getTypeArguments(procedureType)[0]);
            return ReservationsInterface.makeEntryPoint(reservationsType);
        });
        Set<Subsystem> reservationsSet = new HashSet<>();
        reservationEntryPoint.applyReservations(reservations, this, reservationsSet);
        return new ProcedureBase(reservationsSet) {
            {
                setName(MagicProcedure.this.getName());
            }

            @Override
            public final void execute(com.team766.framework.Context context) {
                try {
                    isStatusActive = true;
                    MagicProcedure.this.run(new Context(context, reservations));
                } finally {
                    isStatusActive = false;
                }
            }
        };
    }

    protected abstract void run(Context context);
}
