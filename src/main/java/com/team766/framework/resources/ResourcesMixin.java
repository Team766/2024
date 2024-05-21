/** Generated from the template in ResourcesMixin.java.template */
package com.team766.framework.resources;

import com.team766.library.function.Functions.*;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface ResourcesMixin {
    ResourceManager getResourceManager();

    // 1 subsystems

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem> boolean ifAvailable(
            Function1<Subsystem0, Command> callback) {
        return getResourceManager().scheduleIfAvailable(callback, subsystems -> {
            return callback.apply((Subsystem0) subsystems[0]);
        });
    }

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem> boolean ifAvailable(Consumer1<Subsystem0> callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept((Subsystem0) subsystems[0]);
        });
    }

    default <Subsystem0 extends Subsystem> void byDefault(Function1<Subsystem0, Command> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    default <Subsystem0 extends Subsystem> void byDefault(Consumer1<Subsystem0> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    // 2 subsystems

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem, Subsystem1 extends Subsystem> boolean ifAvailable(
            Function2<Subsystem0, Subsystem1, Command> callback) {
        return getResourceManager().scheduleIfAvailable(callback, subsystems -> {
            return callback.apply((Subsystem0) subsystems[0], (Subsystem1) subsystems[1]);
        });
    }

    @SuppressWarnings("unchecked")
    default <Subsystem0 extends Subsystem, Subsystem1 extends Subsystem> boolean ifAvailable(
            Consumer2<Subsystem0, Subsystem1> callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept((Subsystem0) subsystems[0], (Subsystem1) subsystems[1]);
        });
    }

    default <Subsystem0 extends Subsystem, Subsystem1 extends Subsystem> void byDefault(
            Function2<Subsystem0, Subsystem1, Command> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    default <Subsystem0 extends Subsystem, Subsystem1 extends Subsystem> void byDefault(
            Consumer2<Subsystem0, Subsystem1> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    // 3 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem>
            boolean ifAvailable(Function3<Subsystem0, Subsystem1, Subsystem2, Command> callback) {
        return getResourceManager().scheduleIfAvailable(callback, subsystems -> {
            return callback.apply(
                    (Subsystem0) subsystems[0], (Subsystem1) subsystems[1], (Subsystem2)
                            subsystems[2]);
        });
    }

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem>
            boolean ifAvailable(Consumer3<Subsystem0, Subsystem1, Subsystem2> callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept((Subsystem0) subsystems[0], (Subsystem1) subsystems[1], (Subsystem2)
                    subsystems[2]);
        });
    }

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem>
            void byDefault(Function3<Subsystem0, Subsystem1, Subsystem2, Command> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem>
            void byDefault(Consumer3<Subsystem0, Subsystem1, Subsystem2> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    // 4 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem>
            boolean ifAvailable(
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
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem>
            boolean ifAvailable(
                    Consumer4<Subsystem0, Subsystem1, Subsystem2, Subsystem3> callback) {
        return getResourceManager().runIfAvailable(callback, subsystems -> {
            callback.accept(
                    (Subsystem0) subsystems[0],
                    (Subsystem1) subsystems[1],
                    (Subsystem2) subsystems[2],
                    (Subsystem3) subsystems[3]);
        });
    }

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem>
            void byDefault(
                    Function4<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Command> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem>
            void byDefault(Consumer4<Subsystem0, Subsystem1, Subsystem2, Subsystem3> callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    // 5 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem>
            boolean ifAvailable(
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
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem>
            boolean ifAvailable(
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

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem>
            void byDefault(
                    Function5<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Subsystem4, Command>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem>
            void byDefault(
                    Consumer5<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Subsystem4>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    // 6 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem>
            boolean ifAvailable(
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
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem>
            boolean ifAvailable(
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

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem>
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
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem>
            void byDefault(
                    Consumer6<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5>
                            callback) {
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    // 7 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem,
                    Subsystem6 extends Subsystem>
            boolean ifAvailable(
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
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem,
                    Subsystem6 extends Subsystem>
            boolean ifAvailable(
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

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem,
                    Subsystem6 extends Subsystem>
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
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem,
                    Subsystem6 extends Subsystem>
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
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    // 8 subsystems

    @SuppressWarnings("unchecked")
    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem,
                    Subsystem6 extends Subsystem,
                    Subsystem7 extends Subsystem>
            boolean ifAvailable(
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
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem,
                    Subsystem6 extends Subsystem,
                    Subsystem7 extends Subsystem>
            boolean ifAvailable(
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

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem,
                    Subsystem6 extends Subsystem,
                    Subsystem7 extends Subsystem>
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
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }

    default <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem,
                    Subsystem6 extends Subsystem,
                    Subsystem7 extends Subsystem>
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
        getResourceManager().registerTransientEndFrameCallback(() -> ifAvailable(callback));
    }
}
