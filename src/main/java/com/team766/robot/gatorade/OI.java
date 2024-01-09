package com.team766.robot.gatorade;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.logging.Category;
import com.team766.logging.Severity;
import com.team766.robot.gatorade.constants.InputConstants;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.procedures.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {

    private JoystickReader leftJoystick;
    private JoystickReader rightJoystick;
    private JoystickReader boxopGamepad;
    private double rightJoystickX = 0;
    private double leftJoystickX = 0;
    private double leftJoystickY = 0;
    private boolean isCross = false;

    private static final double FINE_DRIVING_COEFFICIENT = 0.25;
    double turningValue = 0;
    boolean manualControl = true;
    PlacementPosition placementPosition = PlacementPosition.NONE;

    private RateLimiter lightsRateLimit = new RateLimiter(1.3);

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
        boxopGamepad = RobotProvider.instance.getJoystick(InputConstants.BOXOP_GAMEPAD);
    }

    public void run(Context context) {
        context.takeOwnership(Robot.drive);
        context.takeOwnership(Robot.gyro);
        context.takeOwnership(Robot.lights);

        boolean elevatorManual = false;
        boolean wristManual = false;

        while (true) {
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();

            SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());

            // Add driver controls here - make sure to take/release ownership
            // of mechanisms when appropriate.

            leftJoystickX = leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
            leftJoystickY = leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
            rightJoystickX = rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
            // Robot.drive.setGyro(-Robot.gyro.getGyroYaw());

            if (leftJoystick.getButtonPressed(InputConstants.INTAKE_OUT)) {
                new IntakeOut().run(context);
            } else if (leftJoystick.getButtonReleased(InputConstants.INTAKE_OUT)) {
                new IntakeStop().run(context);
            }

            if (leftJoystick.getButtonPressed(InputConstants.RESET_GYRO)) {
                Robot.gyro.resetGyro();
            }

            if (leftJoystick.getButtonPressed(InputConstants.RESET_POS)) {
                Robot.drive.resetCurrentPosition();
            }

            if (Math.abs(rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT)) > 0.05) {
                rightJoystickX = rightJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT) / 2;
            } else {
                rightJoystickX = 0;
            }

            if (Math.abs(leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD)) > 0.05) {
                leftJoystickY = leftJoystick.getAxis(InputConstants.AXIS_FORWARD_BACKWARD);
            } else {
                leftJoystickY = 0;
            }
            if (Math.abs(leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT)) > 0.05) {
                leftJoystickX = leftJoystick.getAxis(InputConstants.AXIS_LEFT_RIGHT);
            } else {
                leftJoystickX = 0;
            }

            // Sets the wheels to the cross position if the cross button is pressed
            if (rightJoystick.getButtonPressed(InputConstants.CROSS_WHEELS)) {
                if (!isCross) {
                    context.startAsync(new SetCross());
                }
                isCross = !isCross;
            }

            // Moves the robot if there are joystick inputs
            if (!isCross
                    && Math.abs(leftJoystickX) + Math.abs(leftJoystickY) + Math.abs(rightJoystickX)
                            > 0) {
                context.takeOwnership(Robot.drive);
                // If a button is pressed, drive is just fine adjustment
                if (leftJoystick.getButton(InputConstants.FINE_DRIVING)) {
                    Robot.drive.controlFieldOriented(
                            Math.toRadians(Robot.gyro.getGyroYaw()),
                            (leftJoystickX * FINE_DRIVING_COEFFICIENT),
                            (leftJoystickY * FINE_DRIVING_COEFFICIENT),
                            (rightJoystickX * FINE_DRIVING_COEFFICIENT));
                } else {
                    // On deafault, controls the robot field oriented
                    Robot.drive.controlFieldOriented(
                            Math.toRadians(Robot.gyro.getGyroYaw()),
                            (leftJoystickX),
                            (leftJoystickY),
                            (rightJoystickX));
                }
            } else {
                Robot.drive.stopDrive();
            }

            // Respond to boxop commands

            // first, check if the boxop is making a cone or cube selection
            if (boxopGamepad.getPOV() == InputConstants.POV_UP) {
                new GoForCones().run(context);
                setLightsForGamePiece();
            } else if (boxopGamepad.getPOV() == InputConstants.POV_DOWN) {
                new GoForCubes().run(context);
                setLightsForGamePiece();
            }

            // look for button presses to queue placement of intake/wrist/elevator superstructure
            if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_LOW)) {
                placementPosition = PlacementPosition.LOW_NODE;
                setLightsForPlacement();
            } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_MID)) {
                placementPosition = PlacementPosition.MID_NODE;
                setLightsForPlacement();
            } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HIGH)) {
                placementPosition = PlacementPosition.HIGH_NODE;
                setLightsForPlacement();
            } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HUMAN_PLAYER)) {
                placementPosition = PlacementPosition.HUMAN_PLAYER;
                setLightsForPlacement();
            }

            // look for button hold to start intake, release to idle intake
            if (boxopGamepad.getButtonPressed(InputConstants.BUTTON_INTAKE_IN)) {
                new IntakeIn().run(context);
            } else if (boxopGamepad.getButtonReleased(InputConstants.BUTTON_INTAKE_IN)) {
                new IntakeIdle().run(context);
            }

            // see if we should reset the button states
            if (boxopGamepad.getButton(InputConstants.BUTTON_RESET_STATE)) {
                // stop the intake
                new IntakeStop().run(context);
                // reset the placement position
                placementPosition = PlacementPosition.NONE;
                // reset the cone/cube selection to cones
                new GoForCones().run(context);
            }

            // look for button hold to extend intake/wrist/elevator superstructure,
            // release to retract
            if (boxopGamepad.getButtonPressed(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {
                switch (placementPosition) {
                    case NONE:
                        break;
                    case LOW_NODE:
                        context.startAsync(new ExtendWristvatorToLow());
                        break;
                    case MID_NODE:
                        context.startAsync(new ExtendWristvatorToMid());
                        break;
                    case HIGH_NODE:
                        context.startAsync(new ExtendWristvatorToHigh());
                        break;
                    case HUMAN_PLAYER:
                        context.startAsync(
                                new ExtendWristvatorToHuman(Robot.intake.getGamePieceType()));
                        break;
                    default:
                        // warn, ignore
                        log(
                                Severity.WARNING,
                                "Unexpected placement position: " + placementPosition.toString());
                        break;
                }
            } else if (boxopGamepad.getButtonReleased(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {
                context.startAsync(new RetractWristvator());
            }

            // TODO: refactor this code.  it's getting gnarly.
            // look for manual nudges
            // we only allow these if the extend elevator trigger is extended
            if (boxopGamepad.getButton(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {
                // the y axis is flipped from what we expect.  invert so up is positive, down is
                // negative.
                double elevatorNudgeAxis =
                        -1 * boxopGamepad.getAxis(InputConstants.AXIS_ELEVATOR_MOVEMENT);
                double wristNudgeAxis =
                        -1 * boxopGamepad.getAxis(InputConstants.AXIS_WRIST_MOVEMENT);

                if (boxopGamepad.getButtonPressed(
                        InputConstants.BUTTON_PLACEMENT_RESET_WRISTVATOR)) {
                    // bypass PID
                    if (Math.abs(elevatorNudgeAxis) > 0.05) {
                        elevatorManual = true;
                        context.takeOwnership(Robot.elevator);
                        Robot.elevator.nudgeNoPID(elevatorNudgeAxis);
                        context.releaseOwnership(Robot.elevator);
                    } else if (elevatorManual) {
                        context.takeOwnership(Robot.elevator);
                        Robot.elevator.stopElevator();
                        context.releaseOwnership(Robot.elevator);
                        elevatorManual = false;
                    }

                    if ((Math.abs(wristNudgeAxis) > 0.05)) {
                        wristManual = true;
                        context.takeOwnership(Robot.wrist);
                        Robot.wrist.nudgeNoPID(wristNudgeAxis);
                        context.releaseOwnership(Robot.wrist);
                    } else if (wristManual) {
                        context.takeOwnership(Robot.wrist);
                        Robot.wrist.stopWrist();
                        context.releaseOwnership(Robot.wrist);
                        elevatorManual = false;
                    }
                } else if (boxopGamepad.getButtonReleased(
                        InputConstants.BUTTON_PLACEMENT_RESET_WRISTVATOR)) {
                    context.takeOwnership(Robot.wrist);
                    context.takeOwnership(Robot.elevator);
                    Robot.wrist.resetEncoder();
                    Robot.elevator.resetEncoder();
                    context.releaseOwnership(Robot.wrist);
                    context.releaseOwnership(Robot.elevator);
                } else {
                    // look for elevator nudges
                    if (Math.abs(elevatorNudgeAxis) > 0.05) {
                        context.takeOwnership(Robot.elevator);
                        if (elevatorNudgeAxis > 0) {
                            Robot.elevator.nudgeUp();
                        } else {
                            Robot.elevator.nudgeDown();
                        }
                        context.releaseOwnership(Robot.elevator);
                    }

                    // look for wrist nudges
                    if (Math.abs(wristNudgeAxis) > 0.05) {
                        context.takeOwnership(Robot.wrist);
                        if (wristNudgeAxis > 0) {
                            Robot.wrist.nudgeUp();
                        } else {
                            Robot.wrist.nudgeDown();
                        }
                        context.releaseOwnership(Robot.wrist);
                    }
                }
            }

            if (lightsRateLimit.next()) {
                if (DriverStation.getMatchTime() > 0 && DriverStation.getMatchTime() < 10) {
                    Robot.lights.rainbow();
                } else {
                    setLightsForPlacement();
                }
            }
        }
    }

    private void setLightsForPlacement() {
        switch (placementPosition) {
            case NONE:
                Robot.lights.white();
                break;
            case LOW_NODE:
                Robot.lights.green();
                break;
            case MID_NODE:
                Robot.lights.red();
                break;
            case HIGH_NODE:
                Robot.lights.orange();
                break;
            case HUMAN_PLAYER:
                setLightsForGamePiece();
                break;
            default:
                // warn, ignore
                log(
                        Severity.WARNING,
                        "Unexpected placement position: " + placementPosition.toString());
                break;
        }

        lightsRateLimit.reset();
        lightsRateLimit.next();
    }

    private void setLightsForGamePiece() {
        if (Robot.intake.getGamePieceType() == GamePieceType.CUBE) {
            Robot.lights.purple();
        } else {
            Robot.lights.yellow();
        }

        lightsRateLimit.reset();
        lightsRateLimit.next();
    }
}
