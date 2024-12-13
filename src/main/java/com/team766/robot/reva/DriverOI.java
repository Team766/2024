package com.team766.robot.reva;

import static com.team766.framework3.RulePersistence.*;

import com.team766.framework3.Conditions;
import com.team766.framework3.Rule;
import com.team766.hal.JoystickReader;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.ArmAndClimber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.procedures.DriverShootNow;
import com.team766.robot.reva.procedures.DriverShootVelocityAndIntake;
import java.util.Set;

public class DriverOI {
    public DriverOI(
            OI oi,
            JoystickReader leftJoystick,
            JoystickReader rightJoystick,
            SwerveDrive drive,
            ArmAndClimber ss,
            Intake intake) {
        leftJoystick.setAllAxisDeadzone(ControlConstants.JOYSTICK_DEADZONE);
        rightJoystick.setAllAxisDeadzone(ControlConstants.JOYSTICK_DEADZONE);

        oi.addRule(
                Rule.create(
                                "Reset Gyro",
                                () -> leftJoystick.getButton(InputConstants.BUTTON_RESET_GYRO))
                        .withOnTriggeringProcedure(ONCE, Set.of(drive), () -> drive.resetGyro()));

        oi.addRule(
                Rule.create(
                                "Reset Pos",
                                () -> leftJoystick.getButton(InputConstants.BUTTON_RESET_POS))
                        .withOnTriggeringProcedure(
                                ONCE, Set.of(drive), () -> drive.resetCurrentPosition()));

        oi.addRule(
                Rule.create(
                                "Cross wheels",
                                new Conditions.Toggle(
                                        () ->
                                                rightJoystick.getButton(
                                                        InputConstants.BUTTON_CROSS_WHEELS)))
                        .withOnTriggeringProcedure(
                                ONCE_AND_HOLD,
                                Set.of(drive),
                                () -> drive.setRequest(new SwerveDrive.SetCross())));

        oi.addRule(
                Rule.create(
                                "Target Shooter",
                                () -> leftJoystick.getButton(InputConstants.BUTTON_TARGET_SHOOTER))
                        .withOnTriggeringProcedure(
                                ONCE_AND_HOLD, () -> new DriverShootNow(drive, ss, intake)));

        oi.addRule(
                Rule.create(
                                "Start Shooting",
                                () ->
                                        rightJoystick.getButton(
                                                InputConstants.BUTTON_START_SHOOTING_PROCEDURE))
                        .withOnTriggeringProcedure(
                                ONCE_AND_HOLD, () -> new DriverShootVelocityAndIntake(intake)));

        // Moves the robot if there are joystick inputs
        oi.addRule(
                Rule.create(
                                "Move robot",
                                () ->
                                        leftJoystick.isAxisMoved(
                                                        InputConstants.AXIS_FORWARD_BACKWARD)
                                                || leftJoystick.isAxisMoved(
                                                        InputConstants.AXIS_LEFT_RIGHT)
                                                || rightJoystick.isAxisMoved(
                                                        InputConstants.AXIS_LEFT_RIGHT))
                        .withOnTriggeringProcedure(
                                REPEATEDLY,
                                Set.of(drive),
                                () -> {
                                    // If a button is pressed, drive is just fine adjustment
                                    final double drivingCoefficient =
                                            rightJoystick.getButton(
                                                            InputConstants.BUTTON_FINE_DRIVING)
                                                    ? ControlConstants.FINE_DRIVING_COEFFICIENT
                                                    : 1;
                                    // For fwd/rv
                                    // Negative because forward is negative in driver station
                                    final double leftJoystickX =
                                            -leftJoystick.getAxis(
                                                            InputConstants.AXIS_FORWARD_BACKWARD)
                                                    * ControlConstants.MAX_TRANSLATIONAL_VELOCITY;
                                    // For left/right
                                    // Negative because left is negative in driver station
                                    final double leftJoystickY =
                                            -leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT)
                                                    * ControlConstants.MAX_TRANSLATIONAL_VELOCITY;
                                    // For steer
                                    // Negative because left is negative in driver station
                                    final double rightJoystickY =
                                            -rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT)
                                                    * ControlConstants.MAX_ROTATIONAL_VELOCITY;

                                    drive.setRequest(
                                            new SwerveDrive.FieldOrientedVelocity(
                                                    (drivingCoefficient
                                                            * curvedJoystickPower(
                                                                    leftJoystickX,
                                                                    ControlConstants
                                                                            .TRANSLATIONAL_CURVE_POWER)),
                                                    (drivingCoefficient
                                                            * curvedJoystickPower(
                                                                    leftJoystickY,
                                                                    ControlConstants
                                                                            .TRANSLATIONAL_CURVE_POWER)),
                                                    (drivingCoefficient
                                                            * curvedJoystickPower(
                                                                    rightJoystickY,
                                                                    ControlConstants
                                                                            .ROTATIONAL_CURVE_POWER))));
                                }));
    }

    private static double curvedJoystickPower(double value, double power) {
        return Math.signum(value) * Math.pow(Math.abs(value), power);
    }
}
