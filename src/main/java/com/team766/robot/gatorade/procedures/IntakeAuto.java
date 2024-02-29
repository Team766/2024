package com.team766.robot.gatorade.procedures;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.gatorade.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class IntakeAuto extends PathSequenceAuto {
    public IntakeAuto() {
        super(Robot.drive, new Pose2d(2.00, 7.00, new Rotation2d(0)));
        add(new IntakeIn());
        add("Intake_Path_1");
        add(new IntakeIdle());
        add("Intake_Path_2");
        add(new IntakeOut());
        add(new SetCross());
        add(1);
        add(new IntakeStop());
        add("Intake_Path_3");
        add(new IntakeIn());
        add("Intake_Path_4");
        add(new SetCross());
        add(new IntakeOut());
        add(2);
        add(new IntakeStop());
    }
}
