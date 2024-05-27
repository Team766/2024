package com.team766.framework.resources;

import com.team766.library.function.FunctionBase;
import com.team766.library.function.Reflection;
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
    private static final Map<Class<? extends Subsystem>, Subsystem> subsystems = new HashMap<>();

    public static void addSubsystem(Subsystem subsystem) {
        subsystems.put(subsystem.getClass(), subsystem);
    }

    @SuppressWarnings("unchecked")
    static <T extends Subsystem> T getSubsystem(Class<T> clazz) {
        T subsystem = (T) subsystems.get(clazz);
        if (subsystem == null) {
            throw new IllegalArgumentException(clazz.getName() + " is not a registered Subsystem");
        }
        return subsystem;
    }

    private final List<Runnable> transientEndFrameCallbacks = new ArrayList<>();

    private final Set<Class<? extends Subsystem>> reservedSubsystems = new HashSet<>();

    private final Map<Class<? extends FunctionBase>, Command> prevScheduledCommands =
            new HashMap<>();
    private Map<Class<? extends FunctionBase>, Object> prevActiveRules = new HashMap<>();
    private Map<Class<? extends FunctionBase>, Object> activeRules = new HashMap<>();
    private boolean initializing = true;

    /* package */ void registerTransientEndFrameCallback(Runnable callback) {
        transientEndFrameCallbacks.add(callback);
    }

    @SuppressWarnings("unchecked")
    public static Command makeAutonomus(
            FunctionBase callback, Function<Subsystem[], Command> doCallback) {
        var params = (Class<? extends Subsystem>[]) Reflection.findLambdaParams(callback);
        Subsystem[] subsystems = acquireSubsytems(params);
        return doCallback.apply(subsystems);
    }

    @SuppressWarnings("unchecked")
    /* package */ boolean runIfAvailable(FunctionBase callback, Consumer<Subsystem[]> doCallback) {
        var params = (Class<? extends Subsystem>[]) Reflection.findLambdaParams(callback);
        Subsystem[] subsystems = tryReserve(params);
        if (subsystems == null) {
            return false;
        }
        checkCallback(callback.getClass());
        doCallback.accept(subsystems);
        return true;
    }

    /* package */ void runOnceIfAvailable(FunctionBase callback, Consumer<Subsystem[]> doCallback) {
        runIfAvailable(callback, subsystems -> {
            if (activeRules.put(callback.getClass(), this) != null | initializing) {
                return;
            }
            doCallback.accept(subsystems);
        });
    }

    /* package */ boolean scheduleIfAvailable(
            FunctionBase callback, Function<Subsystem[], Command> doCallback) {
        return runIfAvailable(
                callback,
                subsystems -> scheduleCommand(callback.getClass(), subsystems, doCallback));
    }

    /* package */ void scheduleOnceIfAvailable(
            FunctionBase callback, Function<Subsystem[], Command> doCallback) {
        scheduleIfAvailable(callback, subsystems -> {
            if (activeRules.put(callback.getClass(), this) != null | initializing) {
                return null;
            }
            return doCallback.apply(subsystems);
        });
    }

    private Subsystem[] tryReserve(Class<? extends Subsystem>[] resources) {
        var resourcesList = Arrays.asList(resources);
        if (!Collections.disjoint(reservedSubsystems, resourcesList)) {
            return null;
        }
        reservedSubsystems.addAll(resourcesList);
        return acquireSubsytems(resources);
    }

    private static Subsystem[] acquireSubsytems(Class<? extends Subsystem>[] resources) {
        var subsystems = new Subsystem[resources.length];
        for (int i = 0; i < subsystems.length; ++i) {
            subsystems[i] = getSubsystem(resources[i]);
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
