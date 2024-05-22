package com.team766.robot.gatorade.procedures;

import com.team766.framework.SetGoalCommand;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class IntakeAuto extends PathSequenceAuto {
    public IntakeAuto(GamePieceType gamePieceType, Drive drive, Intake intake) {
        super(drive, new Pose2d(2.00, 7.00, new Rotation2d(0)));
        addProcedure(new SetGoalCommand<>(
                intake, new Intake.Status(gamePieceType, Intake.MotorState.IN)));
        addPath("Intake_Path_1");
        addProcedure(new SetGoalCommand<>(
                intake, new Intake.Status(gamePieceType, Intake.MotorState.IDLE)));
        addPath("Intake_Path_2");
        addProcedure(new SetGoalCommand<>(
                intake, new Intake.Status(gamePieceType, Intake.MotorState.OUT)));
        addProcedure(new SetGoalCommand<>(drive, new Drive.SetCross()));
        addWait(1);
        addProcedure(new SetGoalCommand<>(
                intake, new Intake.Status(gamePieceType, Intake.MotorState.STOP)));
        addPath("Intake_Path_3");
        addProcedure(new SetGoalCommand<>(
                intake, new Intake.Status(gamePieceType, Intake.MotorState.IN)));
        addPath("Intake_Path_4");
        addProcedure(new SetGoalCommand<>(drive, new Drive.SetCross()));
        addProcedure(new SetGoalCommand<>(
                intake, new Intake.Status(gamePieceType, Intake.MotorState.OUT)));
        addWait(2);
        addProcedure(new SetGoalCommand<>(
                intake, new Intake.Status(gamePieceType, Intake.MotorState.STOP)));
    }
}
