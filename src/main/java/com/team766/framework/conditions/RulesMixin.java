package com.team766.framework.conditions;

import edu.wpi.first.wpilibj2.command.Command;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RulesMixin implements RuleEngineProvider {
    interface ManagedCondition {
        void invalidate();
    }

    private class ConditionBase extends Condition implements ManagedCondition {
        private boolean valid = false;
        private boolean triggering = false;
        private boolean newlyTriggering = false;
        private boolean finishedTriggering = false;

        protected ConditionBase() {
            engine.registerCondition(this);
        }

        @Override
        public void invalidate() {
            valid = false;
        }

        protected void update(final boolean triggeringNow) {
            if (valid) {
                throw new IllegalStateException("update() called multiple times on this Condition");
            }

            newlyTriggering = !triggering && triggeringNow;
            finishedTriggering = triggering && !triggeringNow;
            triggering = triggeringNow;
            valid = true;
        }

        @Override
        protected final boolean isTriggering() {
            if (!valid) {
                throw new IllegalStateException(
                        "Call update() on this Condition before calling isTriggering()");
            }
            return triggering;
        }

        @Override
        protected final boolean isNewlyTriggering() {
            if (!valid) {
                throw new IllegalStateException(
                        "Call update() on this Condition before calling isNewlyTriggering()");
            }
            return newlyTriggering;
        }

        @Override
        protected final boolean isFinishedTriggering() {
            if (!valid) {
                throw new IllegalStateException(
                        "Call update() on this Condition before calling isFinishedTriggering()");
            }
            return finishedTriggering;
        }

        @Override
        protected RuleEngine getRuleEngine() {
            return engine;
        }
    }

    public class InlineCondition {
        private ConditionBase base = new ConditionBase();

        public InlineCondition() {}

        // Method redefined to make it public.
        public Condition update(boolean triggeringNow) {
            base.update(triggeringNow);
            return base;
        }
    }

    public class DeclaredCondition extends ConditionBase {
        private final BooleanSupplier condition;

        public DeclaredCondition(BooleanSupplier condition) {
            this.condition = condition;
        }

        @Override
        public void invalidate() {
            super.invalidate();

            super.update(condition.getAsBoolean());
        }
    }

    public class ValueCondition<Value> implements ManagedCondition {
        private final Supplier<Value> valueSupplier;
        private Value value;
        private Value prevValue;

        public ValueCondition(Supplier<Value> valueSupplier) {
            this.valueSupplier = valueSupplier;

            engine.registerCondition(this);
        }

        @Override
        public void invalidate() {
            prevValue = value;
            value = valueSupplier.get();
        }

        public Condition condition(Predicate<Value> predicate) {
            return new Condition() {
                private boolean prevTriggering() {
                    return predicate.test(prevValue);
                }

                @Override
                protected boolean isTriggering() {
                    return predicate.test(value);
                }

                @Override
                protected boolean isNewlyTriggering() {
                    return !prevTriggering() && isTriggering();
                }

                @Override
                protected boolean isFinishedTriggering() {
                    return prevTriggering() && !isTriggering();
                }

                @Override
                protected RuleEngine getRuleEngine() {
                    return engine;
                }
            };
        }

        public Condition onEquals(Value value) {
            return condition(value::equals);
        }
    }

    private final RuleEngine engine;
    public final Condition neverCondition;

    protected RulesMixin(RuleEngineProvider engine) {
        this.engine = engine.getRuleEngine();
        neverCondition = this.engine.neverCondition;
    }

    @Override
    public RuleEngine getRuleEngine() {
        return engine;
    }

    protected void byDefault(Command behavior) {
        engine.tryScheduling(behavior);
    }
}
