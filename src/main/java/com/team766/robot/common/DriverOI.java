package com.team766.robot.common;

import com.team766.framework.OIFragment;
import com.team766.framework.conditions.RuleEngineProvider;
import com.team766.hal.JoystickReader;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.common.constants.InputConstants;
import com.team766.robot.common.mechanisms.Drive;
import org.littletonrobotics.junction.AutoLogOutput;

public class DriverOI extends OIFragment {

    public class State {
        @AutoLogOutput
        protected boolean isCross = false;
    }

    protected final State state = new State();
    protected final Drive drive;
    protected final JoystickReader leftJoystick;
    protected final JoystickReader rightJoystick;

    public DriverOI(
            RuleEngineProvider oi,
            Drive drive,
            JoystickReader leftJoystick,
            JoystickReader rightJoystick) {
        super(oi);
        this.drive = drive;
        this.leftJoystick = leftJoystick;
        this.rightJoystick = rightJoystick;
    }

    protected void dispatch() {
        // Negative because forward is negative in driver station
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

        new Condition(leftJoystick.getButton(InputConstants.BUTTON_RESET_GYRO)) {
            protected void ifNewlyTriggering() {
                runIfAvailable(() -> drive.setGoalBehavior(new Drive.ResetGyro()));
            }
        };

        new Condition(leftJoystick.getButton(InputConstants.BUTTON_RESET_POS)) {
            protected void ifNewlyTriggering() {
                runIfAvailable(() -> drive.setGoalBehavior(new Drive.ResetCurrentPosition()));
            }
        };

        // Sets the wheels to the cross position if the cross button is pressed
        new Condition(rightJoystick.getButton(InputConstants.BUTTON_CROSS_WHEELS)) {
            protected void ifNewlyTriggering() {
                state.isCross = !state.isCross;
            }
        };

        new Condition(state.isCross) {
            protected void ifTriggering() {
                runIfAvailable(() -> drive.setGoalBehavior(new Drive.SetCross()));
            }
        };

        // Moves the robot if there are joystick inputs
        new Condition(
                Math.abs(leftJoystickX) + Math.abs(leftJoystickY) + Math.abs(rightJoystickY) > 0) {
            protected void ifTriggering() {
                // If a button is pressed, drive is just fine adjustment
                final double drivingCoefficient =
                        rightJoystick.getButton(InputConstants.BUTTON_FINE_DRIVING)
                                ? ControlConstants.FINE_DRIVING_COEFFICIENT
                                : 1;
                runIfAvailable(() -> drive.setGoalBehavior(new Drive.FieldOrientedVelocity(
                        (drivingCoefficient
                                * curvedJoystickPower(
                                        leftJoystickX, ControlConstants.TRANSLATIONAL_CURVE_POWER)),
                        (drivingCoefficient
                                * curvedJoystickPower(
                                        leftJoystickY, ControlConstants.TRANSLATIONAL_CURVE_POWER)),
                        (drivingCoefficient
                                * curvedJoystickPower(
                                        rightJoystickY,
                                        ControlConstants.ROTATIONAL_CURVE_POWER)))));
            }
        };

        byDefault(() -> drive.setGoalBehavior(new Drive.SetCross()));
    }

    /**
     * Helper method to ignore joystick values below JOYSTICK_DEADZONE
     * @param joystickValue the value to trim
     * @return the trimmed joystick value
     */
    private static double createJoystickDeadzone(double joystickValue) {
        return Math.abs(joystickValue) > ControlConstants.JOYSTICK_DEADZONE ? joystickValue : 0;
    }

    private static double curvedJoystickPower(double value, double power) {
        return Math.signum(value) * Math.pow(Math.abs(value), power);
    }
}
