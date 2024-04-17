package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;

public interface ProcedureWithValueInterface<T> {
    void run(ContextWithValue<T> context);

    Set<Subsystem> getRequirements();
}
