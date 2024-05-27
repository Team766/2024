package com.team766.framework;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import java.util.Objects;

public class SetGoalCommand<Goal, SubsystemT extends RobotSystem<?, Goal>> extends InstantCommand {

    public SetGoalCommand(SubsystemT subsystem, Goal goal) {
        super(() -> subsystem.setGoal(goal), subsystem);

        Objects.requireNonNull(goal, "Goal object must be non-null");
    }
}
