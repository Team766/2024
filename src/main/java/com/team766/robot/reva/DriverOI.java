package com.team766.robot.reva;

import com.team766.framework.OIFragment;
import com.team766.framework.conditions.Guarded;
import com.team766.framework.conditions.RuleEngineProvider;
import com.team766.hal.JoystickReader;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.procedures.DriverShootNow;
import com.team766.robot.reva.procedures.DriverShootVelocityAndIntake;
import org.littletonrobotics.junction.AutoLogOutput;

public class DriverOI extends OIFragment {
    protected final Guarded<Drive> drive;
    protected final Guarded<Shoulder> shoulder;
    protected final Guarded<Intake> intake;
    protected final Guarded<Shooter> shooter;
    protected final Guarded<ForwardApriltagCamera> forwardApriltagCamera;
    protected final JoystickReader leftJoystick;
    protected final JoystickReader rightJoystick;

    @AutoLogOutput
    protected boolean isCross = false;

    public DriverOI(
            RuleEngineProvider oi,
            Guarded<Drive> drive,
            Guarded<Shoulder> shoulder,
            Guarded<Intake> intake,
            Guarded<Shooter> shooter,
            Guarded<ForwardApriltagCamera> forwardApriltagCamera,
            JoystickReader leftJoystick,
            JoystickReader rightJoystick) {
        super(oi);
        this.drive = drive;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
        this.forwardApriltagCamera = forwardApriltagCamera;
        this.leftJoystick = leftJoystick;
        this.rightJoystick = rightJoystick;
    }

    @Override
    protected void dispatch() {

        if (leftJoystick.getButton(InputConstants.BUTTON_RESET_GYRO).isNewlyTriggering()) {
            tryRunning(() -> reserve(drive).resetGyro());
        }

        if (leftJoystick.getButton(InputConstants.BUTTON_RESET_POS).isNewlyTriggering()) {
            tryRunning(() -> reserve(drive).resetCurrentPosition());
        }

        if (rightJoystick.getButton(InputConstants.BUTTON_CROSS_WHEELS).isNewlyTriggering()) {
            isCross = !isCross;
        }
        if (isCross) {
            tryRunning(() -> reserve(drive).setGoal(new Drive.SetCross()));
        }

        if (leftJoystick.getButton(InputConstants.BUTTON_TARGET_SHOOTER).isTriggering()) {
            tryRunning(() -> new DriverShootNow(
                    reserve(drive),
                    reserve(shoulder),
                    useStatus(shooter),
                    reserve(intake),
                    reserve(forwardApriltagCamera)));
        }

        if (rightJoystick
                .getButton(InputConstants.BUTTON_START_SHOOTING_PROCEDURE)
                .isTriggering()) {
            tryRunning(() -> new DriverShootVelocityAndIntake(useStatus(shooter), reserve(intake)));
        }

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

        // Moves the robot if there are joystick inputs
        if (Math.abs(leftJoystickX) + Math.abs(leftJoystickY) + Math.abs(rightJoystickY) > 0) {
            // If a button is pressed, drive is just fine adjustment
            final double drivingCoefficient =
                    rightJoystick.getButton(InputConstants.BUTTON_FINE_DRIVING).isTriggering()
                            ? ControlConstants.FINE_DRIVING_COEFFICIENT
                            : 1;

            tryRunning(() -> reserve(drive)
                    .setGoal(new Drive.FieldOrientedVelocity(
                            (drivingCoefficient
                                    * curvedJoystickPower(
                                            leftJoystickX,
                                            ControlConstants.TRANSLATIONAL_CURVE_POWER)),
                            (drivingCoefficient
                                    * curvedJoystickPower(
                                            leftJoystickY,
                                            ControlConstants.TRANSLATIONAL_CURVE_POWER)),
                            (drivingCoefficient
                                    * curvedJoystickPower(
                                            rightJoystickY,
                                            ControlConstants.ROTATIONAL_CURVE_POWER)))));
        }

        byDefault(() -> reserve(drive).setGoal(new Drive.SetCross()));
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
