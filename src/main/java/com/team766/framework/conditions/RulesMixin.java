package com.team766.framework.conditions;

import edu.wpi.first.wpilibj2.command.Command;
import java.util.HashMap;
import java.util.function.Supplier;

public class RulesMixin implements RuleEngineProvider {

    public class Condition {
        private final boolean triggeringNow;

        public Condition(boolean triggeringNow) {
            this.triggeringNow = triggeringNow;
        }

        private boolean prevTriggering(Class<?> handle) {
            return prevConditions.getOrDefault(handle, false);
        }

        private boolean isTriggering(Class<?> handle) {
            var result = conditions.put(handle, triggeringNow);
            if (result != null) {
                throw new IllegalStateException(
                        "A single callback object was used in two different conditions. This is not supported.");
            }
            return triggeringNow;
        }

        private boolean isNewlyTriggering(Class<?> handle) {
            return !prevTriggering(handle) && isTriggering(handle);
        }

        private boolean isFinishedTriggering(Class<?> handle) {
            return prevTriggering(handle) && !isTriggering(handle);
        }

        public Condition isNewlyTriggering(Runnable callback) {
            if (isNewlyTriggering(callback.getClass())) {
                callback.run();
            }
            return this;
        }

        public Condition isFinishedTriggering(Runnable callback) {
            if (isFinishedTriggering(callback.getClass())) {
                callback.run();
            }
            return this;
        }

        public Condition isTriggering(Runnable callback) {
            if (isTriggering(callback.getClass())) {
                callback.run();
            }
            return this;
        }

        public Condition isNotTriggering(Runnable callback) {
            if (!isTriggering(callback.getClass())) {
                callback.run();
            }
            return this;
        }
    }

    private final RuleEngine engine;
    private HashMap<Class<?>, Boolean> prevConditions = new HashMap<>();
    private HashMap<Class<?>, Boolean> conditions = new HashMap<>();

    protected RulesMixin(RuleEngineProvider engine) {
        this.engine = engine.getRuleEngine();
        this.engine.registerStartFrameCallback(() -> {
            prevConditions = conditions;
            conditions = new HashMap<>();
        });
    }

    public Condition when(boolean triggeringNow) {
        return new Condition(triggeringNow);
    }

    @Override
    public RuleEngine getRuleEngine() {
        return engine;
    }

    protected void runIfAvailable(Supplier<Command> behavior) {
        engine.tryScheduling(behavior);
    }

    protected void byDefault(Supplier<Command> behavior) {
        engine.tryScheduling(behavior);
    }
}
