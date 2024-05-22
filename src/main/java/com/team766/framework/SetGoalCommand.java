package com.team766.framework;

import java.util.Objects;

public class SetGoalCommand<Goal, SubsystemT extends Subsystem<?, Goal>> extends InstantProcedure {
    private Goal goal;
    private SubsystemT subsystem;

    public SetGoalCommand(SubsystemT subsystem, Goal goal) {
        super(reservations(subsystem));

        this.subsystem = subsystem;
        this.goal = Objects.requireNonNull(goal, "Goal object must be non-null");
    }

    @Override
    protected void run() {
        subsystem.setGoal(goal);
    }
}
