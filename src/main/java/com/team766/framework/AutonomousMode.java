package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Command;
import java.util.function.Supplier;

public class AutonomousMode {
    private final Supplier<Command> m_constructor;
    private final String m_name;

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
