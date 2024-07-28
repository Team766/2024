/** Generated from the template in ResourcesMixin.java.template */
package com.team766.framework.resources;

import com.team766.library.function.Functions.*;
import com.team766.library.function.Functions.Runnable;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.List;

public interface ResourcesMixin {
    ResourceManager getResourceManager();

    // 0 RobotSystems

    default void whileAvailable(Provider<Command> callback) {
        getResourceManager().scheduleIfAvailable(callback, List.of(), subsystems -> callback.get());
    }

    default void onceAvailable(Provider<Command> callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(callback, List.of(), subsystems -> callback.get());
    }

    default void repeatedly(Runnable callback) {
        callback.run();
    }

    default void once(Runnable callback) {
        getResourceManager().runOnceIfAvailable(callback, List.of(), subsystems -> callback.run());
    }

    // 1 RobotSystems

    @SuppressWarnings("unchecked")
    default <RobotSystem0 extends Subsystem & Reservable> boolean whileAvailable(
            Guarded<RobotSystem0> subsystem0, Function1<RobotSystem0, Command> callback) {
        return getResourceManager()
                .scheduleIfAvailable(callback, List.of(subsystem0), subsystems -> {
                    return callback.apply((RobotSystem0) subsystems[0]);
                });
    }

    default <RobotSystem0 extends Subsystem & Reservable, T> boolean whileAvailable(
            Guarded<RobotSystem0> subsystem0,
            InvalidReturnType.Function1<RobotSystem0, T> callback) {
        return false;
    }

    @SuppressWarnings("unchecked")
    default <RobotSystem0 extends Subsystem & Reservable> void onceAvailable(
            Guarded<RobotSystem0> subsystem0, Function1<RobotSystem0, Command> callback) {
        getResourceManager().scheduleOnceIfAvailable(callback, List.of(subsystem0), subsystems -> {
            return callback.apply((RobotSystem0) subsystems[0]);
        });
    }

    default <RobotSystem0 extends Subsystem & Reservable, T> void onceAvailable(
            Guarded<RobotSystem0> subsystem0,
            InvalidReturnType.Function1<RobotSystem0, T> callback) {}

    @SuppressWarnings("unchecked")
    default <RobotSystem0 extends Subsystem & Reservable> boolean whileAvailable(
            Guarded<RobotSystem0> subsystem0, Consumer1<RobotSystem0> callback) {
        return getResourceManager().runIfAvailable(callback, List.of(subsystem0), subsystems -> {
            callback.accept((RobotSystem0) subsystems[0]);
        });
    }

    @SuppressWarnings("unchecked")
    default <RobotSystem0 extends Subsystem & Reservable> void onceAvailable(
            Guarded<RobotSystem0> subsystem0, Consumer1<RobotSystem0> callback) {
        getResourceManager().runOnceIfAvailable(callback, List.of(subsystem0), subsystems -> {
            callback.accept((RobotSystem0) subsystems[0]);
        });
    }

    default <RobotSystem0 extends Subsystem & Reservable> void byDefault(
            Guarded<RobotSystem0> subsystem0, Function1<RobotSystem0, Command> callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() -> whileAvailable(subsystem0, callback));
    }

    default <RobotSystem0 extends Subsystem & Reservable, T> void byDefault(
            Guarded<RobotSystem0> subsystem0,
            InvalidReturnType.Function1<RobotSystem0, T> callback) {}

    default <RobotSystem0 extends Subsystem & Reservable> void byDefault(
            Guarded<RobotSystem0> subsystem0, Consumer1<RobotSystem0> callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() -> whileAvailable(subsystem0, callback));
    }

    // 2 RobotSystems

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Function2<RobotSystem0, RobotSystem1, Command> callback) {
        return getResourceManager()
                .scheduleIfAvailable(callback, List.of(subsystem0, subsystem1), subsystems -> {
                    return callback.apply(
                            (RobotSystem0) subsystems[0], (RobotSystem1) subsystems[1]);
                });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    T>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    InvalidReturnType.Function2<RobotSystem0, RobotSystem1, T> callback) {
        return false;
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Function2<RobotSystem0, RobotSystem1, Command> callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(callback, List.of(subsystem0, subsystem1), subsystems -> {
                    return callback.apply(
                            (RobotSystem0) subsystems[0], (RobotSystem1) subsystems[1]);
                });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    T>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    InvalidReturnType.Function2<RobotSystem0, RobotSystem1, T> callback) {}

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Consumer2<RobotSystem0, RobotSystem1> callback) {
        return getResourceManager()
                .runIfAvailable(callback, List.of(subsystem0, subsystem1), subsystems -> {
                    callback.accept((RobotSystem0) subsystems[0], (RobotSystem1) subsystems[1]);
                });
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Consumer2<RobotSystem0, RobotSystem1> callback) {
        getResourceManager()
                .runOnceIfAvailable(callback, List.of(subsystem0, subsystem1), subsystems -> {
                    callback.accept((RobotSystem0) subsystems[0], (RobotSystem1) subsystems[1]);
                });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Function2<RobotSystem0, RobotSystem1, Command> callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(
                        () -> whileAvailable(subsystem0, subsystem1, callback));
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    T>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    InvalidReturnType.Function2<RobotSystem0, RobotSystem1, T> callback) {}

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Consumer2<RobotSystem0, RobotSystem1> callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(
                        () -> whileAvailable(subsystem0, subsystem1, callback));
    }

    // 3 RobotSystems

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Function3<RobotSystem0, RobotSystem1, RobotSystem2, Command> callback) {
        return getResourceManager()
                .scheduleIfAvailable(
                        callback, List.of(subsystem0, subsystem1, subsystem2), subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    T>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    InvalidReturnType.Function3<RobotSystem0, RobotSystem1, RobotSystem2, T>
                            callback) {
        return false;
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Function3<RobotSystem0, RobotSystem1, RobotSystem2, Command> callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(
                        callback, List.of(subsystem0, subsystem1, subsystem2), subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    T>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    InvalidReturnType.Function3<RobotSystem0, RobotSystem1, RobotSystem2, T>
                            callback) {}

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Consumer3<RobotSystem0, RobotSystem1, RobotSystem2> callback) {
        return getResourceManager()
                .runIfAvailable(
                        callback, List.of(subsystem0, subsystem1, subsystem2), subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2]);
                        });
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Consumer3<RobotSystem0, RobotSystem1, RobotSystem2> callback) {
        getResourceManager()
                .runOnceIfAvailable(
                        callback, List.of(subsystem0, subsystem1, subsystem2), subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Function3<RobotSystem0, RobotSystem1, RobotSystem2, Command> callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(
                        () -> whileAvailable(subsystem0, subsystem1, subsystem2, callback));
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    T>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    InvalidReturnType.Function3<RobotSystem0, RobotSystem1, RobotSystem2, T>
                            callback) {}

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Consumer3<RobotSystem0, RobotSystem1, RobotSystem2> callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(
                        () -> whileAvailable(subsystem0, subsystem1, subsystem2, callback));
    }

    // 4 RobotSystems

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Function4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, Command>
                            callback) {
        return getResourceManager()
                .scheduleIfAvailable(
                        callback,
                        List.of(subsystem0, subsystem1, subsystem2, subsystem3),
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    T>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    InvalidReturnType.Function4<
                                    RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, T>
                            callback) {
        return false;
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Function4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, Command>
                            callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(
                        callback,
                        List.of(subsystem0, subsystem1, subsystem2, subsystem3),
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    T>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    InvalidReturnType.Function4<
                                    RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, T>
                            callback) {}

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Consumer4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3> callback) {
        return getResourceManager()
                .runIfAvailable(
                        callback,
                        List.of(subsystem0, subsystem1, subsystem2, subsystem3),
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3]);
                        });
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Consumer4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3> callback) {
        getResourceManager()
                .runOnceIfAvailable(
                        callback,
                        List.of(subsystem0, subsystem1, subsystem2, subsystem3),
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Function4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, Command>
                            callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() ->
                        whileAvailable(subsystem0, subsystem1, subsystem2, subsystem3, callback));
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    T>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    InvalidReturnType.Function4<
                                    RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, T>
                            callback) {}

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Consumer4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3> callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() ->
                        whileAvailable(subsystem0, subsystem1, subsystem2, subsystem3, callback));
    }

    // 5 RobotSystems

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Function5<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    Command>
                            callback) {
        return getResourceManager()
                .scheduleIfAvailable(
                        callback,
                        List.of(subsystem0, subsystem1, subsystem2, subsystem3, subsystem4),
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    T>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    InvalidReturnType.Function5<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    T>
                            callback) {
        return false;
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Function5<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    Command>
                            callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(
                        callback,
                        List.of(subsystem0, subsystem1, subsystem2, subsystem3, subsystem4),
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    T>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    InvalidReturnType.Function5<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    T>
                            callback) {}

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Consumer5<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, RobotSystem4>
                            callback) {
        return getResourceManager()
                .runIfAvailable(
                        callback,
                        List.of(subsystem0, subsystem1, subsystem2, subsystem3, subsystem4),
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4]);
                        });
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Consumer5<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, RobotSystem4>
                            callback) {
        getResourceManager()
                .runOnceIfAvailable(
                        callback,
                        List.of(subsystem0, subsystem1, subsystem2, subsystem3, subsystem4),
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Function5<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    Command>
                            callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() -> whileAvailable(
                        subsystem0, subsystem1, subsystem2, subsystem3, subsystem4, callback));
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    T>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    InvalidReturnType.Function5<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    T>
                            callback) {}

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Consumer5<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, RobotSystem4>
                            callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() -> whileAvailable(
                        subsystem0, subsystem1, subsystem2, subsystem3, subsystem4, callback));
    }

    // 6 RobotSystems

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Function6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    Command>
                            callback) {
        return getResourceManager()
                .scheduleIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5),
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    T>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    InvalidReturnType.Function6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    T>
                            callback) {
        return false;
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Function6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    Command>
                            callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5),
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    T>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    InvalidReturnType.Function6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    T>
                            callback) {}

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Consumer6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5>
                            callback) {
        return getResourceManager()
                .runIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5),
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5]);
                        });
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Consumer6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5>
                            callback) {
        getResourceManager()
                .runOnceIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5),
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Function6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    Command>
                            callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() -> whileAvailable(
                        subsystem0,
                        subsystem1,
                        subsystem2,
                        subsystem3,
                        subsystem4,
                        subsystem5,
                        callback));
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    T>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    InvalidReturnType.Function6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    T>
                            callback) {}

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Consumer6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5>
                            callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() -> whileAvailable(
                        subsystem0,
                        subsystem1,
                        subsystem2,
                        subsystem3,
                        subsystem4,
                        subsystem5,
                        callback));
    }

    // 7 RobotSystems

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Function7<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    Command>
                            callback) {
        return getResourceManager()
                .scheduleIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5,
                                subsystem6),
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5],
                                    (RobotSystem6) subsystems[6]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    T>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    InvalidReturnType.Function7<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    T>
                            callback) {
        return false;
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Function7<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    Command>
                            callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5,
                                subsystem6),
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5],
                                    (RobotSystem6) subsystems[6]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    T>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    InvalidReturnType.Function7<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    T>
                            callback) {}

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Consumer7<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6>
                            callback) {
        return getResourceManager()
                .runIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5,
                                subsystem6),
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5],
                                    (RobotSystem6) subsystems[6]);
                        });
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Consumer7<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6>
                            callback) {
        getResourceManager()
                .runOnceIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5,
                                subsystem6),
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5],
                                    (RobotSystem6) subsystems[6]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Function7<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    Command>
                            callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() -> whileAvailable(
                        subsystem0,
                        subsystem1,
                        subsystem2,
                        subsystem3,
                        subsystem4,
                        subsystem5,
                        subsystem6,
                        callback));
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    T>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    InvalidReturnType.Function7<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    T>
                            callback) {}

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Consumer7<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6>
                            callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() -> whileAvailable(
                        subsystem0,
                        subsystem1,
                        subsystem2,
                        subsystem3,
                        subsystem4,
                        subsystem5,
                        subsystem6,
                        callback));
    }

    // 8 RobotSystems

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    RobotSystem7 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Guarded<RobotSystem7> subsystem7,
                    Function8<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    RobotSystem7,
                                    Command>
                            callback) {
        return getResourceManager()
                .scheduleIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5,
                                subsystem6,
                                subsystem7),
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5],
                                    (RobotSystem6) subsystems[6],
                                    (RobotSystem7) subsystems[7]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    RobotSystem7 extends Subsystem & Reservable,
                    T>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Guarded<RobotSystem7> subsystem7,
                    InvalidReturnType.Function8<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    RobotSystem7,
                                    T>
                            callback) {
        return false;
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    RobotSystem7 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Guarded<RobotSystem7> subsystem7,
                    Function8<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    RobotSystem7,
                                    Command>
                            callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5,
                                subsystem6,
                                subsystem7),
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5],
                                    (RobotSystem6) subsystems[6],
                                    (RobotSystem7) subsystems[7]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    RobotSystem7 extends Subsystem & Reservable,
                    T>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Guarded<RobotSystem7> subsystem7,
                    InvalidReturnType.Function8<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    RobotSystem7,
                                    T>
                            callback) {}

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    RobotSystem7 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Guarded<RobotSystem7> subsystem7,
                    Consumer8<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    RobotSystem7>
                            callback) {
        return getResourceManager()
                .runIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5,
                                subsystem6,
                                subsystem7),
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5],
                                    (RobotSystem6) subsystems[6],
                                    (RobotSystem7) subsystems[7]);
                        });
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    RobotSystem7 extends Subsystem & Reservable>
            void onceAvailable(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Guarded<RobotSystem7> subsystem7,
                    Consumer8<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    RobotSystem7>
                            callback) {
        getResourceManager()
                .runOnceIfAvailable(
                        callback,
                        List.of(
                                subsystem0,
                                subsystem1,
                                subsystem2,
                                subsystem3,
                                subsystem4,
                                subsystem5,
                                subsystem6,
                                subsystem7),
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0],
                                    (RobotSystem1) subsystems[1],
                                    (RobotSystem2) subsystems[2],
                                    (RobotSystem3) subsystems[3],
                                    (RobotSystem4) subsystems[4],
                                    (RobotSystem5) subsystems[5],
                                    (RobotSystem6) subsystems[6],
                                    (RobotSystem7) subsystems[7]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    RobotSystem7 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Guarded<RobotSystem7> subsystem7,
                    Function8<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    RobotSystem7,
                                    Command>
                            callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() -> whileAvailable(
                        subsystem0,
                        subsystem1,
                        subsystem2,
                        subsystem3,
                        subsystem4,
                        subsystem5,
                        subsystem6,
                        subsystem7,
                        callback));
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    RobotSystem7 extends Subsystem & Reservable,
                    T>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Guarded<RobotSystem7> subsystem7,
                    InvalidReturnType.Function8<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    RobotSystem7,
                                    T>
                            callback) {}

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    RobotSystem5 extends Subsystem & Reservable,
                    RobotSystem6 extends Subsystem & Reservable,
                    RobotSystem7 extends Subsystem & Reservable>
            void byDefault(
                    Guarded<RobotSystem0> subsystem0,
                    Guarded<RobotSystem1> subsystem1,
                    Guarded<RobotSystem2> subsystem2,
                    Guarded<RobotSystem3> subsystem3,
                    Guarded<RobotSystem4> subsystem4,
                    Guarded<RobotSystem5> subsystem5,
                    Guarded<RobotSystem6> subsystem6,
                    Guarded<RobotSystem7> subsystem7,
                    Consumer8<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6,
                                    RobotSystem7>
                            callback) {
        getResourceManager()
                .registerTransientEndFrameCallback(() -> whileAvailable(
                        subsystem0,
                        subsystem1,
                        subsystem2,
                        subsystem3,
                        subsystem4,
                        subsystem5,
                        subsystem6,
                        subsystem7,
                        callback));
    }
}
