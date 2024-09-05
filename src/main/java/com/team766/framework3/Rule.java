package com.team766.framework3;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class Rule {

    @FunctionalInterface
    public interface RunnableCreator {
        RunnableWithContext create();
    }

    public static class Builder {
        private BooleanSupplier predicate;
        private RunnableCreator newlyTriggeringRunnable;
        private RunnableCreator continuingTriggeringRunnable;
        private RunnableCreator finishedTriggeringRunnable;

        // TODO: make the newly triggering runnable a ctor argument as well?
        public Builder(BooleanSupplier predicate) {
            this.predicate = predicate;
        }

        public Builder withNewlyTriggeringRunnable(RunnableCreator action) {
            this.newlyTriggeringRunnable = action;
            return this;
        }

        public Builder withContinuingTriggeringRunnable(RunnableCreator action) {
            this.continuingTriggeringRunnable = action;
            return this;
        }

        public Builder withFinishedTriggeringRunnable(RunnableCreator action) {
            this.finishedTriggeringRunnable = action;
            return this;
        }

        /* package */ Rule build() {
            return new Rule(
                    predicate,
                    newlyTriggeringRunnable,
                    continuingTriggeringRunnable,
                    finishedTriggeringRunnable);
        }
    }

    enum TriggerType {
        NONE,
        NEWLY,
        CONTINUING,
        FINISHED
    }

    private final BooleanSupplier predicate;
    private final Map<TriggerType, RunnableCreator> triggerRunnables =
            Maps.newEnumMap(TriggerType.class);
    private final Map<TriggerType, Set<Mechanism<?>>> triggerReservations =
            Maps.newEnumMap(TriggerType.class);

    private TriggerType currentTriggerType = TriggerType.NONE;

    private Rule(
            BooleanSupplier predicate,
            RunnableCreator newlyTriggeringRunnable,
            RunnableCreator continuingTriggeringRunnable,
            RunnableCreator finishedTriggeringRunnable) {

        if (predicate == null) {
            throw new IllegalArgumentException("Rule predicate has not been set.");
        }

        if (newlyTriggeringRunnable == null) {
            throw new IllegalArgumentException("Newly triggering rule is not defined.");
        }

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

    private Set<Mechanism<?>> getReservationsForRunnable(RunnableCreator creator) {
        if (creator != null) {
            RunnableWithContext runnable = creator.create();
            if (runnable != null) {
                return runnable.reservations();
            }
        }
        return Collections.emptySet();
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

    /* package */ Set<Mechanism<?>> getMechanismsToReserve() {
        if (triggerReservations.containsKey(currentTriggerType)) {
            return triggerReservations.get(currentTriggerType);
        }
        return Collections.emptySet();
    }

    /* package */ RunnableWithContext getRunnableToRun() {
        if (currentTriggerType != TriggerType.NONE) {
            if (triggerRunnables.containsKey(currentTriggerType)) {
                RunnableCreator creator = triggerRunnables.get(currentTriggerType);
                if (creator != null) {
                    return creator.create();
                }
            }
        }
        return null;
    }
}
