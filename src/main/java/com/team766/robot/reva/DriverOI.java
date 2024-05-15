package com.team766.robot.reva;

import com.team766.framework.OIFragment;
import com.team766.framework.conditions.RuleEngineProvider;
import com.team766.hal.JoystickReader;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Lights;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.procedures.DriverShootNow;
import com.team766.robot.reva.procedures.DriverShootVelocityAndIntake;
import org.littletonrobotics.junction.AutoLogOutput;

public class DriverOI extends OIFragment {
    protected final Drive drive;
    protected final Shoulder shoulder;
    protected final Intake intake;
    protected final Shooter shooter;
    protected final Lights lights;
    protected final ForwardApriltagCamera forwardApriltagCamera;
    protected final JoystickReader leftJoystick;
    protected final JoystickReader rightJoystick;

    @AutoLogOutput
    protected boolean isCross = false;

    private final InlineCondition isCrossCondition = new InlineCondition();
    private final InlineCondition movingJoysticks = new InlineCondition();

    public DriverOI(
            RuleEngineProvider oi,
            Drive drive,
            Shoulder shoulder,
            Intake intake,
            Shooter shooter,
            Lights lights,
            ForwardApriltagCamera forwardApriltagCamera,
            JoystickReader leftJoystick,
            JoystickReader rightJoystick) {
        super(oi);
        this.drive = drive;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
        this.lights = lights;
        this.forwardApriltagCamera = forwardApriltagCamera;
        this.leftJoystick = leftJoystick;
        this.rightJoystick = rightJoystick;
    }

    @Override
    protected void dispatch() {

        leftJoystick
                .getButton(InputConstants.BUTTON_RESET_GYRO)
                .ifNewlyTriggering(() -> drive.setGoalBehavior(new Drive.ResetGyro()));

        leftJoystick
                .getButton(InputConstants.BUTTON_RESET_POS)
                .ifNewlyTriggering(() -> drive.setGoalBehavior(new Drive.ResetCurrentPosition()));

        // if (rightJoystick.getButton(InputConstants.BUTTON_CROSS_WHEELS).isNewlyTriggering()) {
        //     isCross = !isCross;
        // }
        // isCrossCondition.update(isCross).whileTriggering(
        //     () -> drive.setGoalBehavior(new Drive.SetCross()));

        leftJoystick
                .getButton(InputConstants.BUTTON_TARGET_SHOOTER)
                .whileTriggering(() -> new DriverShootNow(
                        drive, shoulder, shooter, intake, lights, forwardApriltagCamera));

        rightJoystick
                .getButton(InputConstants.BUTTON_START_SHOOTING_PROCEDURE)
                .whileTriggering(() -> new DriverShootVelocityAndIntake(shooter, intake));

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
        movingJoysticks
                .update(Math.abs(leftJoystickX) + Math.abs(leftJoystickY) + Math.abs(rightJoystickY)
                        > 0)
                .whileTriggering(() -> {
                    double drivingCoefficient = 1;
                    // If a button is pressed, drive is just fine adjustment
                    if (rightJoystick
                            .getButton(InputConstants.BUTTON_FINE_DRIVING)
                            .isTriggering()) {
                        drivingCoefficient = ControlConstants.FINE_DRIVING_COEFFICIENT;
                    }

                    return drive.setGoalBehavior(new Drive.FieldOrientedVelocity(
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
                                            ControlConstants.ROTATIONAL_CURVE_POWER))));
                });

        byDefault(drive.setGoalBehavior(new Drive.SetCross()));
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
