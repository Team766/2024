package com.team766.framework3;

import com.google.common.collect.Maps;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * Rule to be evaluated in the {@link RuleEngine}.  Rules contain a
 * "predicate" that will be evaluated in each call to {@link RuleEngine#run}, typically
 * in an OperatorInterface loop or Display (LED lights, etc) loop.  The Rule keeps track of
 * when the predicate starts triggering, continues triggering, and has finished triggering, via
 * a {@link TriggerType}, eg when a driver or boxop starts pressing a button, continues holding down
 * the button, and releases the button.  Each Rule has optional {@link RunnableWithContext} actions
 * for each of these trigger types, which the {@link RuleEngine} will consider running, after checking
 * if higher priority rules have reserved the same {@link Mechanism}s that the candidate rule would use.
 *
 * {@link Rule}s are always created and used with a {@link RuleEngine}.  Typically creation would be:
 *
 * <pre>
 * {@code
 *   public class MyRules extends RuleEngine {
 *     public MyRules() {
 *       // add rule to spin up the shooter when the boxop presses the right trigger on the gamepad
 *       rules.add(Rule.create("spin up shooter", gamepad.getButton(InputConstants.XBOX_RT)).
 *         withNewlyTriggeringRunnable(() -> new ShooterSpin(shooter)));
 *       ...
 *     }
 * }
 * </pre>
 */
public class Rule {

    /**
     * Functional interface for creating new {@link Command}s.
     */
    @FunctionalInterface
    private interface CommandCreator {
        Command createCommand();
    }

    /**
     * Functional interface for creating new {@link RunnableWithContext}s.
     */
    @FunctionalInterface
    public interface RunnableWithContextCreator extends CommandCreator {
        RunnableWithContext createRunnable();

        @Override
        default Command createCommand() {
            return new ContextImpl(createRunnable());
        }
    }

    /**
     * Functional interface for creating new {@link InstantRunnable}s.
     */
    @FunctionalInterface
    public interface InstantRunnableCreator extends CommandCreator {
        InstantRunnable createRunnable();

        @Override
        default Command createCommand() {
            return new InstantCommand(createRunnable());
        }
    }

    /**
     * Rules will be in one of four "trigger" (based on rule predicate) states:
     *
     * NONE - rule is not triggering and was not triggering in the last evaluation.
     * NEWLY - rule just started triggering this evaluation.
     * CONTINUING - rule was triggering in the last evaluation and is still triggering.
     * FINISHED - rule was triggering in the last evaluation and is no longer triggering.
     *
     */
    enum TriggerType {
        NONE,
        NEWLY,
        CONTINUING,
        FINISHED
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
        private CommandCreator newlyTriggeringRunnable;
        private CommandCreator continuingTriggeringRunnable;
        private CommandCreator finishedTriggeringRunnable;

        private Builder(String name, BooleanSupplier predicate) {
            this.name = name;
            this.predicate = predicate;
        }

        /** Specify a creator for the Command that should be run when this rule starts triggering. */
        public Builder withNewlyTriggeringRunnable(RunnableWithContextCreator action) {
            this.newlyTriggeringRunnable = action;
            return this;
        }

        public Builder withNewlyTriggeringRunnable(InstantRunnableCreator action) {
            this.newlyTriggeringRunnable = action;
            return this;
        }

        public Builder withNewlyTriggeringRunnable(Set<Subsystem> reservations, Runnable action) {
            this.newlyTriggeringRunnable = () -> new InstantCommand(reservations, action);
            return this;
        }

        /** Specify a creator for the Command that should be run when this rule was triggering before and is continuing to trigger. */
        public Builder withContinuingTriggeringRunnable(RunnableWithContextCreator action) {
            this.continuingTriggeringRunnable = action;
            return this;
        }

        public Builder withContinuingTriggeringRunnable(InstantRunnableCreator action) {
            this.continuingTriggeringRunnable = action;
            return this;
        }

        public Builder withContinuingTriggeringRunnable(
                Set<Subsystem> reservations, Runnable action) {
            this.continuingTriggeringRunnable = () -> new InstantCommand(reservations, action);
            return this;
        }

        /** Specify a creator for the Command that should be run when this rule was triggering before and is no longer triggering. */
        public Builder withFinishedTriggeringRunnable(RunnableWithContextCreator action) {
            this.finishedTriggeringRunnable = action;
            return this;
        }

        public Builder withFinishedTriggeringRunnable(InstantRunnableCreator action) {
            this.finishedTriggeringRunnable = action;
            return this;
        }

        public Builder withFinishedTriggeringRunnable(
                Set<Subsystem> reservations, Runnable action) {
            this.finishedTriggeringRunnable = () -> new InstantCommand(reservations, action);
            return this;
        }

        // called by {@link RuleEngine#addRule}.
        /* package */ Rule build() {
            return new Rule(
                    name,
                    predicate,
                    newlyTriggeringRunnable,
                    continuingTriggeringRunnable,
                    finishedTriggeringRunnable);
        }
    }

    private final String name;
    private final BooleanSupplier predicate;
    private final Map<TriggerType, CommandCreator> triggerRunnables =
            Maps.newEnumMap(TriggerType.class);
    private final Map<TriggerType, Set<Subsystem>> triggerReservations =
            Maps.newEnumMap(TriggerType.class);

    private TriggerType currentTriggerType = TriggerType.NONE;

    public static Builder create(String name, BooleanSupplier predicate) {
        return new Builder(name, predicate);
    }

    private Rule(
            String name,
            BooleanSupplier predicate,
            CommandCreator newlyTriggeringRunnable,
            CommandCreator continuingTriggeringRunnable,
            CommandCreator finishedTriggeringRunnable) {
        if (predicate == null) {
            throw new IllegalArgumentException("Rule predicate has not been set.");
        }

        if (newlyTriggeringRunnable == null) {
            throw new IllegalArgumentException("Newly triggering rule is not defined.");
        }

        this.name = name;
        this.predicate = predicate;
        if (newlyTriggeringRunnable != null) {
            triggerRunnables.put(TriggerType.NEWLY, newlyTriggeringRunnable);
            triggerReservations.put(
                    TriggerType.NEWLY, getReservationsForRunnable(finishedTriggeringRunnable));
        }

        if (continuingTriggeringRunnable != null) {
            triggerRunnables.put(TriggerType.CONTINUING, continuingTriggeringRunnable);
            triggerReservations.put(
                    TriggerType.CONTINUING,
                    getReservationsForRunnable(continuingTriggeringRunnable));
        }

        if (finishedTriggeringRunnable != null) {
            triggerRunnables.put(TriggerType.FINISHED, finishedTriggeringRunnable);
            triggerReservations.put(
                    TriggerType.FINISHED, getReservationsForRunnable(finishedTriggeringRunnable));
        }
    }

    private Set<Subsystem> getReservationsForRunnable(CommandCreator creator) {
        if (creator != null) {
            Command runnable = creator.createCommand();
            if (runnable != null) {
                return runnable.getRequirements();
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

    /* package */ Set<Subsystem> getMechanismsToReserve() {
        if (triggerReservations.containsKey(currentTriggerType)) {
            return triggerReservations.get(currentTriggerType);
        }
        return Collections.emptySet();
    }

    /* package */ Command getRunnableToRun() {
        if (currentTriggerType != TriggerType.NONE) {
            if (triggerRunnables.containsKey(currentTriggerType)) {
                CommandCreator creator = triggerRunnables.get(currentTriggerType);
                if (creator != null) {
                    return creator.createCommand();
                }
            }
        }
        return null;
    }
}
