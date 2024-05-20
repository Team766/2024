package com.team766.framework.resources;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ResourceManager {
    private final List<Runnable> transientEndFrameCallbacks = new ArrayList<>();
    private final Set<Subsystem> reservedSubsystems = new HashSet<>();
    private final Set<Subsystem> tempClaimedSubsystems = new HashSet<>();
    private Set<Subsystem> claimedSubsystems = reservedSubsystems;
    private final Map<Class<?>, Command> prevScheduledCommands = new HashMap<>();
    private final Set<Class<?>> scheduledCommands = new HashSet<>();

    /* package */ void registerTransientEndFrameCallback(Runnable callback) {
        transientEndFrameCallbacks.add(callback);
    }

    /* package */ void reserveSubsystem(Subsystem subsystem) throws ResourceUnavailableException {
        if (reservedSubsystems.contains(subsystem)) {
            throw new ResourceUnavailableException(subsystem.getName() + " is already reserved");
        }
        claimedSubsystems.add(subsystem);
        Command prevOwner = CommandScheduler.getInstance().requiring(subsystem);
        if (prevOwner != null) {
            prevOwner.cancel();
        }
    }

    /* package */ boolean tryScheduling(CommandSupplier commandSupplier) {
        if (scheduledCommands.contains(commandSupplier.getClass())) {
            throw new IllegalStateException(
                    "A single command supplier object was used in two different conditions. This is not supported.");
        }
        Command command = prevScheduledCommands.get(commandSupplier.getClass());
        if (command == null || command.isFinished()) {
            tempClaimedSubsystems.clear();
            claimedSubsystems = tempClaimedSubsystems;
            try {
                command = commandSupplier.get();
            } catch (ResourceUnavailableException ex) {
                return false;
            } finally {
                claimedSubsystems = reservedSubsystems;
            }
            if (command == null) {
                return false;
            }
            command.addRequirements(tempClaimedSubsystems.toArray(Subsystem[]::new));
            if (!Collections.disjoint(reservedSubsystems, command.getRequirements())) {
                return false;
            }
            command.schedule();
        }
        reservedSubsystems.addAll(command.getRequirements());
        prevScheduledCommands.put(commandSupplier.getClass(), command);
        scheduledCommands.add(commandSupplier.getClass());
        return true;
    }

    public void startFrame() {
        scheduledCommands.clear();

        reservedSubsystems.clear();
    }

    public void endFrame() {
        for (var callback : transientEndFrameCallbacks) {
            callback.run();
        }
        transientEndFrameCallbacks.clear();
    }
}
