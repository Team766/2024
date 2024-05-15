package com.team766.framework.conditions;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class RuleEngine {
    private final Set<Subsystem> reservedSubsystems = new HashSet<>();
    private final List<Runnable> startFrameCallbacks = new LinkedList<>();

    void registerStartFrameCallback(Runnable condition) {
        startFrameCallbacks.add(condition);
    }

    void tryScheduling(Supplier<Command> behaviorSupplier) {
        final var behavior = behaviorSupplier.get();
        if (behavior == null) {
            return;
        }
        if (Collections.disjoint(reservedSubsystems, behavior.getRequirements())) {
            reservedSubsystems.addAll(behavior.getRequirements());
            behavior.schedule(); // TODO: don't schedule if already scheduled
        }
    }

    void startFrame() {
        for (var callback : startFrameCallbacks) {
            callback.run();
        }
    }
}
