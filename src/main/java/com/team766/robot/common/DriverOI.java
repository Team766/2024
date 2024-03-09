package com.team766.robot.common;

import com.team766.framework.Context;
import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.common.constants.InputConstants;
import com.team766.robot.common.mechanisms.Drive;

public class DriverOI extends OIFragment {

    protected static final double FINE_DRIVING_COEFFICIENT = 0.25;

    protected final Drive drive;
    protected final JoystickReader leftJoystick;
    protected final JoystickReader rightJoystick;
    protected double rightJoystickY = 0;
    protected double leftJoystickX = 0;
    protected double leftJoystickY = 0;
    protected boolean isCross = false;

    public DriverOI(Drive drive, JoystickReader leftJoystick, JoystickReader rightJoystick) {
        super("DriverOI");
        this.drive = drive;
        this.leftJoystick = leftJoystick;
        this.rightJoystick = rightJoystick;

        addRule(
                () -> leftJoystick.getButtonPressed(InputConstants.BUTTON_RESET_GYRO),
                (context) -> drive.resetGyro(),
                null);
        addRule(
                () -> leftJoystick.getButtonPressed(InputConstants.BUTTON_RESET_POS),
                (context) -> drive.resetCurrentPosition(),
                null);

        addRule(
                () -> rightJoystick.getButtonPressed(InputConstants.BUTTON_CROSS_WHEELS),
                (context) -> crossWheels(context),
                null);
        addRule(
                () ->
                        (!isCross
                                && Math.abs(leftJoystickX)
                                                + Math.abs(leftJoystickY)
                                                + Math.abs(rightJoystickY)
                                        > 0),
                (context) -> drive(context),
                (context) -> driveDone(context));
    }

    protected void pre(Context context) {
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
    }

    private void crossWheels(Context context) {
        if (!isCross) {
            context.takeOwnership(drive);
            drive.stopDrive();
            drive.setCross();
            context.releaseOwnership(drive);
        }
        isCross = !isCross;
    }

    private void drive(Context context) {
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
        context.releaseOwnership(drive);
    }

    private void driveDone(Context context) {
        context.takeOwnership(drive);
        drive.stopDrive();
        context.releaseOwnership(drive);
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
