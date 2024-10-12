package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.LaunchedContext;
import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.reva.VisionUtil.VisionSpeakerHelper;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.procedures.DriverShootNow;
import com.team766.robot.reva.procedures.DriverShootVelocityAndIntake;

public class DriverOI extends OIFragment {

    protected static final double FINE_DRIVING_COEFFICIENT = 0.25;

    protected VisionSpeakerHelper visionSpeakerHelper;
    protected final SwerveDrive drive;
    protected final Shoulder shoulder;
    protected final Intake intake;
    protected final Shooter shooter;
    protected final JoystickReader leftJoystick;
    protected final JoystickReader rightJoystick;
    protected double rightJoystickY = 0;
    protected double leftJoystickX = 0;
    protected double leftJoystickY = 0;
    protected boolean isCross = false;

    private final OICondition movingJoysticks;

    private LaunchedContext visionContext;

    public DriverOI(
            SwerveDrive drive,
            Shoulder shoulder,
            Intake intake,
            Shooter shooter,
            JoystickReader leftJoystick,
            JoystickReader rightJoystick) {
        super("DriverOI");
        this.drive = drive;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
        this.leftJoystick = leftJoystick;
        this.rightJoystick = rightJoystick;
        visionSpeakerHelper = new VisionSpeakerHelper(drive);

        movingJoysticks =
                new OICondition(
                        () ->
                                !isCross
                                        && Math.abs(leftJoystickX)
                                                        + Math.abs(leftJoystickY)
                                                        + Math.abs(rightJoystickY)
                                                > 0);
    }

    @Override
    protected void handlePre() {
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

    @Override
    protected void handleOI(Context context) {

        Robot.orin.run();
        if (leftJoystick.getButtonPressed(InputConstants.BUTTON_RESET_GYRO)) {
            drive.resetGyro();
        }

        if (leftJoystick.getButtonPressed(InputConstants.BUTTON_RESET_POS)) {
            drive.resetCurrentPosition();
        }

        // Sets the wheels to the cross position if the cross button is pressed
        // if (rightJoystick.getButtonPressed(InputConstants.BUTTON_CROSS_WHEELS)) {
        //     if (!isCross) {
        //         context.takeOwnership(drive);
        //         drive.stopDrive();
        //         drive.setCross();
        //     }
        //     isCross = !isCross;
        // }

        visionSpeakerHelper.update();

        if (leftJoystick.getButtonPressed(InputConstants.BUTTON_TARGET_SHOOTER)) {

            visionContext = context.startAsync(new DriverShootNow());

        } else if (leftJoystick.getButtonReleased(InputConstants.BUTTON_TARGET_SHOOTER)) {
            visionContext.stop();
            context.takeOwnership(drive);
            context.takeOwnership(intake);

            intake.stop();
            drive.stopDrive();

            context.releaseOwnership(drive);
            context.releaseOwnership(intake);
        }

        if (rightJoystick.getButtonPressed(InputConstants.BUTTON_START_SHOOTING_PROCEDURE)) {

            visionContext = context.startAsync(new DriverShootVelocityAndIntake());

        } else if (rightJoystick.getButtonReleased(
                InputConstants.BUTTON_START_SHOOTING_PROCEDURE)) {

            visionContext.stop();

            context.takeOwnership(intake);
            intake.stop();
            context.releaseOwnership(intake);
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
                    (drivingCoefficient
                            * curvedJoystickPower(
                                    leftJoystickX, ControlConstants.TRANSLATIONAL_CURVE_POWER)),
                    (drivingCoefficient
                            * curvedJoystickPower(
                                    leftJoystickY, ControlConstants.TRANSLATIONAL_CURVE_POWER)),
                    (drivingCoefficient
                            * curvedJoystickPower(
                                    rightJoystickY, ControlConstants.ROTATIONAL_CURVE_POWER)));
        } else if (movingJoysticks.isFinishedTriggering()) {
            context.takeOwnership(drive);
            drive.stopDrive();
            drive.setCross();
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

    private double curvedJoystickPower(double value, double power) {
        return Math.signum(value) * Math.pow(Math.abs(value), power);
    }
}
