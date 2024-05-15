package com.team766.framework.conditions;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class RuleEngine {
    private final Set<Subsystem> reservedSubsystems = new HashSet<>();
    private final List<Runnable> startFrameCallbacks = new LinkedList<>();
    private Map<Class<?>, Command> prevScheduledCommands = new HashMap<>();
    private Map<Class<?>, Command> scheduledCommands = new HashMap<>();

    void registerStartFrameCallback(Runnable condition) {
        startFrameCallbacks.add(condition);
    }

    void tryScheduling(Supplier<Command> commandSupplier) {
        if (scheduledCommands.containsKey(commandSupplier.getClass())) {
            throw new IllegalStateException(
                    "A single command supplier object was used in two different conditions. This is not supported.");
        }
        Command command = prevScheduledCommands.get(commandSupplier.getClass());
        if (command == null || command.isFinished()) {
            command = commandSupplier.get();
            if (command == null) {
                return;
            }
            if (!Collections.disjoint(reservedSubsystems, command.getRequirements())) {
                return;
            }
            command.schedule();
        }
        reservedSubsystems.addAll(command.getRequirements());
        scheduledCommands.put(commandSupplier.getClass(), command);
    }

    void startFrame() {
        var temp = prevScheduledCommands;
        prevScheduledCommands = scheduledCommands;
        scheduledCommands = temp;
        scheduledCommands.clear();

        reservedSubsystems.clear();

        for (var callback : startFrameCallbacks) {
            callback.run();
        }
    }
}
