package com.team766.framework.conditions;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RuleEngine {
    private final Set<Subsystem> reservedSubsystems = new HashSet<>();
    private final List<RulesMixin.ManagedCondition> conditions =
            new LinkedList<RulesMixin.ManagedCondition>();

    public final Condition neverCondition = new Condition() {
        @Override
        protected boolean isTriggering() {
            return false;
        }

        @Override
        protected boolean isNewlyTriggering() {
            return false;
        }

        @Override
        protected boolean isFinishedTriggering() {
            return false;
        }

        @Override
        protected RuleEngine getRuleEngine() {
            return RuleEngine.this;
        }
    };

    void registerCondition(RulesMixin.ManagedCondition condition) {
        conditions.add(condition);
    }

    void tryScheduling(Command behavior) {
        if (behavior == null) {
            return;
        }
        if (Collections.disjoint(reservedSubsystems, behavior.getRequirements())) {
            reservedSubsystems.addAll(behavior.getRequirements());
            behavior.schedule(); // TODO: don't schedule if already scheduled
        }
    }

    void startFrame() {
        for (var condition : conditions) {
            condition.invalidate();
        }
    }
}
