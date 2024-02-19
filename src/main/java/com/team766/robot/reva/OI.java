package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.reva.constants.InputConstants;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {

    private JoystickReader leftJoystick;
    private JoystickReader rightJoystick;
    private double rightJoystickX = 0;
    private double leftJoystickX = 0;
    private double leftJoystickY = 0;
    private boolean isCross = false;

    private static final double FINE_DRIVING_COEFFICIENT = 0.25;

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
    }

    public void run(Context context) {
        context.takeOwnership(Robot.drive);

        while (true) {
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();

            SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());

            // Add driver controls here - make sure to take/release ownership
            // of mechanisms when appropriate.

            leftJoystickX = leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
            leftJoystickY = leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
            rightJoystickX = rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
            // Robot.drive.setGyro(-Robot.gyro.getGyroYaw());

            if (leftJoystick.getButtonPressed(InputConstants.RESET_GYRO)) {
                Robot.drive.resetGyro();
            }

            if (leftJoystick.getButtonPressed(InputConstants.RESET_POS)) {
                Robot.drive.resetCurrentPosition();
            }

            if (Math.abs(rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT)) > 0.05) {
                rightJoystickX = rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT) / 2;
            } else {
                rightJoystickX = 0;
            }

            if (Math.abs(leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD)) > 0.05) {
                leftJoystickY = leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
            } else {
                leftJoystickY = 0;
            }
            if (Math.abs(leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT)) > 0.05) {
                leftJoystickX = leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
            } else {
                leftJoystickX = 0;
            }

            // Sets the wheels to the cross position if the cross button is pressed
            if (rightJoystick.getButtonPressed(InputConstants.CROSS_WHEELS)) {
                if (!isCross) {
                    Robot.drive.stopDrive();
                    Robot.drive.setCross();
                }
                isCross = !isCross;
            }

            // Moves the robot if there are joystick inputs
            if (!isCross
                    && Math.abs(leftJoystickX) + Math.abs(leftJoystickY) + Math.abs(rightJoystickX)
                            > 0) {
                context.takeOwnership(Robot.drive);
                double drivingCoefficient = 1;
                // If a button is pressed, drive is just fine adjustment
                if (rightJoystick.getButton(InputConstants.FINE_DRIVING)) {
                    drivingCoefficient = FINE_DRIVING_COEFFICIENT;
                }
                Robot.drive.controlFieldOriented(
                        (drivingCoefficient * leftJoystickX),
                        -(drivingCoefficient * leftJoystickY),
                        (drivingCoefficient * rightJoystickX));
            } else {
                Robot.drive.stopDrive();
            }
        }
    }
}
