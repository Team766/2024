package com.team766.robot.gatorade.procedures;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.gatorade.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;

public class IntakeAuto extends PathSequenceAuto {
    public IntakeAuto() {
        super(Robot.drive, new Pose2d(2.00, 7.00, new Rotation2d(0)));
        addProcedure(new IntakeIn());
        addPath("Intake_Path_1");
        addProcedure(new IntakeIdle());
        addPath("Intake_Path_2");
        addProcedure(new IntakeOut());
        addProcedure(new SetCross());
        addWait(1);
        addProcedure(new IntakeStop());
        addPath("Intake_Path_3");
        addProcedure(new IntakeIn());
        addPath("Intake_Path_4");
        addProcedure(new SetCross());
        addProcedure(new IntakeOut());
        addWait(2);
        addProcedure(new IntakeStop());
    }
}
