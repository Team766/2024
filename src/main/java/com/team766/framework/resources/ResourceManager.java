package com.team766.framework.resources;

import com.team766.library.function.FunctionBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ResourceManager {
    private final List<Runnable> transientEndFrameCallbacks = new ArrayList<>();

    private final Set<Subsystem> reservedSubsystems = new HashSet<>();

    private final Map<Class<? extends FunctionBase>, Command> prevScheduledCommands =
            new HashMap<>();
    private Map<Class<? extends FunctionBase>, Object> prevActiveRules = new HashMap<>();
    private Map<Class<? extends FunctionBase>, Object> activeRules = new HashMap<>();
    private boolean initializing = true;

    /* package */ void registerTransientEndFrameCallback(Runnable callback) {
        transientEndFrameCallbacks.add(callback);
    }

    public static Command makeAutonomous(
            List<Guarded<? extends Subsystem>> resources,
            Function<Subsystem[], Command> doCallback) {
        Subsystem[] subsystems = acquireSubsytems(resources);
        return doCallback.apply(subsystems);
    }

    /* package */ boolean runIfAvailable(
            FunctionBase callback,
            List<Guarded<? extends Subsystem>> resources,
            Consumer<Subsystem[]> doCallback) {
        Subsystem[] subsystems = tryReserve(resources);
        if (subsystems == null) {
            return false;
        }
        checkCallback(callback.getClass());
        doCallback.accept(subsystems);
        return true;
    }

    /* package */ void runOnceIfAvailable(
            FunctionBase callback,
            List<Guarded<? extends Subsystem>> resources,
            Consumer<Subsystem[]> doCallback) {
        runIfAvailable(callback, resources, subsystems -> {
            if (activeRules.put(callback.getClass(), this) != null | initializing) {
                return;
            }
            doCallback.accept(subsystems);
        });
    }

    /* package */ boolean scheduleIfAvailable(
            FunctionBase callback,
            List<Guarded<? extends Subsystem>> resources,
            Function<Subsystem[], Command> doCallback) {
        return runIfAvailable(
                callback,
                resources,
                subsystems -> scheduleCommand(callback.getClass(), subsystems, doCallback));
    }

    /* package */ void scheduleOnceIfAvailable(
            FunctionBase callback,
            List<Guarded<? extends Subsystem>> resources,
            Function<Subsystem[], Command> doCallback) {
        scheduleIfAvailable(callback, resources, subsystems -> {
            if (activeRules.put(callback.getClass(), this) != null | initializing) {
                return null;
            }
            return doCallback.apply(subsystems);
        });
    }

    private Subsystem[] tryReserve(List<Guarded<? extends Subsystem>> resources) {
        if (!Collections.disjoint(reservedSubsystems, resources)) {
            return null;
        }
        var subsystems = acquireSubsytems(resources);
        reservedSubsystems.addAll(Arrays.asList(subsystems));
        return subsystems;
    }

    private static Subsystem[] acquireSubsytems(List<Guarded<? extends Subsystem>> resources) {
        var subsystems = new Subsystem[resources.size()];
        for (int i = 0; i < subsystems.length; ++i) {
            subsystems[i] = resources.get(i).get();
            Command prevOwner = CommandScheduler.getInstance().requiring(subsystems[i]);
            if (prevOwner != null) {
                prevOwner.cancel();
            }
        }
        return subsystems;
    }

    private boolean scheduleCommand(
            Class<? extends FunctionBase> handle,
            Subsystem[] subsystems,
            Function<Subsystem[], Command> commandSupplier) {
        Command command = prevScheduledCommands.get(handle);
        if (command == null || command.isFinished()) {
            command = commandSupplier.apply(subsystems);
            if (command == null) {
                return false;
            }
            command.addRequirements(subsystems);
            command.schedule();
        }
        prevScheduledCommands.put(handle, command);
        return true;
    }

    private void checkCallback(Class<? extends FunctionBase> handle) {
        if (!handle.isAnonymousClass()) {
            throw new IllegalArgumentException(
                    "Argument to whileAvailable/onceAvailable should be a lambda");
        }
        if (activeRules.containsKey(handle)) {
            throw new IllegalArgumentException(
                    "A single action runnable type was used in two different conditions. This is not supported.");
        }
        activeRules.put(handle, prevActiveRules.get(handle));
    }

    public void startFrame() {
        var temp = prevActiveRules;
        prevActiveRules = activeRules;
        activeRules = temp;
        activeRules.clear();

        reservedSubsystems.clear();
    }

    public void endFrame() {
        for (var callback : transientEndFrameCallbacks) {
            callback.run();
        }
        transientEndFrameCallbacks.clear();
        initializing = false;
    }
}
