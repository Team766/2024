package com.team766.robot.swerveandshoot;

import com.team766.framework.AutonomousMode;
import com.team766.framework.LightsBase;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.swerveandshoot.constants.SwerveDriveConstants;
import com.team766.robot.swerveandshoot.mechanisms.*;
import com.team766.robot.swerveandshoot.procedures.*;

public class Robot implements RobotConfigurator {
    @Override
    public void initializeRobotSystems() {
        SwerveConfig config = new SwerveConfig().withCanBus(SwerveDriveConstants.SWERVE_CANBUS);
        addRobotSystem(new TempPickerUpper());
        addRobotSystem(new TempShooter());
        addRobotSystem(new Drive(config));
        addRobotSystem(new NoteCamera());
        addRobotSystem(new ForwardApriltagCamera());
    }

    @Override
    public OI createOI() {
        return new OI();
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

            new AutonomousMode("DoNothing", () -> new DoNothing()),
        };
    }
}
