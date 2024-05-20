package com.team766.framework.resources;

import java.util.function.Consumer;

public interface ResourcesMixin extends ResourceManagerProvider {
    default boolean tryRunning(CommandSupplier behavior) {
        return getResourceManager().tryScheduling(behavior);
    }

    default boolean tryRunning(ReservingRunnable callback) {
        try {
            callback.run();
            return true;
        } catch (ResourceUnavailableException e) {
            return false;
        }
    }

    default boolean tryRunning(InvalidReturnType<?> callback) {
        return false;
    }

    default void byDefault(CommandSupplier behavior) {
        getResourceManager().registerTransientEndFrameCallback(() -> tryRunning(behavior));
    }

    default void byDefault(ReservingRunnable callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> tryRunning(callback));
    }

    default void byDefault(InvalidReturnType<?> callback) {}

    default <SubsystemT extends edu.wpi.first.wpilibj2.command.Subsystem> SubsystemT reserve(
            Guarded<SubsystemT> subsystem) throws ResourceUnavailableException {
        if (subsystem.manager != getResourceManager()) {
            throw new IllegalArgumentException();
        }
        return reserve(subsystem.value);
    }

    default <SubsystemT extends edu.wpi.first.wpilibj2.command.Subsystem> SubsystemT reserve(
            SubsystemT subsystem) throws ResourceUnavailableException {
        getResourceManager().reserveSubsystem(subsystem);
        return subsystem;
    }

    default <SubsystemT extends edu.wpi.first.wpilibj2.command.Subsystem> boolean tryReserving(
            Guarded<SubsystemT> subsystem, Consumer<SubsystemT> callback) {
        if (subsystem.manager != getResourceManager()) {
            throw new IllegalArgumentException();
        }
        return tryReserving(subsystem.value, callback);
    }

    default <SubsystemT extends edu.wpi.first.wpilibj2.command.Subsystem> boolean tryReserving(
            SubsystemT subsystem, Consumer<SubsystemT> callback) {
        try {
            callback.accept(reserve(subsystem));
            return true;
        } catch (ResourceUnavailableException e) {
            return false;
        }
    }

    default <SubsystemT extends edu.wpi.first.wpilibj2.command.Subsystem> Guarded<SubsystemT> guard(
            SubsystemT subsystem) {
        return new Guarded<>(subsystem, getResourceManager());
    }
}

@FunctionalInterface
/* package private */ interface InvalidReturnType<T> {
    T get() throws ResourceUnavailableException;
}
