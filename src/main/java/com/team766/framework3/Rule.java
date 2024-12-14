package com.team766.framework3;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
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
 *       rules.add(Rule.create("spin up shooter", gamepad.getButton(InputConstants.XBOX_RT)).
 *         withOnTriggeringProcedure(ONCE_AND_HOLD, () -> new ShooterSpin(shooter)));
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

    /** Policy for canceling actions when the rule is in a given state. */
    enum Cancellation {
        /** Do not cancel any previous actions. */
        DO_NOT_CANCEL,
        /** Cancel the action previously scheduled when the rule was in the NEWLY state. */
        CANCEL_NEWLY_ACTION,
    }

    /**
     * Simple Builder for {@link Rule}s.  Configure Rules via this Builder; these fields will be immutable
     * in the rule the Builder constructs.
     *
     * Instances of this Builder are created via {@link Rule#create} to simplify syntax.
     */
    public static class Builder {
        private final String name;
        private final BooleanSupplier predicate;
        private Supplier<Procedure> onTriggeringProcedure;
        private Cancellation cancellationOnFinish = Cancellation.DO_NOT_CANCEL;
        private Supplier<Procedure> finishedTriggeringProcedure;
        private final List<Rule.Builder> composedRules = new ArrayList<>();
        private final List<Rule.Builder> negatedComposedRules = new ArrayList<>();

        private Builder(String name, BooleanSupplier predicate) {
            this.name = name;
            this.predicate = predicate;
        }

        private void applyRulePersistence(
                RulePersistence rulePersistence, Supplier<Procedure> action) {
            switch (rulePersistence) {
                case ONCE -> {
                    this.onTriggeringProcedure = action;
                    this.cancellationOnFinish = Cancellation.DO_NOT_CANCEL;
                }
                case ONCE_AND_HOLD -> {
                    this.onTriggeringProcedure =
                            () -> {
                                final Procedure procedure = action.get();
                                return new FunctionalProcedure(
                                        procedure.getName(),
                                        procedure.reservations(),
                                        context -> {
                                            procedure.run(context);
                                            context.waitFor(() -> false);
                                        });
                            };
                    this.cancellationOnFinish = Cancellation.CANCEL_NEWLY_ACTION;
                }
                case REPEATEDLY -> {
                    this.onTriggeringProcedure =
                            () -> {
                                final Procedure procedure = action.get();
                                return new FunctionalProcedure(
                                        procedure.getName(),
                                        procedure.reservations(),
                                        context -> {
                                            Procedure currentProcedure = procedure;
                                            while (true) {
                                                context.runSync(currentProcedure);
                                                context.yield();
                                                currentProcedure = action.get();
                                            }
                                        });
                            };
                    this.cancellationOnFinish = Cancellation.CANCEL_NEWLY_ACTION;
                }
            }
        }

        /** Specify a creator for the Procedure that should be run when this rule starts triggering. */
        public Builder withOnTriggeringProcedure(
                RulePersistence rulePersistence, Supplier<Procedure> action) {
            applyRulePersistence(rulePersistence, action);
            return this;
        }

        /** Specify a creator for the Procedure that should be run when this rule starts triggering. */
        public Builder withOnTriggeringProcedure(
                RulePersistence rulePersistence,
                Set<Mechanism<?, ?>> reservations,
                Runnable action) {
            applyRulePersistence(
                    rulePersistence, () -> new FunctionalInstantProcedure(reservations, action));
            return this;
        }

        /** Specify a creator for the Procedure that should be run when this rule starts triggering. */
        public Builder withOnTriggeringProcedure(
                RulePersistence rulePersistence,
                Set<Mechanism<?, ?>> reservations,
                Consumer<Context> action) {
            applyRulePersistence(
                    rulePersistence, () -> new FunctionalProcedure(reservations, action));
            return this;
        }

        /** Specify a creator for the Procedure that should be run when this rule was triggering before and is no longer triggering. */
        public Builder withFinishedTriggeringProcedure(Supplier<Procedure> action) {
            this.finishedTriggeringProcedure = action;
            return this;
        }

        /** Specify a creator for the Procedure that should be run when this rule was triggering before and is no longer triggering. */
        public Builder withFinishedTriggeringProcedure(
                Set<Mechanism<?, ?>> reservations, Runnable action) {
            this.finishedTriggeringProcedure =
                    () -> new FunctionalInstantProcedure(reservations, action);
            return this;
        }

        /** Specify Rules which should only trigger when this Rule is also triggering. */
        public Builder whenTriggering(Rule.Builder... rules) {
            composedRules.addAll(Arrays.asList(rules));
            return this;
        }

        /** Specify Rules which should only trigger when this Rule is not triggering. */
        public Builder whenNotTriggering(Rule.Builder... rules) {
            negatedComposedRules.addAll(Arrays.asList(rules));
            return this;
        }

        // called by {@link RuleEngine#addRule}.
        /* package */ List<Rule> build() {
            return build(null);
        }

        private List<Rule> build(BooleanSupplier parentPredicate) {
            final var rules = new ArrayList<Rule>();

            final BooleanSupplier fullPredicate =
                    parentPredicate == null
                            ? predicate
                            : () -> parentPredicate.getAsBoolean() && predicate.getAsBoolean();
            final var thisRule =
                    new Rule(
                            name,
                            fullPredicate,
                            onTriggeringProcedure,
                            cancellationOnFinish,
                            finishedTriggeringProcedure);
            rules.add(thisRule);

            // Important! These composed predicates shouldn't invoke `predicate`. `predicate` should
            // only be invoked once per call to RuleEngine.run(), so having all rules in the
            // hierarchy call it would not work as expected. Instead, we have the child rules query
            // the triggering state of the parent rule.
            final BooleanSupplier composedPredicate =
                    parentPredicate == null
                            ? () -> thisRule.isTriggering()
                            : () -> parentPredicate.getAsBoolean() && thisRule.isTriggering();
            final BooleanSupplier negativeComposedPredicate =
                    parentPredicate == null
                            ? () -> !thisRule.isTriggering()
                            : () -> parentPredicate.getAsBoolean() && !thisRule.isTriggering();
            for (var r : composedRules) {
                rules.addAll(r.build(composedPredicate));
            }
            for (var r : negatedComposedRules) {
                rules.addAll(r.build(negativeComposedPredicate));
            }
            return rules;
        }
    }

    private final String name;
    private final BooleanSupplier predicate;
    private final Map<TriggerType, Supplier<Procedure>> triggerProcedures =
            Maps.newEnumMap(TriggerType.class);
    private final Map<TriggerType, Set<Mechanism<?, ?>>> triggerReservations =
            Maps.newEnumMap(TriggerType.class);
    private final Cancellation cancellationOnFinish;

    private TriggerType currentTriggerType = TriggerType.NONE;

    public static Builder create(String name, BooleanSupplier predicate) {
        return new Builder(name, predicate);
    }

    private Rule(
            String name,
            BooleanSupplier predicate,
            Supplier<Procedure> onTriggeringProcedure,
            Cancellation cancellationOnFinish,
            Supplier<Procedure> finishedTriggeringProcedure) {
        if (predicate == null) {
            throw new IllegalArgumentException("Rule predicate has not been set.");
        }

        if (onTriggeringProcedure == null) {
            throw new IllegalArgumentException("On-triggering Procedure is not defined.");
        }

        this.name = name;
        this.predicate = predicate;
        if (onTriggeringProcedure != null) {
            triggerProcedures.put(TriggerType.NEWLY, onTriggeringProcedure);
            triggerReservations.put(
                    TriggerType.NEWLY, getReservationsForProcedure(onTriggeringProcedure));
        }

        this.cancellationOnFinish = cancellationOnFinish;

        if (finishedTriggeringProcedure != null) {
            triggerProcedures.put(TriggerType.FINISHED, finishedTriggeringProcedure);
            triggerReservations.put(
                    TriggerType.FINISHED, getReservationsForProcedure(finishedTriggeringProcedure));
        }
    }

    private static Set<Mechanism<?, ?>> getReservationsForProcedure(Supplier<Procedure> supplier) {
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

    /* package */ boolean isTriggering() {
        return switch (currentTriggerType) {
            case NEWLY -> true;
            case CONTINUING -> true;
            case FINISHED -> false;
            case NONE -> false;
        };
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

    /* package */ Set<Mechanism<?, ?>> getMechanismsToReserve() {
        return triggerReservations.getOrDefault(currentTriggerType, Collections.emptySet());
    }

    /* package */ Cancellation getCancellationOnFinish() {
        return cancellationOnFinish;
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
