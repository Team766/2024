package com.team766.framework3;

import edu.wpi.first.wpilibj2.command.Command;
import java.util.Set;

public abstract class InstantProcedure extends Procedure implements Runnable {
    protected InstantProcedure() {
        super();
    }

    protected InstantProcedure(String name, Set<Mechanism<?>> reservations) {
        super(name, reservations);
    }

    @Override
    public abstract void run();

    @Override
    public final void run(Context context) {
        run();
    }

    @Override
    Command createCommand() {
        return new InstantCommand(this);
    }
}
