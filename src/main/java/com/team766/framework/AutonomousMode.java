package com.team766.framework;

import com.team766.framework.resources.ResourceManager;
import com.team766.library.function.Function1;
import com.team766.library.function.Function2;
import com.team766.library.function.Function3;
import com.team766.library.function.Function4;
import com.team766.library.function.Function5;
import com.team766.library.function.Function6;
import com.team766.library.function.Function7;
import com.team766.library.function.Function8;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.function.Supplier;

public class AutonomousMode {
    private final Supplier<Command> m_constructor;
    private final String m_name;

    @SuppressWarnings("unchecked")
    public <Subsystem0 extends Subsystem> AutonomousMode(
            final String name, Function1<Subsystem0, Command> callback) {
        this(
                name,
                () -> ResourceManager.makeAutonomus(callback, subsystems -> {
                    return callback.apply((Subsystem0) subsystems[0]);
                }));
    }

    @SuppressWarnings("unchecked")
    public <Subsystem0 extends Subsystem, Subsystem1 extends Subsystem> AutonomousMode(
            final String name, Function2<Subsystem0, Subsystem1, Command> callback) {
        this(
                name,
                () -> ResourceManager.makeAutonomus(callback, subsystems -> {
                    return callback.apply((Subsystem0) subsystems[0], (Subsystem1) subsystems[1]);
                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem>
            AutonomousMode(
                    final String name,
                    Function3<Subsystem0, Subsystem1, Subsystem2, Command> callback) {
        this(
                name,
                () -> ResourceManager.makeAutonomus(callback, subsystems -> {
                    return callback.apply(
                            (Subsystem0) subsystems[0], (Subsystem1) subsystems[1], (Subsystem2)
                                    subsystems[2]);
                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem>
            AutonomousMode(
                    final String name,
                    Function4<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Command> callback) {
        this(
                name,
                () -> ResourceManager.makeAutonomus(callback, subsystems -> {
                    return callback.apply(
                            (Subsystem0) subsystems[0],
                            (Subsystem1) subsystems[1],
                            (Subsystem2) subsystems[2],
                            (Subsystem3) subsystems[3]);
                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem>
            AutonomousMode(
                    final String name,
                    Function5<Subsystem0, Subsystem1, Subsystem2, Subsystem3, Subsystem4, Command>
                            callback) {
        this(
                name,
                () -> ResourceManager.makeAutonomus(callback, subsystems -> {
                    return callback.apply(
                            (Subsystem0) subsystems[0],
                            (Subsystem1) subsystems[1],
                            (Subsystem2) subsystems[2],
                            (Subsystem3) subsystems[3],
                            (Subsystem4) subsystems[4]);
                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem>
            AutonomousMode(
                    final String name,
                    Function6<
                                    Subsystem0,
                                    Subsystem1,
                                    Subsystem2,
                                    Subsystem3,
                                    Subsystem4,
                                    Subsystem5,
                                    Command>
                            callback) {
        this(
                name,
                () -> ResourceManager.makeAutonomus(callback, subsystems -> {
                    return callback.apply(
                            (Subsystem0) subsystems[0],
                            (Subsystem1) subsystems[1],
                            (Subsystem2) subsystems[2],
                            (Subsystem3) subsystems[3],
                            (Subsystem4) subsystems[4],
                            (Subsystem5) subsystems[5]);
                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem,
                    Subsystem6 extends Subsystem>
            AutonomousMode(
                    final String name,
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
        this(
                name,
                () -> ResourceManager.makeAutonomus(callback, subsystems -> {
                    return callback.apply(
                            (Subsystem0) subsystems[0],
                            (Subsystem1) subsystems[1],
                            (Subsystem2) subsystems[2],
                            (Subsystem3) subsystems[3],
                            (Subsystem4) subsystems[4],
                            (Subsystem5) subsystems[5],
                            (Subsystem6) subsystems[6]);
                }));
    }

    @SuppressWarnings("unchecked")
    public <
                    Subsystem0 extends Subsystem,
                    Subsystem1 extends Subsystem,
                    Subsystem2 extends Subsystem,
                    Subsystem3 extends Subsystem,
                    Subsystem4 extends Subsystem,
                    Subsystem5 extends Subsystem,
                    Subsystem6 extends Subsystem,
                    Subsystem7 extends Subsystem>
            AutonomousMode(
                    final String name,
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
        this(
                name,
                () -> ResourceManager.makeAutonomus(callback, subsystems -> {
                    return callback.apply(
                            (Subsystem0) subsystems[0],
                            (Subsystem1) subsystems[1],
                            (Subsystem2) subsystems[2],
                            (Subsystem3) subsystems[3],
                            (Subsystem4) subsystems[4],
                            (Subsystem5) subsystems[5],
                            (Subsystem6) subsystems[6],
                            (Subsystem7) subsystems[7]);
                }));
    }

    public AutonomousMode(final String name, final Supplier<Command> constructor) {
        m_constructor = constructor;
        m_name = name;
    }

    public Command instantiate() {
        return m_constructor.get();
    }

    public String name() {
        return m_name;
    }

    @Override
    public String toString() {
        return name();
    }

    public AutonomousMode clone() {
        return new AutonomousMode(m_name, m_constructor);
    }
}
