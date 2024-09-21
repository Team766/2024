package com.team766.robot.gatorade.procedures;

import com.team766.framework3.Context;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class IntakeAuto extends PathSequenceAuto {
    private final GamePieceType gamePieceType;
    private final SwerveDrive drive;
    private final Intake intake;

    public IntakeAuto(GamePieceType gamePieceType, SwerveDrive drive, Intake intake) {
        super(drive, new Pose2d(2.00, 7.00, new Rotation2d(0)));
        this.gamePieceType = gamePieceType;
        this.drive = reserve(drive);
        this.intake = reserve(intake);
    }

    @Override
    protected void runSequence(Context context) {
        intake.setRequest(new Intake.IntakeState(gamePieceType, Intake.MotorState.IN));
        runPath(context, "Intake_Path_1");
        intake.setRequest(new Intake.IntakeState(gamePieceType, Intake.MotorState.IDLE));
        runPath(context, "Intake_Path_2");
        intake.setRequest(new Intake.IntakeState(gamePieceType, Intake.MotorState.OUT));
        drive.setRequest(new SwerveDrive.SetCross());
        context.waitForSeconds(1);
        intake.setRequest(new Intake.IntakeState(gamePieceType, Intake.MotorState.STOP));
        runPath(context, "Intake_Path_3");
        intake.setRequest(new Intake.IntakeState(gamePieceType, Intake.MotorState.IN));
        runPath(context, "Intake_Path_4");
        drive.setRequest(new SwerveDrive.SetCross());
        intake.setRequest(new Intake.IntakeState(gamePieceType, Intake.MotorState.OUT));
        context.waitForSeconds(2);
        intake.setRequest(new Intake.IntakeState(gamePieceType, Intake.MotorState.STOP));
    }
}
