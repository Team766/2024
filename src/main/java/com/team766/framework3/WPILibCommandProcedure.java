package com.team766.framework3;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;

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
        super(command.getName(), checkSubsystemsAreMechanisms(command.getRequirements()));
        this.command = command;
    }

    @SuppressWarnings("unchecked")
    private static Set<Mechanism<?>> checkSubsystemsAreMechanisms(Set<Subsystem> requirements) {
        for (var s : requirements) {
            if (!(s instanceof Mechanism<?>)) {
                throw new IllegalArgumentException(
                        "Maroon Framework requires the use of Mechanism instead of Subsystem");
            }
        }
        return (Set<Mechanism<?>>) (Set<?>) requirements;
    }

    @Override
    public void run(final Context context) {
        boolean interrupted = false;
        try {
            command.initialize();
            if (!command.isFinished()) {
                context.waitFor(
                        () -> {
                            command.execute();
                            return command.isFinished();
                        });
            }
        } catch (Throwable ex) {
            interrupted = true;
            throw ex;
        } finally {
            command.end(interrupted);
        }
    }

    @Override
    public Command createCommandToRunProcedure() {
        return command;
    }
}
