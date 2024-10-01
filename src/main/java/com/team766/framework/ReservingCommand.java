package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.WrapperCommand;
import java.util.ArrayDeque;

public final class ReservingCommand extends WrapperCommand {
    /*
     * The currently-executing Command (Context/InstantCommand).
     */
    private static ArrayDeque<Command> currentCommands = new ArrayDeque<>();

    public static void checkCurrentCommandHasReservation(Subsystem subsystem) {
        if (!currentCommandHasReservation(subsystem)) {
            var exception =
                    new IllegalStateException(
                            subsystem.getName() + " tried to be used without reserving it");
            Logger.get(Category.FRAMEWORK)
                    .logRaw(
                            Severity.ERROR,
                            exception.getMessage()
                                    + "\n"
                                    + StackTraceUtils.getStackTrace(exception.getStackTrace()));
            throw exception;
        }
    }

    public static boolean currentCommandHasReservation(Subsystem subsystem) {
        return currentCommands.size() > 0 && currentCommands.peekLast().hasRequirement(subsystem);
    }

    public static void enterCommand(Command command) {
        if (CommandScheduler.getInstance().isScheduled(command)) {
            if (!currentCommands.isEmpty()) {
                throw new IllegalStateException();
            }
        } else {
            checkProcedureReservationsSubset(currentCommands.getLast(), command);
        }
        currentCommands.addLast(command);
    }

    public static void exitCommand(Command command) {
        if (currentCommands.removeLast() != command) {
            throw new IllegalStateException();
        }
    }

    private static void checkProcedureReservationsSubset(Command parent, Command child) {
        final var thisReservations = parent.getRequirements();
        for (var req : child.getRequirements()) {
            if (!thisReservations.contains(req)) {
                throw new IllegalArgumentException(
                        parent.getName()
                                + " tried to run "
                                + child.getName()
                                + " but is missing the reservation on "
                                + req.getName());
            }
        }
    }

    public ReservingCommand(Command command) {
        super(command);
    }

    @Override
    public void initialize() {
        try {
            enterCommand(m_command);
            super.initialize();
        } finally {
            exitCommand(m_command);
        }
    }

    @Override
    public void execute() {
        try {
            enterCommand(m_command);
            super.execute();
        } finally {
            exitCommand(m_command);
        }
    }

    @Override
    public void end(boolean interrupted) {
        try {
            enterCommand(m_command);
            super.end(interrupted);
        } finally {
            exitCommand(m_command);
        }
    }
}
