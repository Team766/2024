package com.team766.framework;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import java.util.Objects;

public class SetGoalCommand<Goal, SystemT extends RobotSystem<?, Goal>> extends InstantCommand {

    public SetGoalCommand(SystemT system, Goal goal) {
        super(() -> system.setGoal(goal), system);

        Objects.requireNonNull(goal, "Goal object must be non-null");
    }
}
