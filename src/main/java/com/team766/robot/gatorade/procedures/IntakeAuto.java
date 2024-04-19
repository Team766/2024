package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.gatorade.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class IntakeAuto extends PathSequenceAuto {
    public IntakeAuto() {
        super(Robot.drive, new Pose2d(2.00, 7.00, new Rotation2d(0)));
    }

    @Override
    protected void runSequence(Context context) {
        context.runSync(new IntakeIn());
        runPath(context, "Intake_Path_1");
        context.runSync(new IntakeIdle());
        runPath(context, "Intake_Path_2");
        context.runSync(new IntakeOut());
        context.runSync(new SetCross());
        context.waitForSeconds(1);
        context.runSync(new IntakeStop());
        runPath(context, "Intake_Path_3");
        context.runSync(new IntakeIn());
        runPath(context, "Intake_Path_4");
        context.runSync(new SetCross());
        context.runSync(new IntakeOut());
        context.waitForSeconds(2);
        context.runSync(new IntakeStop());
    }
}
