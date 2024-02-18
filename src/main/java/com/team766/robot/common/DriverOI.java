package com.team766.robot.common;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.robot.common.constants.InputConstants;
import com.team766.robot.common.mechanisms.Drive;

public class DriverOI {

    protected static final double FINE_DRIVING_COEFFICIENT = 0.25;

    protected final Drive drive;
    protected final JoystickReader leftJoystick;
    protected final JoystickReader rightJoystick;
    protected double rightJoystickX = 0;
    protected double leftJoystickX = 0;
    protected double leftJoystickY = 0;
    protected boolean isCross = false;

    public DriverOI(Drive drive, JoystickReader leftJoystick, JoystickReader rightJoystick) {
        this.drive = drive;
        this.leftJoystick = leftJoystick;
        this.rightJoystick = rightJoystick;
    }

    public void handleOI(Context context) {
        context.takeOwnership(drive);

        try {
            leftJoystickX = leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
            leftJoystickY = leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
            rightJoystickX = rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);

            if (leftJoystick.getButtonPressed(InputConstants.BUTTON_RESET_GYRO)) {
                drive.resetGyro();
            }

            if (leftJoystick.getButtonPressed(InputConstants.BUTTON_RESET_POS)) {
                drive.resetCurrentPosition();
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
            if (rightJoystick.getButtonPressed(InputConstants.BUTTON_CROSS_WHEELS)) {
                if (!isCross) {
                    drive.stopDrive();
                    drive.setCross();
                }
                isCross = !isCross;
            }

            // Moves the robot if there are joystick inputs
            if (!isCross
                    && Math.abs(leftJoystickX) + Math.abs(leftJoystickY) + Math.abs(rightJoystickX)
                            > 0) {
                double drivingCoefficient = 1;
                // If a button is pressed, drive is just fine adjustment
                if (rightJoystick.getButton(InputConstants.BUTTON_FINE_DRIVING)) {
                    drivingCoefficient = FINE_DRIVING_COEFFICIENT;
                }
                drive.controlFieldOriented(
                        (drivingCoefficient * leftJoystickX),
                        -(drivingCoefficient * leftJoystickY),
                        (drivingCoefficient * rightJoystickX));
            } else {
                drive.stopDrive();
            }
        } finally {
            context.releaseOwnership(drive);
        }
    }
}
