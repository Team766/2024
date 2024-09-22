/** Generated from the template in ResourcesMixin.java.template */
package com.team766.framework.resources;

import com.team766.library.function.Functions.*;
import com.team766.library.function.Functions.Runnable;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface ResourcesMixin {
    ResourceManager getResourceManager();

    // 0 RobotSystems

    default void whileAvailable(Provider<Command> callback) {
        getResourceManager().scheduleIfAvailable(callback, subsystems -> callback.get());
    }

    default void onceAvailable(Provider<Command> callback) {
        getResourceManager().scheduleOnceIfAvailable(callback, subsystems -> callback.get());
    }

    default void repeatedly(Runnable callback) {
        callback.run();
    }

    default void once(Runnable callback) {
        getResourceManager().runOnceIfAvailable(callback, subsystems -> callback.run());
    }

    // 1 RobotSystems

    @SuppressWarnings("unchecked")
    default <RobotSystem0 extends Subsystem & Reservable> boolean whileAvailable(
            Function1<RobotSystem0, Command> callback) {
        return getResourceManager()
                .scheduleIfAvailable(
                        callback,
                        subsystems -> {
                            return callback.apply((RobotSystem0) subsystems[0]);
                        });
    }

    default <RobotSystem0 extends Subsystem & Reservable, T> boolean whileAvailable(
            InvalidReturnType.Function1<RobotSystem0, T> callback) {
        return false;
    }

    @SuppressWarnings("unchecked")
    default <RobotSystem0 extends Subsystem & Reservable> void onceAvailable(
            Function1<RobotSystem0, Command> callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(
                        callback,
                        subsystems -> {
                            return callback.apply((RobotSystem0) subsystems[0]);
                        });
    }

    default <RobotSystem0 extends Subsystem & Reservable, T> void onceAvailable(
            InvalidReturnType.Function1<RobotSystem0, T> callback) {}

    @SuppressWarnings("unchecked")
    default <RobotSystem0 extends Subsystem & Reservable> boolean whileAvailable(
            Consumer1<RobotSystem0> callback) {
        return getResourceManager()
                .runIfAvailable(
                        callback,
                        subsystems -> {
                            callback.accept((RobotSystem0) subsystems[0]);
                        });
    }

    @SuppressWarnings("unchecked")
    default <RobotSystem0 extends Subsystem & Reservable> void onceAvailable(
            Consumer1<RobotSystem0> callback) {
        getResourceManager()
                .runOnceIfAvailable(
                        callback,
                        subsystems -> {
                            callback.accept((RobotSystem0) subsystems[0]);
                        });
    }

    default <RobotSystem0 extends Subsystem & Reservable> void byDefault(
            Function1<RobotSystem0, Command> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <RobotSystem0 extends Subsystem & Reservable, T> void byDefault(
            InvalidReturnType.Function1<RobotSystem0, T> callback) {}

    default <RobotSystem0 extends Subsystem & Reservable> void byDefault(
            Consumer1<RobotSystem0> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    // 2 RobotSystems

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            boolean whileAvailable(Function2<RobotSystem0, RobotSystem1, Command> callback) {
        return getResourceManager()
                .scheduleIfAvailable(
                        callback,
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0], (RobotSystem1) subsystems[1]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    T>
            boolean whileAvailable(
                    InvalidReturnType.Function2<RobotSystem0, RobotSystem1, T> callback) {
        return false;
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            void onceAvailable(Function2<RobotSystem0, RobotSystem1, Command> callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(
                        callback,
                        subsystems -> {
                            return callback.apply(
                                    (RobotSystem0) subsystems[0], (RobotSystem1) subsystems[1]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    T>
            void onceAvailable(
                    InvalidReturnType.Function2<RobotSystem0, RobotSystem1, T> callback) {}

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            boolean whileAvailable(Consumer2<RobotSystem0, RobotSystem1> callback) {
        return getResourceManager()
                .runIfAvailable(
                        callback,
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0], (RobotSystem1) subsystems[1]);
                        });
    }

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            void onceAvailable(Consumer2<RobotSystem0, RobotSystem1> callback) {
        getResourceManager()
                .runOnceIfAvailable(
                        callback,
                        subsystems -> {
                            callback.accept(
                                    (RobotSystem0) subsystems[0], (RobotSystem1) subsystems[1]);
                        });
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            void byDefault(Function2<RobotSystem0, RobotSystem1, Command> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    T>
            void byDefault(InvalidReturnType.Function2<RobotSystem0, RobotSystem1, T> callback) {}

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable>
            void byDefault(Consumer2<RobotSystem0, RobotSystem1> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    // 3 RobotSystems

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Function3<RobotSystem0, RobotSystem1, RobotSystem2, Command> callback) {
        return getResourceManager()
                .scheduleIfAvailable(
                        callback,
                        subsystems -> {
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
                    Function3<RobotSystem0, RobotSystem1, RobotSystem2, Command> callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(
                        callback,
                        subsystems -> {
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
                    InvalidReturnType.Function3<RobotSystem0, RobotSystem1, RobotSystem2, T>
                            callback) {}

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable>
            boolean whileAvailable(Consumer3<RobotSystem0, RobotSystem1, RobotSystem2> callback) {
        return getResourceManager()
                .runIfAvailable(
                        callback,
                        subsystems -> {
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
            void onceAvailable(Consumer3<RobotSystem0, RobotSystem1, RobotSystem2> callback) {
        getResourceManager()
                .runOnceIfAvailable(
                        callback,
                        subsystems -> {
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
            void byDefault(Function3<RobotSystem0, RobotSystem1, RobotSystem2, Command> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    T>
            void byDefault(
                    InvalidReturnType.Function3<RobotSystem0, RobotSystem1, RobotSystem2, T>
                            callback) {}

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable>
            void byDefault(Consumer3<RobotSystem0, RobotSystem1, RobotSystem2> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    // 4 RobotSystems

    @SuppressWarnings("unchecked")
    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Function4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, Command>
                            callback) {
        return getResourceManager()
                .scheduleIfAvailable(
                        callback,
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
                    Function4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, Command>
                            callback) {
        getResourceManager()
                .scheduleOnceIfAvailable(
                        callback,
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
                    Consumer4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3> callback) {
        return getResourceManager()
                .runIfAvailable(
                        callback,
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
                    Consumer4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3> callback) {
        getResourceManager()
                .runOnceIfAvailable(
                        callback,
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
                    Function4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, Command>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    T>
            void byDefault(
                    InvalidReturnType.Function4<
                                    RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, T>
                            callback) {}

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable>
            void byDefault(
                    Consumer4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
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
                    Consumer5<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, RobotSystem4>
                            callback) {
        return getResourceManager()
                .runIfAvailable(
                        callback,
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
                    Consumer5<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, RobotSystem4>
                            callback) {
        getResourceManager()
                .runOnceIfAvailable(
                        callback,
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
                    Function5<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    Command>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <
                    RobotSystem0 extends Subsystem & Reservable,
                    RobotSystem1 extends Subsystem & Reservable,
                    RobotSystem2 extends Subsystem & Reservable,
                    RobotSystem3 extends Subsystem & Reservable,
                    RobotSystem4 extends Subsystem & Reservable,
                    T>
            void byDefault(
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
                    Consumer5<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, RobotSystem4>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
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
                    Function6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    Command>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
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
                    Consumer6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
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
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
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
                    Consumer7<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    RobotSystem6>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
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
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
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
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }
}
