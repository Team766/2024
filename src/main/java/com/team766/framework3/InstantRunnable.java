package com.team766.framework3;

import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;

// This interface is sealed because implementors must implement run(Context) like this:
// @Override
// public final void run(Context context) {
//     run();
// }
public sealed interface InstantRunnable extends Runnable, RunnableWithContext
        permits InstantProcedure {
    @Override
    void run();

    @Override
    Set<Subsystem> reservations();
}
