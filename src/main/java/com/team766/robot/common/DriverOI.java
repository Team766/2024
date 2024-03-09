package com.team766.robot.common;

import com.team766.framework.Context;
import com.team766.framework.OICondition;
import com.team766.hal.JoystickReader;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.common.constants.InputConstants;
import com.team766.robot.common.mechanisms.Drive;

public class DriverOI {

    protected static final double FINE_DRIVING_COEFFICIENT = 0.25;

    protected final Drive drive;
    protected final JoystickReader leftJoystick;
    protected final JoystickReader rightJoystick;
    protected double rightJoystickY = 0;
    protected double leftJoystickX = 0;
    protected double leftJoystickY = 0;
    protected boolean isCross = false;

    private final OICondition movingJoysticks;

    public DriverOI(Drive drive, JoystickReader leftJoystick, JoystickReader rightJoystick) {
        this.drive = drive;
        this.leftJoystick = leftJoystick;
        this.rightJoystick = rightJoystick;

        movingJoysticks =
                new OICondition(
                        () ->
                                !isCross
                                        && Math.abs(leftJoystickX)
                                                        + Math.abs(leftJoystickY)
                                                        + Math.abs(rightJoystickY)
                                                > 0);
    }

    public void handleOI(Context context) {

        // Negative because forward is negative in driver station
        leftJoystickX =
                -createJoystickDeadzone(leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD))
                        * ControlConstants.MAX_POSITIONAL_VELOCITY; // For fwd/rv
        // Negative because left is negative in driver station
        leftJoystickY =
                -createJoystickDeadzone(leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT))
                        * ControlConstants.MAX_POSITIONAL_VELOCITY; // For left/right
        // Negative because left is negative in driver station
        rightJoystickY =
                -createJoystickDeadzone(rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT))
                        * ControlConstants.MAX_ROTATIONAL_VELOCITY; // For steer

        if (leftJoystick.getButtonPressed(InputConstants.BUTTON_RESET_GYRO)) {
            drive.resetGyro();
        }

        if (leftJoystick.getButtonPressed(InputConstants.BUTTON_RESET_POS)) {
            drive.resetCurrentPosition();
        }

        // Sets the wheels to the cross position if the cross button is pressed
        if (rightJoystick.getButtonPressed(InputConstants.BUTTON_CROSS_WHEELS)) {
            if (!isCross) {
                context.takeOwnership(drive);
                drive.stopDrive();
                drive.setCross();
            }
            isCross = !isCross;
        }

        // Moves the robot if there are joystick inputs
        if (movingJoysticks.isTriggering()) {
            double drivingCoefficient = 1;
            // If a button is pressed, drive is just fine adjustment
            if (rightJoystick.getButton(InputConstants.BUTTON_FINE_DRIVING)) {
                drivingCoefficient = FINE_DRIVING_COEFFICIENT;
            }
            context.takeOwnership(drive);
            drive.controlFieldOriented(
                    (drivingCoefficient * leftJoystickX),
                    (drivingCoefficient * leftJoystickY),
                    (drivingCoefficient * rightJoystickY));
        } else if (movingJoysticks.isFinishedTriggering()) {
            context.takeOwnership(drive);
            drive.stopDrive();
        }
    }

    /**
     * Helper method to ignore joystick values below JOYSTICK_DEADZONE
     * @param joystickValue the value to trim
     * @return the trimmed joystick value
     */
    private double createJoystickDeadzone(double joystickValue) {
        return Math.abs(joystickValue) > ControlConstants.JOYSTICK_DEADZONE ? joystickValue : 0;
    }
}
