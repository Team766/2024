package com.team766.robot.gatorade;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.logging.Category;
import com.team766.logging.Severity;
import com.team766.robot.common.DriverOI;
import com.team766.robot.gatorade.constants.ControlConstants;
import com.team766.robot.gatorade.constants.InputConstants;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.procedures.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.ShuffleboardUtil.ShuffleboardUtil;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {

    private JoystickReader leftJoystick;
    private JoystickReader rightJoystick;
    private JoystickReader boxopGamepad;
    private double rightJoystickY = 0;
    private double leftJoystickX = 0;
    private double leftJoystickY = 0;
    private boolean isCross = false;
    private final DriverOI driverOI;

    double turningValue = 0;
    boolean manualControl = true;
    PlacementPosition placementPosition = PlacementPosition.NONE;

    private RateLimiter lightsRateLimit = new RateLimiter(1.3);

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
        boxopGamepad = RobotProvider.instance.getJoystick(InputConstants.BOXOP_GAMEPAD);

        driverOI = new DriverOI(Robot.drive, leftJoystick, rightJoystick);
    }

    public void run(Context context) {
        context.takeOwnership(Robot.lights);

        boolean elevatorManual = false;
        boolean wristManual = false;

        while (true) {
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();

            ShuffleboardUtil.putString("Alliance", DriverStation.getAlliance().toString());

            // Add driver controls here - make sure to take/release ownership
            // of mechanisms when appropriate.

            // Driver OI: take input from left, right joysticks.  control drive.
            driverOI.handleOI(context);

            if (leftJoystick.getButtonPressed(InputConstants.INTAKE_OUT)) {
                new IntakeOut().run(context);
            } else if (leftJoystick.getButtonReleased(InputConstants.INTAKE_OUT)) {
                new IntakeStop().run(context);
            }

            // Respond to boxop commands

            // first, check if the boxop is making a cone or cube selection
            if (boxopGamepad.getPOV() == InputConstants.POV_UP) {
                new GoForCones().run(context);
                setLightsForGamePiece();
                ShuffleboardUtil.putBoolean("Game Piece", true);
            } else if (boxopGamepad.getPOV() == InputConstants.POV_DOWN) {
                new GoForCubes().run(context);
                setLightsForGamePiece();
                ShuffleboardUtil.putBoolean("Game Piece", false);
            }

            // look for button presses to queue placement of intake/wrist/elevator superstructure
            if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_NONE)) {
                placementPosition = PlacementPosition.NONE;
                // setLightsForPlacement();
            } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_LOW)) {
                placementPosition = PlacementPosition.LOW_NODE;
                // setLightsForPlacement();
            } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_MID)) {
                placementPosition = PlacementPosition.MID_NODE;
                // setLightsForPlacement();
            } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HIGH)) {
                placementPosition = PlacementPosition.HIGH_NODE;
                // setLightsForPlacement();
            } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HUMAN_PLAYER)) {
                placementPosition = PlacementPosition.HUMAN_PLAYER;
                // setLightsForPlacement();
            }

            // look for button hold to start intake, release to idle intake
            if (boxopGamepad.getButtonPressed(InputConstants.BUTTON_INTAKE_IN)) {
                new IntakeIn().run(context);
            } else if (boxopGamepad.getButtonReleased(InputConstants.BUTTON_INTAKE_IN)) {
                new IntakeIdle().run(context);
            } else if (boxopGamepad.getButton(InputConstants.BUTTON_INTAKE_STOP)) {
                new IntakeStop().run(context);
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
                                new ExtendToHumanWithIntake(Robot.intake.getGamePieceType()));
                        break;
                    default:
                        // warn, ignore
                        log(
                                Severity.WARNING,
                                "Unexpected placement position: " + placementPosition.toString());
                        break;
                }
            } else if (boxopGamepad.getButtonReleased(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {
                if (placementPosition == PlacementPosition.HUMAN_PLAYER) {
                    context.startAsync(new RetractWristvatorIdleIntake());
                } else {
                    context.startAsync(new RetractWristvator());
                }
            }

            // look for manual nudges
            // we only allow these if the extend elevator trigger is extended
            if (boxopGamepad.getButton(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {

                // look for elevator nudges
                double elevatorNudgeAxis =
                        -1 * boxopGamepad.getAxis(InputConstants.AXIS_ELEVATOR_MOVEMENT);
                if (Math.abs(elevatorNudgeAxis) > 0.05) {
                    // elevatorManual = true;
                    context.takeOwnership(Robot.elevator);
                    // Robot.elevator.nudgeNoPID(elevatorNudgeAxis);
                    if (elevatorNudgeAxis > 0) {
                        Robot.elevator.nudgeUp();
                    } else {
                        Robot.elevator.nudgeDown();
                    }
                    context.releaseOwnership(Robot.elevator);
                } else if (false && elevatorManual) {
                    Robot.elevator.stopElevator();
                    elevatorManual = false;
                }

                // look for wrist nudges
                double wristNudgeAxis =
                        -1 * boxopGamepad.getAxis(InputConstants.AXIS_WRIST_MOVEMENT);
                if (Math.abs(wristNudgeAxis) > 0.05) {
                    // wristManual = true;
                    context.takeOwnership(Robot.wrist);
                    // Robot.wrist.nudgeNoPID(wristNudgeAxis);
                    if (wristNudgeAxis > 0) {
                        Robot.wrist.nudgeUp();
                    } else {
                        Robot.wrist.nudgeDown();
                    }
                    context.releaseOwnership(Robot.wrist);
                } else if (false && wristManual) {
                    Robot.wrist.stopWrist();
                    wristManual = true;
                }
            }

            if (lightsRateLimit.next()) {
                if (DriverStation.getMatchTime() > 0 && DriverStation.getMatchTime() < 17) {
                    Robot.lights.rainbow();
                } else {
                    setLightsForGamePiece();
                }
            }
        }
    }

    private void setLightsForPlacement() {
        switch (placementPosition) {
                // case NONE:
                // 	Robot.lights.white();
                // 	break;
                // case LOW_NODE:
                // 	Robot.lights.green();
                // 	break;
                // case MID_NODE:
                // 	Robot.lights.red();
                // 	break;
                // case HIGH_NODE:
                // 	Robot.lights.orange();
                // 	break;
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

    /**
     * Helper method to ignore joystick values below JOYSTICK_DEADZONE
     * @param joystickValue the value to trim
     * @return the trimmed joystick value
     */
    private double createJoystickDeadzone(double joystickValue) {
        return Math.abs(joystickValue) > ControlConstants.JOYSTICK_DEADZONE ? joystickValue : 0;
    }
}
