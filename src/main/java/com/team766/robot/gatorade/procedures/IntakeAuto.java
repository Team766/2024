package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class IntakeAuto extends PathSequenceAuto {
    private final GamePieceType gamePieceType;
    private final Drive drive;
    private final Intake intake;

    public IntakeAuto(GamePieceType gamePieceType, Drive drive, Intake intake) {
        super(reservations(drive, intake), drive, new Pose2d(2.00, 7.00, new Rotation2d(0)));
        this.gamePieceType = gamePieceType;
        this.drive = drive;
        this.intake = intake;
    }

    @Override
    protected void runSequence(Context context) {
        intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.IN));
        runPath(context, "Intake_Path_1");
        intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.IDLE));
        runPath(context, "Intake_Path_2");
        intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.OUT));
        drive.setGoal(new Drive.SetCross());
        context.waitForSeconds(1);
        intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.STOP));
        runPath(context, "Intake_Path_3");
        intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.IN));
        runPath(context, "Intake_Path_4");
        drive.setGoal(new Drive.SetCross());
        intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.OUT));
        context.waitForSeconds(2);
        intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.STOP));
    }
}
