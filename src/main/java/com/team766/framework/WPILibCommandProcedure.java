package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Command;

/**
 * This wraps a class that confroms to WPILib's Command interface, and allows
 * it to be used in the Maroon Framework as a Procedure.
 */
public class WPILibCommandProcedure extends Procedure {

    private final Command command;

    /**
     * @param command The WPILib Command to adapt
     */
    public WPILibCommandProcedure(final Command command_) {
        super(reservations(command_.getRequirements()));
        this.command = command_;
    }

    @Override
    public void run(final Context context) {
        boolean interrupted = false;
        try {
            this.command.initialize();
            while (!this.command.isFinished()) {
                this.command.execute();
                context.yield();
            }
        } catch (Throwable ex) {
            interrupted = true;
            throw ex;
        } finally {
            this.command.end(interrupted);
        }
    }
}
