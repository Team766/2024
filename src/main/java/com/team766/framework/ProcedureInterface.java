package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;

public interface ProcedureInterface {
    void execute(Context context);

    Set<Subsystem> getReservations();
}
