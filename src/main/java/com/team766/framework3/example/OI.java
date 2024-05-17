package com.team766.framework3.example;

import com.team766.framework.OIBase;
import com.team766.framework.conditions.Guarded;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.PlacementPosition;
import com.team766.robot.gatorade.constants.InputConstants;
import com.team766.robot.gatorade.procedures.*;
import com.team766.robot.reva.procedures.DriverShootNow;
import org.littletonrobotics.junction.AutoLogOutput;

public class OI extends OIBase {
    private final JoystickReader leftJoystick =
            RobotProvider.instance.getJoystick(this, InputConstants.LEFT_JOYSTICK);
    private final JoystickReader rightJoystick =
            RobotProvider.instance.getJoystick(this, InputConstants.RIGHT_JOYSTICK);
    private final JoystickReader boxopGamepad =
            RobotProvider.instance.getJoystick(this, InputConstants.BOXOP_GAMEPAD);

    private final Guarded<Drive> drive;

    @AutoLogOutput
    private PlacementPosition placementPosition = PlacementPosition.NONE;

    @AutoLogOutput
    private boolean isCross = false;

    public OI(Drive drive) {
        this.drive = guard(drive);
    }

    @Override
    protected void dispatch() {
        dispatchDriver();
        dispatchBoxop();
        dispatchDebug();
    }

    public void dispatchDriver() {
        if (leftJoystick.getButton(InputConstants.BUTTON_TARGET_SHOOTER).isTriggering()) {
            tryRunning(() -> new DriverShootNow(null, null, null, null, null));
        }

        if (leftJoystick.getButton(InputConstants.BUTTON_RESET_GYRO).isNewlyTriggering()) {
            tryRunning(() -> reserve(drive).resetGyro());
        }

        if (leftJoystick.getButton(InputConstants.BUTTON_RESET_POS).isNewlyTriggering()) {
            tryReserving(drive, drive -> drive.resetCurrentPosition());
        }

        // Sets the wheels to the cross position if the cross button is pressed
        if (rightJoystick.getButton(InputConstants.BUTTON_CROSS_WHEELS).isNewlyTriggering()) {
            isCross = !isCross;
        }

        if (isCross) {
            tryRunning(() -> reserve(drive).setGoal(new Drive.SetCross()));
        }

        final double leftJoystickX =
                -createJoystickDeadzone(leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD))
                        * ControlConstants.MAX_POSITIONAL_VELOCITY; // For fwd/rv
        // Negative because left is negative in driver station
        final double leftJoystickY =
                -createJoystickDeadzone(leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT))
                        * ControlConstants.MAX_POSITIONAL_VELOCITY; // For left/right
        // Negative because left is negative in driver station
        final double rightJoystickY =
                -createJoystickDeadzone(rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT))
                        * ControlConstants.MAX_ROTATIONAL_VELOCITY; // For steer

        if (Math.abs(leftJoystickX) > 0
                || Math.abs(leftJoystickY) > 0
                || Math.abs(rightJoystickY) > 0) {
            tryRunning(() -> reserve(drive)
                    .setGoal(new Drive.FieldOrientedVelocity(
                            leftJoystickX, leftJoystickY, rightJoystickY)));
        }

        byDefault(() -> reserve(drive).setGoal(new Drive.StopDrive()));
    }

    public void dispatchBoxop() {}

    public void dispatchDebug() {}

    private static double createJoystickDeadzone(double joystickValue) {
        return Math.abs(joystickValue) > ControlConstants.JOYSTICK_DEADZONE ? joystickValue : 0;
    }
}
