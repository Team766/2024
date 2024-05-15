package com.team766.framework.conditions;

import edu.wpi.first.wpilibj2.command.Command;
import java.util.function.Supplier;

public abstract class Condition {
    protected abstract boolean isTriggering();

    protected abstract boolean isNewlyTriggering();

    protected abstract boolean isFinishedTriggering();

    protected abstract RuleEngine getRuleEngine();

    public Condition ifNewlyTriggering(Runnable callback) {
        if (isNewlyTriggering()) {
            callback.run();
        }
        return this;
    }

    public Condition ifNewlyTriggering(Supplier<Command> callback) {
        return ifNewlyTriggering(
                () -> {
                    getRuleEngine().tryScheduling(callback.get());
                });
    }

    public Condition ifFinishedTriggering(Runnable callback) {
        if (isFinishedTriggering()) {
            callback.run();
        }
        return this;
    }

    public Condition ifFinishedTriggering(Supplier<Command> callback) {
        return ifFinishedTriggering(
                () -> {
                    getRuleEngine().tryScheduling(callback.get());
                });
    }

    public Condition whileTriggering(Runnable callback) {
        if (isTriggering()) {
            callback.run();
        }
        return this;
    }

    public Condition whileTriggering(Supplier<Command> callback) {
        return whileTriggering(
                () -> {
                    getRuleEngine().tryScheduling(callback.get());
                });
    }

    public Condition whileNotTriggering(Runnable callback) {
        if (!isTriggering()) {
            callback.run();
        }
        return this;
    }

    public Condition whileNotTriggering(Supplier<Command> callback) {
        return whileNotTriggering(
                () -> {
                    getRuleEngine().tryScheduling(callback.get());
                });
    }
}
