package com.team766.framework3;

import edu.wpi.first.wpilibj2.command.Command;

/**
 * This wraps a class that confroms to WPILib's Command interface, and allows
 * it to be used in the Maroon Framework as a Procedure.
 */
public final class WPILibCommandProcedure extends Procedure {

    private final Command command;

    /**
     * @param command The WPILib Command to adapt
     */
    public WPILibCommandProcedure(final Command command) {
        super(command.getName(), command.getRequirements());
        this.command = command;
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
}
