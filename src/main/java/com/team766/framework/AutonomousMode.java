/** Generated from the template in AutonomousMode.java.template */
package com.team766.framework;

import com.team766.framework.resources.ResourceManager;
import com.team766.library.function.Functions.*;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.function.Supplier;

public class AutonomousMode extends AutonomousModeBase {
    public AutonomousMode(final String name, final Supplier<Command> constructor) {
        super(name, constructor);
    }

    @SuppressWarnings("unchecked")
    public <RobotSystem0 extends Subsystem> AutonomousMode(
            final String name, Function1<RobotSystem0, Command> callback) {
        super(
                name,
                () ->
                        ResourceManager.makeAutonomus(
                                callback,
                                subsystems -> {
                                    return callback.apply((RobotSystem0) subsystems[0]);
                                }));
    }

    @SuppressWarnings("unchecked")
    public <RobotSystem0 extends Subsystem, RobotSystem1 extends Subsystem> AutonomousMode(
            final String name, Function2<RobotSystem0, RobotSystem1, Command> callback) {
        super(
                name,
                () ->
                        ResourceManager.makeAutonomus(
                                callback,
                                subsystems -> {
                                    return callback.apply(
                                            (RobotSystem0) subsystems[0],
                                            (RobotSystem1) subsystems[1]);
                                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    RobotSystem0 extends Subsystem,
                    RobotSystem1 extends Subsystem,
                    RobotSystem2 extends Subsystem>
            AutonomousMode(
                    final String name,
                    Function3<RobotSystem0, RobotSystem1, RobotSystem2, Command> callback) {
        super(
                name,
                () ->
                        ResourceManager.makeAutonomus(
                                callback,
                                subsystems -> {
                                    return callback.apply(
                                            (RobotSystem0) subsystems[0],
                                            (RobotSystem1) subsystems[1],
                                            (RobotSystem2) subsystems[2]);
                                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    RobotSystem0 extends Subsystem,
                    RobotSystem1 extends Subsystem,
                    RobotSystem2 extends Subsystem,
                    RobotSystem3 extends Subsystem>
            AutonomousMode(
                    final String name,
                    Function4<RobotSystem0, RobotSystem1, RobotSystem2, RobotSystem3, Command>
                            callback) {
        super(
                name,
                () ->
                        ResourceManager.makeAutonomus(
                                callback,
                                subsystems -> {
                                    return callback.apply(
                                            (RobotSystem0) subsystems[0],
                                            (RobotSystem1) subsystems[1],
                                            (RobotSystem2) subsystems[2],
                                            (RobotSystem3) subsystems[3]);
                                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    RobotSystem0 extends Subsystem,
                    RobotSystem1 extends Subsystem,
                    RobotSystem2 extends Subsystem,
                    RobotSystem3 extends Subsystem,
                    RobotSystem4 extends Subsystem>
            AutonomousMode(
                    final String name,
                    Function5<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    Command>
                            callback) {
        super(
                name,
                () ->
                        ResourceManager.makeAutonomus(
                                callback,
                                subsystems -> {
                                    return callback.apply(
                                            (RobotSystem0) subsystems[0],
                                            (RobotSystem1) subsystems[1],
                                            (RobotSystem2) subsystems[2],
                                            (RobotSystem3) subsystems[3],
                                            (RobotSystem4) subsystems[4]);
                                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    RobotSystem0 extends Subsystem,
                    RobotSystem1 extends Subsystem,
                    RobotSystem2 extends Subsystem,
                    RobotSystem3 extends Subsystem,
                    RobotSystem4 extends Subsystem,
                    RobotSystem5 extends Subsystem>
            AutonomousMode(
                    final String name,
                    Function6<
                                    RobotSystem0,
                                    RobotSystem1,
                                    RobotSystem2,
                                    RobotSystem3,
                                    RobotSystem4,
                                    RobotSystem5,
                                    Command>
                            callback) {
        super(
                name,
                () ->
                        ResourceManager.makeAutonomus(
                                callback,
                                subsystems -> {
                                    return callback.apply(
                                            (RobotSystem0) subsystems[0],
                                            (RobotSystem1) subsystems[1],
                                            (RobotSystem2) subsystems[2],
                                            (RobotSystem3) subsystems[3],
                                            (RobotSystem4) subsystems[4],
                                            (RobotSystem5) subsystems[5]);
                                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    RobotSystem0 extends Subsystem,
                    RobotSystem1 extends Subsystem,
                    RobotSystem2 extends Subsystem,
                    RobotSystem3 extends Subsystem,
                    RobotSystem4 extends Subsystem,
                    RobotSystem5 extends Subsystem,
                    RobotSystem6 extends Subsystem>
            AutonomousMode(
                    final String name,
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
        super(
                name,
                () ->
                        ResourceManager.makeAutonomus(
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
                                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    RobotSystem0 extends Subsystem,
                    RobotSystem1 extends Subsystem,
                    RobotSystem2 extends Subsystem,
                    RobotSystem3 extends Subsystem,
                    RobotSystem4 extends Subsystem,
                    RobotSystem5 extends Subsystem,
                    RobotSystem6 extends Subsystem,
                    RobotSystem7 extends Subsystem>
            AutonomousMode(
                    final String name,
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
        super(
                name,
                () ->
                        ResourceManager.makeAutonomus(
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
                                }));
    }
}
