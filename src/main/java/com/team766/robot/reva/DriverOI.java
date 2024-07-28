package com.team766.robot.reva;

import static com.team766.framework.resources.Guarded.guard;

import com.team766.framework.OIFragment;
import com.team766.framework.resources.Guarded;
import com.team766.hal.JoystickReader;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Superstructure;
import com.team766.robot.reva.procedures.DriverShootNow;
import com.team766.robot.reva.procedures.DriverShootVelocityAndIntake;
import org.littletonrobotics.junction.AutoLogOutput;

public class DriverOI extends OIFragment {
    protected final JoystickReader leftJoystick;
    protected final JoystickReader rightJoystick;
    protected final Guarded<Drive> drive;
    protected final Guarded<Superstructure> ss;
    protected final Guarded<Intake> intake;

    @AutoLogOutput
    protected boolean isCross = false;

    public DriverOI(
            OI oi,
            JoystickReader leftJoystick,
            JoystickReader rightJoystick,
            Drive drive,
            Superstructure ss,
            Intake intake) {
        super(oi);
        this.leftJoystick = leftJoystick;
        this.rightJoystick = rightJoystick;
        this.drive = guard(drive);
        this.ss = guard(ss);
        this.intake = guard(intake);
    }

    @Override
    protected void dispatch() {

        if (leftJoystick.getButton(InputConstants.BUTTON_RESET_GYRO)) {
            onceAvailable(drive, (Drive drive) -> drive.resetGyro());
        }

        if (leftJoystick.getButton(InputConstants.BUTTON_RESET_POS)) {
            onceAvailable(drive, (Drive drive) -> drive.resetCurrentPosition());
        }

        if (rightJoystick.getButton(InputConstants.BUTTON_CROSS_WHEELS)) {
            once(() -> isCross = !isCross);
        }
        if (isCross) {
            whileAvailable(drive, (Drive drive) -> drive.setGoal(new Drive.SetCross()));
        }

        if (leftJoystick.getButton(InputConstants.BUTTON_TARGET_SHOOTER)) {
            whileAvailable(
                    drive,
                    ss,
                    intake,
                    (Drive drive, Superstructure ss, Intake intake) ->
                            new DriverShootNow(drive, ss, intake));
        }

        if (rightJoystick.getButton(InputConstants.BUTTON_START_SHOOTING_PROCEDURE)) {
            whileAvailable(intake, (Intake intake) -> new DriverShootVelocityAndIntake(intake));
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
                    rightJoystick.getButton(InputConstants.BUTTON_FINE_DRIVING)
                            ? ControlConstants.FINE_DRIVING_COEFFICIENT
                            : 1;

            whileAvailable(
                    drive,
                    (Drive drive) -> drive.setGoal(new Drive.FieldOrientedVelocity(
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

        byDefault(drive, (Drive drive) -> drive.setGoal(new Drive.SetCross()));
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
