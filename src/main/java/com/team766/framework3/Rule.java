package com.team766.framework3;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Rule to be evaluated in the {@link RuleEngine}.  Rules contain a
 * "predicate" that will be evaluated in each call to {@link RuleEngine#run}, typically
 * in an OperatorInterface loop or Display (LED lights, etc) loop.  The Rule keeps track of
 * when the predicate starts triggering and has finished triggering, via
 * a {@link TriggerType}, eg when a driver or boxop starts pressing a button and then releases the button.
 * Each Rule has optional {@link Procedure} actions for each of these trigger types, which the
 * {@link RuleEngine} will consider running, after checking if higher priority rules have reserved the
 * same {@link Mechanism}s that the candidate rule would use.
 *
 * {@link Rule}s are always created and used with a {@link RuleEngine}.  Typically creation would be:
 *
 * <pre>
 * {@code
 *   public class MyRules extends RuleEngine {
 *     public MyRules() {
 *       // add rule to spin up the shooter when the boxop presses the right trigger on the gamepad
 *       rules.add("spin up shooter", gamepad.getButton(InputConstants.XBOX_RT),
 *                 () -> new ShooterSpin(shooter)));
 *       ...
 *     }
 * }
 * </pre>
 */
public class Rule {

    /**
     * Rules will be in one of four "trigger" (based on rule predicate) states:
     *
     * NONE - rule is not triggering and was not triggering in the last evaluation.
     * NEWLY - rule just started triggering this evaluation.
     * CONTINUING - rule was triggering in the last evaluation and is still triggering.  Only used internally.
     * FINISHED - rule was triggering in the last evaluation and is no longer triggering.
     *
     */
    enum TriggerType {
        NONE,
        NEWLY,
        CONTINUING,
        FINISHED
    }

    private final String name;
    private final BooleanSupplier predicate;
    private final Map<TriggerType, Supplier<Procedure>> triggerProcedures =
            Maps.newEnumMap(TriggerType.class);
    private final Map<TriggerType, Set<Mechanism<?>>> triggerReservations =
            Maps.newEnumMap(TriggerType.class);

    private TriggerType currentTriggerType = TriggerType.NONE;
    private boolean sealed = false;

    /* package */ Rule(
            String name, BooleanSupplier predicate, Supplier<Procedure> newlyTriggeringProcedure) {
        if (predicate == null) {
            throw new IllegalArgumentException("Rule predicate has not been set.");
        }

        if (newlyTriggeringProcedure == null) {
            throw new IllegalArgumentException("Newly triggering Procedure is not defined.");
        }

        this.name = name;
        this.predicate = predicate;
        if (newlyTriggeringProcedure != null) {
            triggerProcedures.put(TriggerType.NEWLY, newlyTriggeringProcedure);
            triggerReservations.put(
                    TriggerType.NEWLY, getReservationsForProcedure(newlyTriggeringProcedure));
        }
    }

    /** Specify a creator for the Procedure that should be run when this rule was triggering before and is no longer triggering. */
    public Rule withFinishedTriggeringProcedure(Supplier<Procedure> action) {
        if (sealed) {
            throw new IllegalStateException(
                    "Cannot modify rules once they've been evaluated in the RuleEngine");
        }

        triggerProcedures.put(TriggerType.FINISHED, action);
        triggerReservations.put(TriggerType.FINISHED, getReservationsForProcedure(action));
        return this;
    }

    public Rule withFinishedTriggeringProcedure(Set<Mechanism<?>> reservations, Runnable action) {
        return withFinishedTriggeringProcedure(
                () -> new FunctionalInstantProcedure(reservations, action));
    }

    private Set<Mechanism<?>> getReservationsForProcedure(Supplier<Procedure> supplier) {
        if (supplier != null) {
            Procedure procedure = supplier.get();
            if (procedure != null) {
                return procedure.reservations();
            }
        }
        return Collections.emptySet();
    }

    public String getName() {
        return name;
    }

    /* package */ TriggerType getCurrentTriggerType() {
        return currentTriggerType;
    }

    /* package */ void seal() {
        sealed = true;
    }

    /* package */ void reset() {
        currentTriggerType = TriggerType.NONE;
    }

    /* package */ void evaluate() {
        if (predicate.getAsBoolean()) {
            currentTriggerType =
                    switch (currentTriggerType) {
                        case NONE -> TriggerType.NEWLY;
                        case NEWLY -> TriggerType.CONTINUING;
                        case CONTINUING -> TriggerType.CONTINUING;
                        case FINISHED -> TriggerType.NEWLY;
                    };
        } else {
            currentTriggerType =
                    switch (currentTriggerType) {
                        case NONE -> TriggerType.NONE;
                        case NEWLY -> TriggerType.FINISHED;
                        case CONTINUING -> TriggerType.FINISHED;
                        case FINISHED -> TriggerType.NONE;
                    };
        }
    }

    /* package */ Set<Mechanism<?>> getMechanismsToReserve() {
        if (triggerReservations.containsKey(currentTriggerType)) {
            return triggerReservations.get(currentTriggerType);
        }
        return Collections.emptySet();
    }

    /* package */ Procedure getProcedureToRun() {
        if (currentTriggerType != TriggerType.NONE) {
            if (triggerProcedures.containsKey(currentTriggerType)) {
                Supplier<Procedure> supplier = triggerProcedures.get(currentTriggerType);
                if (supplier != null) {
                    return supplier.get();
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[ name: ");
        builder.append(name);
        builder.append(", predicate: ");
        builder.append(predicate);
        builder.append(", currentTriggerType: ");
        builder.append(currentTriggerType);
        builder.append("]");
        return builder.toString();
    }
}
