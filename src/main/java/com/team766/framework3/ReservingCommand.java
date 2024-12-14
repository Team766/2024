package com.team766.framework3;

import com.team766.framework.StackTraceUtils;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.WrapperCommand;
import java.util.ArrayDeque;

/**
 * This class's static members encapsulate the functionality for tracking and verifying that
 * Commands have correctly reserved Subsystems.
 *
 * Instances of this class wrap pre-existing Commands to allow for proper tracking of those
 * Commands' reservations.
 */
public final class ReservingCommand extends WrapperCommand {
    /**
     * The currently-executing Command (e.g. Context/InstantCommand).
     */
    private static ArrayDeque<Command> currentCommands = new ArrayDeque<>();

    /**
     * Throws an exception if the currently-executing Command does not reserve the given Subsystem.
     *
     * @throws IllegalStateException
     *      if the currently-executing Command does not reserve the given Subsystem.
     */
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

    /**
     * Returns true iff the currently-executing Command has reserved the given Subsystem.
     */
    public static boolean currentCommandHasReservation(Subsystem subsystem) {
        return currentCommands.size() > 0 && currentCommands.peekLast().hasRequirement(subsystem);
    }

    /**
     * Call this method whenever a Command begins executing to record the Subsystems that it has
     * reserved.
     */
    public static void enterCommand(Command command) {
        if (CommandScheduler.getInstance().isScheduled(command)) {
            if (!currentCommands.isEmpty()) {
                throw new IllegalStateException();
            }
        } else if (!command.getRequirements().isEmpty()) {
            checkProcedureReservationsSubset(currentCommands.getLast(), command);
        }
        currentCommands.addLast(command);
    }

    /**
     * Call this method whenever a Command finishes executing if enterCommand was previously called.
     */
    public static void exitCommand(Command command) {
        if (currentCommands.removeLast() != command) {
            throw new IllegalStateException();
        }
    }

    /**
     * Throws an exception if the child Command's reservations are not a subset of the parent
     * Command's reservations.
     *
     * @throws IllegalArgumentException
     *      if the child Command's reservations are not a subset of the parent Command's
     *      reservations.
     */
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
