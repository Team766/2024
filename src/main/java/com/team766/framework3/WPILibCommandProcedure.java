package com.team766.framework3;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;

/**
 * This wraps a class that confroms to WPILib's Command interface, and allows
 * it to be used in the Maroon Framework as a Procedure.
 */
public final class WPILibCommandProcedure implements RunnableWithContext {

    private final Command command;

    /**
     * @param command The WPILib Command to adapt
     */
    public WPILibCommandProcedure(final Command command_) {
        this.command = command_;
    }

    @Override
    public void run(final Context context) {
        boolean interrupted = false;
        try {
            this.command.initialize();
            if (!this.command.isFinished()) {
                this.command.execute();
                while (!this.command.isFinished()) {
                    context.yield();
                    this.command.execute();
                }
            }
        } catch (Throwable ex) {
            interrupted = true;
            throw ex;
        } finally {
            this.command.end(interrupted);
        }
    }

    @Override
    public Set<Subsystem> reservations() {
        return this.command.getRequirements();
    }
}
