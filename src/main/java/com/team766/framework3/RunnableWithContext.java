package com.team766.framework3;

import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;

public interface RunnableWithContext {
    void run(Context context);

    Set<Subsystem> reservations();
}
