package com.team766.framework.conditions;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RulesMixin implements RuleEngineProvider {
    private class ConditionBase implements Condition {
        private boolean valid = false;
        private Boolean triggering = null;
        private ConditionState state = ConditionState.IsNotTriggering;

        protected ConditionBase() {
            engine.registerStartFrameCallback(this::invalidate);
        }

        protected void invalidate() {
            valid = false;
        }

        protected void update(final boolean triggeringNow) {
            if (valid) {
                throw new IllegalStateException("update() called multiple times on this Condition");
            }

            if (triggering == null) {
                triggering = triggeringNow;
            }
            state = ConditionState.make(triggering, triggeringNow);
            triggering = triggeringNow;
            valid = true;
        }

        @Override
        public final ConditionState getState() {
            if (!valid) {
                throw new IllegalStateException(
                        "Call update() on this Condition before calling isTriggering()");
            }
            return state;
        }
    }

    public final class InlineCondition {
        private ConditionBase base = new ConditionBase();

        public InlineCondition() {}

        public Condition update(boolean triggeringNow) {
            base.update(triggeringNow);
            return base;
        }
    }

    public final class DeclaredCondition extends ConditionBase {
        private final BooleanSupplier condition;

        public DeclaredCondition(BooleanSupplier condition) {
            this.condition = condition;
            invalidate();
        }

        @Override
        protected void invalidate() {
            super.invalidate();

            super.update(condition.getAsBoolean());
        }
    }

    public final class ValueCondition<Value> {
        private final Supplier<Value> valueSupplier;
        private Value value;
        private Value prevValue;

        public ValueCondition(Supplier<Value> valueSupplier) {
            this.valueSupplier = valueSupplier;
            prevValue = value = valueSupplier.get();

            engine.registerStartFrameCallback(this::invalidate);
        }

        protected void invalidate() {
            prevValue = value;
            value = valueSupplier.get();
        }

        public ConditionState condition(Predicate<Value> predicate) {
            return ConditionState.make(predicate.test(prevValue), predicate.test(value));
        }

        public ConditionState onEquals(Value value) {
            return condition(value::equals);
        }

        public Value value() {
            return value;
        }
    }

    private final RuleEngine engine;

    protected RulesMixin(RuleEngineProvider engine) {
        this.engine = engine.getRuleEngine();
    }

    @Override
    public final RuleEngine getRuleEngine() {
        return engine;
    }

    protected final boolean tryRunning(CommandSupplier behavior) {
        return engine.tryScheduling(behavior);
    }

    protected final boolean tryRunning(ReservingRunnable callback) {
        try {
            callback.run();
            return true;
        } catch (ResourceUnavailableException e) {
            return false;
        }
    }

    protected final boolean tryRunning(InvalidReturnType<?> callback) {
        return false;
    }

    protected final void byDefault(CommandSupplier behavior) {
        engine.registerTransientEndFrameCallback(() -> tryRunning(behavior));
    }

    protected final void byDefault(ReservingRunnable callback) {
        engine.registerTransientEndFrameCallback(() -> tryRunning(callback));
    }

    protected final void byDefault(InvalidReturnType<?> callback) {}

    protected final <SubsystemT extends edu.wpi.first.wpilibj2.command.Subsystem>
            SubsystemT reserve(Guarded<SubsystemT> subsystem) throws ResourceUnavailableException {
        if (subsystem.engine != getRuleEngine()) {
            throw new IllegalArgumentException();
        }
        return reserve(subsystem.value);
    }

    protected final <SubsystemT extends edu.wpi.first.wpilibj2.command.Subsystem>
            SubsystemT reserve(SubsystemT subsystem) throws ResourceUnavailableException {
        getRuleEngine().reserveSubsystem(subsystem);
        return subsystem;
    }

    protected final <SubsystemT extends edu.wpi.first.wpilibj2.command.Subsystem>
            boolean tryReserving(Guarded<SubsystemT> subsystem, Consumer<SubsystemT> callback) {
        if (subsystem.engine != getRuleEngine()) {
            throw new IllegalArgumentException();
        }
        return tryReserving(subsystem.value, callback);
    }

    protected final <SubsystemT extends edu.wpi.first.wpilibj2.command.Subsystem>
            boolean tryReserving(SubsystemT subsystem, Consumer<SubsystemT> callback) {
        try {
            callback.accept(reserve(subsystem));
            return true;
        } catch (ResourceUnavailableException e) {
            return false;
        }
    }

    protected final <SubsystemT extends edu.wpi.first.wpilibj2.command.Subsystem>
            Guarded<SubsystemT> guard(SubsystemT subsystem) {
        return new Guarded<>(subsystem, getRuleEngine());
    }

    @FunctionalInterface
    private interface InvalidReturnType<T> {
        T get() throws ResourceUnavailableException;
    }
}
