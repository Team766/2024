/** Generated from the template in ResourcesMixin.java.template */
package com.team766.framework.resources;

import com.team766.library.function.Functions.*;
import com.team766.library.function.Functions.Runnable;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface ResourcesMixin {
    ResourceManager getResourceManager();

    // 0 subsystems

    default void repeatedly(Provider<Command> callback) {
        getResourceManager().scheduleIfAvailable(callback, subsystems -> callback.get());
    }

    default void once(Provider<Command> callback) {
        getResourceManager().scheduleOnceIfAvailable(callback, subsystems -> callback.get());
    }

    default void repeatedly(Runnable callback) {
        callback.run();
    }

    default void once(Runnable callback) {
        getResourceManager().runOnceIfAvailable(callback, subsystems -> callback.run());
    }

    // 1 subsystems

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem & Reservable> boolean whileAvailable(
            Function1<Subsystem0, Command> callback) {
        return getResourceManager().scheduleIfAvailable(callback, subsystems -> {
            return callback.apply((Subsystem0) subsystems[0]);
        });
    }

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem & Reservable> void onceAvailable(
            Function1<Subsystem0, Command> callback) {
        getResourceManager().scheduleOnceIfAvailable(callback, subsystems -> {
            return callback.apply((Subsystem0) subsystems[0]);
        });
    }

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem & Reservable> boolean whileAvailable(
            Consumer1<Subsystem0> callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept((Subsystem0) subsystems[0]);
        });
    }

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem & Reservable> void onceAvailable(
            Consumer1<Subsystem0> callback) {
        getResourceManager().runOnceIfAvailable(callback, subsystems -> {
            callback.accept((Subsystem0) subsystems[0]);
        });
    }

    default <Subsystem0 extends Subsystem & Reservable> void byDefault(
            Function1<Subsystem0, Command> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <Subsystem0 extends Subsystem & Reservable> void byDefault(
            Consumer1<Subsystem0> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    // 2 subsystems

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem & Reservable, Subsystem1 extends Subsystem & Reservable>
            boolean whileAvailable(Function2<Subsystem0, Subsystem1, Command> callback) {
        return getResourceManager().scheduleIfAvailable(callback, subsystems -> {
            return callback.apply((Subsystem0) subsystems[0], (Subsystem1) subsystems[1]);
        });
    }

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem & Reservable, Subsystem1 extends Subsystem & Reservable>
            void onceAvailable(Function2<Subsystem0, Subsystem1, Command> callback) {
        getResourceManager().scheduleOnceIfAvailable(callback, subsystems -> {
            return callback.apply((Subsystem0) subsystems[0], (Subsystem1) subsystems[1]);
        });
    }

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem & Reservable, Subsystem1 extends Subsystem & Reservable>
            boolean whileAvailable(Consumer2<Subsystem0, Subsystem1> callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept((Subsystem0) subsystems[0], (Subsystem1) subsystems[1]);
        });
    }

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem & Reservable, Subsystem1 extends Subsystem & Reservable>
            void onceAvailable(Consumer2<Subsystem0, Subsystem1> callback) {
        getResourceManager().runOnceIfAvailable(callback, subsystems -> {
            callback.accept((Subsystem0) subsystems[0], (Subsystem1) subsystems[1]);
        });
    }

    default <Subsystem0 extends Subsystem & Reservable, Subsystem1 extends Subsystem & Reservable>
            void byDefault(Function2<Subsystem0, Subsystem1, Command> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <Subsystem0 extends Subsystem & Reservable, Subsystem1 extends Subsystem & Reservable>
            void byDefault(Consumer2<Subsystem0, Subsystem1> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    // 3 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Function3<Subsystem0, Subsystem1, Subsystem2, Command> callback) {
        return getResourceManager().scheduleIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0], (Subsystem1) subsystems[1], (Subsystem2)
                            subsystems[2]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable>
            void onceAvailable(Function3<Subsystem0, Subsystem1, Subsystem2, Command> callback) {
        getResourceManager().scheduleOnceIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0], (Subsystem1) subsystems[1], (Subsystem2)
                            subsystems[2]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable>
            boolean whileAvailable(Consumer3<Subsystem0, Subsystem1, Subsystem2> callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept((Subsystem0) subsystems[0], (Subsystem1) subsystems[1], (Subsystem2)
                    subsystems[2]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable>
            void onceAvailable(Consumer3<Subsystem0, Subsystem1, Subsystem2> callback) {
        getResourceManager().runOnceIfAvailable(callback, subsystems -> {
            callback.accept((Subsystem0) subsystems[0], (Subsystem1) subsystems[1], (Subsystem2)
                    subsystems[2]);
        });
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable>
            void byDefault(Function3<Subsystem0, Subsystem1, Subsystem2, Command> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable>
            void byDefault(Consumer3<Subsystem0, Subsystem1, Subsystem2> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    // 4 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Function4<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Command> callback) {
        return getResourceManager().scheduleIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable>
            void onceAvailable(
                    Function4<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Command> callback) {
        getResourceManager().scheduleOnceIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Consumer4<Subsystem0, Subsystem1, Subsystem2, Subsystem3> callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable>
            void onceAvailable(Consumer4<Subsystem0, Subsystem1, Subsystem2, Subsystem3> callback) {
        getResourceManager().runOnceIfAvailable(callback, subsystems -> {
            callback.accept(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3]);
        });
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable>
            void byDefault(
                    Function4<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Command> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable>
            void byDefault(Consumer4<Subsystem0, Subsystem1, Subsystem2, Subsystem3> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    // 5 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Function5<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Subsystem4, Command>
                            callback) {
        return getResourceManager().scheduleIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable>
            void onceAvailable(
                    Function5<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Subsystem4, Command>
                            callback) {
        getResourceManager().scheduleOnceIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Consumer5<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Subsystem4>
                            callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable>
            void onceAvailable(
                    Consumer5<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Subsystem4>
                            callback) {
        getResourceManager().runOnceIfAvailable(callback, subsystems -> {
            callback.accept(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4]);
        });
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable>
            void byDefault(
                    Function5<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Subsystem4, Command>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable>
            void byDefault(
                    Consumer5<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Subsystem4>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    // 6 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Function6<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Command>
                            callback) {
        return getResourceManager().scheduleIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable>
            void onceAvailable(
                    Function6<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Command>
                            callback) {
        getResourceManager().scheduleOnceIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Consumer6<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5>
                            callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable>
            void onceAvailable(
                    Consumer6<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5>
                            callback) {
        getResourceManager().runOnceIfAvailable(callback, subsystems -> {
            callback.accept(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5]);
        });
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable>
            void byDefault(
                    Function6<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Command>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable>
            void byDefault(
                    Consumer6<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    // 7 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Function7<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6,
                                    Command>
                            callback) {
        return getResourceManager().scheduleIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5],
                    (Subsystem6) subsystems[6]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable>
            void onceAvailable(
                    Function7<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6,
                                    Command>
                            callback) {
        getResourceManager().scheduleOnceIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5],
                    (Subsystem6) subsystems[6]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Consumer7<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6>
                            callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5],
                    (Subsystem6) subsystems[6]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable>
            void onceAvailable(
                    Consumer7<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6>
                            callback) {
        getResourceManager().runOnceIfAvailable(callback, subsystems -> {
            callback.accept(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5],
                    (Subsystem6) subsystems[6]);
        });
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable>
            void byDefault(
                    Function7<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6,
                                    Command>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable>
            void byDefault(
                    Consumer7<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    // 8 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable,
                    Subsystem7 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Function8<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6,
                                    Subsystem7,
                                    Command>
                            callback) {
        return getResourceManager().scheduleIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5],
                    (Subsystem6) subsystems[6],
                    (Subsystem7) subsystems[7]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable,
                    Subsystem7 extends Subsystem & Reservable>
            void onceAvailable(
                    Function8<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6,
                                    Subsystem7,
                                    Command>
                            callback) {
        getResourceManager().scheduleOnceIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5],
                    (Subsystem6) subsystems[6],
                    (Subsystem7) subsystems[7]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable,
                    Subsystem7 extends Subsystem & Reservable>
            boolean whileAvailable(
                    Consumer8<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6,
                                    Subsystem7>
                            callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5],
                    (Subsystem6) subsystems[6],
                    (Subsystem7) subsystems[7]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable,
                    Subsystem7 extends Subsystem & Reservable>
            void onceAvailable(
                    Consumer8<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6,
                                    Subsystem7>
                            callback) {
        getResourceManager().runOnceIfAvailable(callback, subsystems -> {
            callback.accept(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3],
                    (Subsystem4) subsystems[4],
                    (Subsystem5) subsystems[5],
                    (Subsystem6) subsystems[6],
                    (Subsystem7) subsystems[7]);
        });
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable,
                    Subsystem7 extends Subsystem & Reservable>
            void byDefault(
                    Function8<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6,
                                    Subsystem7,
                                    Command>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem & Reservable,
                    Subsystem1 extends Subsystem & Reservable,
                    Subsystem2 extends Subsystem & Reservable,
                    Subsystem3 extends Subsystem & Reservable,
                    Subsystem4 extends Subsystem & Reservable,
                    Subsystem5 extends Subsystem & Reservable,
                    Subsystem6 extends Subsystem & Reservable,
                    Subsystem7 extends Subsystem & Reservable>
            void byDefault(
                    Consumer8<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Subsystem6,
                                    Subsystem7>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> whileAvailable(callback));
    }
}
