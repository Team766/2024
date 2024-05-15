package com.team766.framework.conditions;

import edu.wpi.first.wpilibj2.command.Command;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RulesMixin implements RuleEngineProvider {

    protected abstract class Condition {
        public Condition(boolean isTriggering) {
            if (!this.getClass().isAnonymousClass()) {
                throw new IllegalCallerException("Condition classes should be anonymous");
            }

            final Boolean prevTriggering = prevConditions.get(this.getClass());

            final var putResult = conditions.put(this.getClass(), isTriggering);
            if (putResult != null) {
                throw new IllegalStateException(
                        "A single callback object was used in two different conditions. This is not supported.");
            }

            if (prevTriggering != null && !prevTriggering && isTriggering) {
                ifNewlyTriggering();
            }

            if (prevTriggering != null && prevTriggering && !isTriggering) {
                ifFinishedTriggering();
            }

            if (isTriggering) {
                ifTriggering();
            }

            if (!isTriggering) {
                ifNotTriggering();
            }
        }

        protected void runIfAvailable(Supplier<Command> command) {
            engine.tryScheduling(command);
        }

        /// This prevents calling byDefault from inside a Condition.
        /// Condition code should select behaviors using runIfAvailable instead.
        protected void byDefault(Use_runIfAvailable_instead __) {}

        protected void ifNewlyTriggering() {}

        protected void ifFinishedTriggering() {}

        protected void ifTriggering() {}

        protected void ifNotTriggering() {}
    }

    private final RuleEngine engine;
    private Map<Class<? extends Condition>, Boolean> prevConditions = new HashMap<>();
    private Map<Class<? extends Condition>, Boolean> conditions = new HashMap<>();

    protected RulesMixin(RuleEngineProvider engine) {
        this.engine = engine.getRuleEngine();
        this.engine.registerStartFrameCallback(() -> {
            var temp = prevConditions;
            prevConditions = conditions;
            conditions = temp;
            conditions.clear();
        });
    }

    @Override
    public RuleEngine getRuleEngine() {
        return engine;
    }

    protected void byDefault(Supplier<Command> command) {
        engine.tryScheduling(command);
    }
}

class Use_runIfAvailable_instead {
    private Use_runIfAvailable_instead() {}
}
