package com.team766.robot.gatorade.procedures;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.gatorade.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class LoopAuto extends PathSequenceAuto {
    public LoopAuto() {
        super(Robot.drive, new Pose2d(2.00, 7.00, new Rotation2d(0)));
        for (int i = 0; i < 5; i++) {
            add("Loop Test");
        }
    }
}
