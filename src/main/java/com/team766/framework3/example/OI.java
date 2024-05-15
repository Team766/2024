package com.team766.framework3.example;

import com.team766.framework.OIBase;
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
    public static class State {
        @AutoLogOutput public PlacementPosition placementPosition;

        @AutoLogOutput public boolean isCross;
    }

    private final State state = new State();

    private final JoystickReader leftJoystick =
            RobotProvider.instance.getJoystick(this, InputConstants.LEFT_JOYSTICK);
    private final JoystickReader rightJoystick =
            RobotProvider.instance.getJoystick(this, InputConstants.RIGHT_JOYSTICK);
    private final JoystickReader boxopGamepad =
            RobotProvider.instance.getJoystick(this, InputConstants.BOXOP_GAMEPAD);

    private final InlineCondition movingJoysticks = new InlineCondition();
    private final InlineCondition isCrossCondition = new InlineCondition();

    private final Drive drive;

    public OI(Drive drive) {
        this.drive = drive;
    }

    @Override
    protected void dispatch() {
        dispatchDriver();
        dispatchBoxop();
        dispatchDebug();
    }

    public void dispatchDriver() {
        leftJoystick
                .getButton(InputConstants.BUTTON_TARGET_SHOOTER)
                .whileTriggering(() -> new DriverShootNow(null, null, null, null, null, null));

        leftJoystick
                .getButton(InputConstants.BUTTON_RESET_GYRO)
                .ifNewlyTriggering(() -> drive.setGoalBehavior(new Drive.ResetGyro()));

        leftJoystick
                .getButton(InputConstants.BUTTON_RESET_POS)
                .ifNewlyTriggering(() -> drive.setGoalBehavior(new Drive.ResetCurrentPosition()));

        // Sets the wheels to the cross position if the cross button is pressed
        rightJoystick
                .getButton(InputConstants.BUTTON_CROSS_WHEELS)
                .ifNewlyTriggering(
                        () -> {
                            state.isCross = !state.isCross;
                        });

        isCrossCondition
                .update(state.isCross)
                .whileTriggering(() -> drive.setGoalBehavior(new Drive.SetCross()));

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

        movingJoysticks
                .update(
                        Math.abs(leftJoystickX) > 0
                                || Math.abs(leftJoystickY) > 0
                                || Math.abs(rightJoystickY) > 0)
                .whileTriggering(
                        () ->
                                drive.setGoalBehavior(
                                        new Drive.FieldOrientedVelocity(
                                                leftJoystickX, leftJoystickY, rightJoystickY)));

        // TODO: shouldn't be able to do this, since it circumvents reservations
        drive.setGoal(new Drive.StopDrive());

        byDefault(drive.setGoalBehavior(new Drive.StopDrive()));

        // buttonPushed
        // 	.ifNewlyTriggering(() -> new MyBehavior());

        // byDefault(new StopIntake());
        // byDefault(new RetractWristvator());
    }

    public void dispatchBoxop() {}

    public void dispatchDebug() {}

    private static double createJoystickDeadzone(double joystickValue) {
        return Math.abs(joystickValue) > ControlConstants.JOYSTICK_DEADZONE ? joystickValue : 0;
    }
}
