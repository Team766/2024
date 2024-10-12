package com.team766.robot.reva;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Lights;
import com.team766.robot.reva.mechanisms.NoteCamera;
import com.team766.robot.reva.mechanisms.Orin;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    public static SwerveDrive drive;
    public static Climber climber;
    public static Shoulder shoulder;
    public static Intake intake;
    public static Shooter shooter;
    // not yet initialized, until we have the camera on the robot and test it.
    public static ForwardApriltagCamera forwardApriltagCamera;
    public static NoteCamera noteCamera;
    public static Lights lights;
    public static Orin orin;

    @Override
    public void initializeMechanisms() {
        SwerveConfig config = new SwerveConfig();
        lights = new Lights();
        drive = new SwerveDrive(config);
        climber = new Climber();
        shoulder = new Shoulder();
        intake = new Intake();
        shooter = new Shooter();
        noteCamera = new NoteCamera();
        try {
            forwardApriltagCamera = new ForwardApriltagCamera();
        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
        }
        orin = new Orin();
    }

    @Override
    public Procedure createOI() {
        return new OI();
    }

    @Override
    public AutonomousMode[] getAutonomousModes() {
        return AutonomousModes.AUTONOMOUS_MODES;
    }
}
