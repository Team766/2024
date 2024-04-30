package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Adapts a Context into a ContextWithValue<T> by taking a Consumer<T> which specifies how
 * to handle any values yielded by Procedures.
 */
public final class YieldedValues<T> implements ContextWithValue<T> {
    /**
     * Adapts a Context into a ContextWithValue<T> such that any values yielded by Procedures using
     * this Context are discarded.
     */
    public static <T> ContextWithValue<T> discard(Context context) {
        return new YieldedValues<>(context, (__) -> {});
    }

    /**
     * Adapts a ProcedureWithValue into a Procedure by discarding any values that it produces.
     */
    public static <T> ProcedureInterface discard(ProcedureWithValue<T> procedure) {
        return new ProcedureInterface() {
            @Override
            public void run(Context context) {
                procedure.run(discard(context));
            }

            @Override
            public Set<Subsystem> getReservations() {
                return procedure.getReservations();
            }
        };
    }

    /**
     * Adapts a Context into a ContextWithValue<T> such that any values yielded by Procedures using
     * this Context are collected into the provided List.
     */
    public static <T> ContextWithValue<T> collectInto(Context context, List<T> valuesCollection) {
        return new YieldedValues<>(context, valuesCollection::add);
    }

    /**
     * Adapts a ProcedureWithValue into a Procedure by collecting any values that it produces into
     * the provided List.
     */
    public static <T> ProcedureInterface collectInto(
            ProcedureWithValue<T> procedure, List<T> valuesCollection) {
        return new ProcedureInterface() {
            @Override
            public void run(Context context) {
                procedure.run(collectInto(context, valuesCollection));
            }

            @Override
            public Set<Subsystem> getReservations() {
                return procedure.getReservations();
            }
        };
    }

    private final Context parentContext;
    private final Consumer<T> valueCallback;

    private YieldedValues(Context parentContext, Consumer<T> valueCallback) {
        this.parentContext = parentContext;
        this.valueCallback = valueCallback;
    }

    @Override
    public boolean waitForConditionOrTimeout(BooleanSupplier predicate, double timeoutSeconds) {
        return parentContext.waitForConditionOrTimeout(predicate, timeoutSeconds);
    }

    @Override
    public void waitFor(BooleanSupplier predicate) {
        parentContext.waitFor(predicate);
    }

    @Override
    public void waitFor(LaunchedContext otherContext) {
        parentContext.waitFor(otherContext);
    }

    @Override
    public void waitFor(LaunchedContext... otherContexts) {
        parentContext.waitFor(otherContexts);
    }

    @Override
    public void yield() {
        parentContext.yield();
    }

    @Override
    public void waitForSeconds(double seconds) {
        parentContext.waitForSeconds(seconds);
    }

    @Override
    public LaunchedContext startAsync(Command cmd) {
        return parentContext.startAsync(cmd);
    }

    @Override
    public <U> LaunchedContextWithValue<U> startAsync(ProcedureWithValue<U> func) {
        return parentContext.startAsync(func);
    }

    @Override
    public void runSync(final ProcedureInterface func) {
        parentContext.runSync(func);
    }

    @Override
    public void yield(T valueToYield) {
        valueCallback.accept(valueToYield);
        parentContext.yield();
    }
}
