package com.team766.robot.gatorade;

import com.team766.framework.AutonomousMode;
import com.team766.framework.LightsBase;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.constants.SwerveDriveConstants;
import com.team766.robot.gatorade.mechanisms.*;
import com.team766.robot.gatorade.procedures.LoopAuto;
import com.team766.robot.gatorade.procedures.TestPathAuto;

public class Robot implements RobotConfigurator {
    private Drive drive;
    private Intake intake;
    private Superstructure superstructure;

    @Override
    public void initializeRobotSystems() {
        SwerveConfig config = new SwerveConfig().withCanBus(SwerveDriveConstants.SWERVE_CANBUS);
        drive = new Drive(config);
        superstructure = new Superstructure();
        intake = new Intake();
    }

    @Override
    public OI createOI() {
        return new OI(drive, superstructure, intake);
    }

    @Override
    public LightsBase createLights() {
        return new Lights();
    }

    @Override
    public AutonomousMode[] getAutonomousModes() {
        return new AutonomousMode[] {
            // Add autonomous modes here like this:
            //    new AutonomousMode("NameOfAutonomousMode", () -> new MyAutonomousProcedure()),
            //
            // If your autonomous procedure has constructor arguments, you can
            // define one or more different autonomous modes with it like this:
            //    new AutonomousMode("DriveFast", () -> new DriveStraight(1.0)),
            //    new AutonomousMode("DriveSlow", () -> new DriveStraight(0.4)),
            // new AutonomousMode("FollowPoints", () -> new FollowPoints()),
            // new AutonomousMode("ReverseIntake", () -> new ReverseIntake()),
            // new AutonomousMode("ScoreHighCube", () -> new ScoreHighCube(GamePieceType.CUBE)),
            // new AutonomousMode("OnePieceExitCommunity", () -> new
            // OnePieceExitCommunity(GamePieceType.CUBE)),
            // new AutonomousMode("OnePieceExitCommunityBalance", () -> new
            // OnePieceExitCommunityBalance(GamePieceType.CUBE)),
            // new AutonomousMode(
            //        "OnePieceBalanceCube", () -> new OnePieceBalance(GamePieceType.CUBE)),
            // new AutonomousMode("FollowPointsFile", () -> new
            // FollowPoints("FollowPoints.json")),
            // //new AutonomousMode("FollowPointsH", () -> new FollowPoints(new PointDir[]{new
            // PointDir(0, 0), new PointDir(2, 0), new PointDir(1, 0), new PointDir(1, 1), new
            // PointDir(2, 1), new PointDir(0, 1)})),
            // new AutonomousMode("DoNothing", () -> new DoNothing()),
            // new AutonomousMode("FollowExamplePath", () -> new FollowPath()),
            new AutonomousMode("RotationTestAuto", () -> new TestPathAuto(drive)),
            new AutonomousMode("LoopAuto", () -> new LoopAuto(drive))
        };
    }
}
