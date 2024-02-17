package com.team766.robot.swerveandshoot;

import com.team766.framework.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.swerveandshoot.mechanisms.SpeakerShooterPowerCalculator;
import com.team766.robot.swerveandshoot.procedures.*;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
    private JoystickReader joystick0;
    private JoystickReader joystick1;
    private JoystickReader joystick2;

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        joystick0 = RobotProvider.instance.getJoystick(0);
        joystick1 = RobotProvider.instance.getJoystick(1);
        joystick2 = RobotProvider.instance.getJoystick(2);
    }

    public void run(final Context context) {
        while (true) {
            // wait for driver station data (and refresh it using the WPILib APIs)
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            context.takeOwnership(Robot.lights);
            context.takeOwnership(Robot.drive);

            RobotProvider.instance.refreshDriverStationData();
            // log(Robot.noteUtil.toString());
            // Add driver controls here - make sure to take/release ownership
            // of mechanisms when appropriate.

            int lightStatusNum = 4;

            if (joystick0.getButtonPressed(2)) {
                Robot.drive.resetGyro();
            }
            if (Math.abs(joystick0.getAxis(0))
                            + Math.abs(joystick0.getAxis(1))
                            + Math.abs(joystick1.getAxis(0))
                    > 0.05) {
                Robot.drive.controlRobotOriented(
                        joystick0.getAxis(0) * .2,
                        -joystick0.getAxis(1) * .2,
                        joystick1.getAxis(0) * .2);
            } else {
                Robot.drive.stopDrive();
            }

            Robot.noteUtil.test();

            // need to hold
            if (joystick0.getButton(1)) {
                try {
                    // Robot.speakerShooter.shoot();
                    // Robot.speakerShooter.shootDefault();
                    Robot.speakerShooter.goToAndScore(SpeakerShooterPowerCalculator.makerSpace1R);
                } catch (AprilTagGeneralCheckedException e) {

                }
            }

            if (joystick0.getButton(2)) {
                try {
                    Robot.speakerShooter.goToAndScore(SpeakerShooterPowerCalculator.makerSpace1L);
                } catch (AprilTagGeneralCheckedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (joystick1.getButton(1)) {
                try {

                    Robot.noteUtil.goToAndPickupNote();
                    lightStatusNum = Robot.noteUtil.getStatus();
                } catch (AprilTagGeneralCheckedException e) {
                    lightStatusNum = 2;
                }
            }

            switch (lightStatusNum) {
                case 1:
                    Robot.lights.signalRing();
                    break;
                case 2:
                    // Robot.lights.signalNoRing();
                    break;
                case 4:
                    Robot.lights.signalNotInUse();
                default:
                    Robot.lights.turnLEDsOff();
            }
        }
    }
}
