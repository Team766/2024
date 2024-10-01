package com.team766.framework3;

import edu.wpi.first.wpilibj2.command.Command;

/* package */ final class InstantCommand extends Command {
    private final InstantProcedure procedure;

    public InstantCommand(InstantProcedure procedure) {
        this.procedure = procedure;
        m_requirements.addAll(procedure.reservations());
        setName(procedure.getName());
    }

    @Override
    public void execute() {
        ReservingCommand.enterCommand(this);
        try {
            procedure.run();
        } finally {
            ReservingCommand.exitCommand(this);
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
