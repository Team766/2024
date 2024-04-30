package com.team766.robot.gatorade;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.logging.Category;
import com.team766.logging.Severity;
import com.team766.robot.common.DriverOI;
import com.team766.robot.common.mechanisms.*;
import com.team766.robot.gatorade.constants.ControlConstants;
import com.team766.robot.gatorade.constants.InputConstants;
import com.team766.robot.gatorade.mechanisms.*;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.procedures.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {

    private final JoystickReader leftJoystick;
    private final JoystickReader rightJoystick;
    private final JoystickReader boxopGamepad;
    private final Drive drive;
    private final Shoulder shoulder;
    private final Elevator elevator;
    private final Wrist wrist;
    private final Intake intake;
    private final Lights lights;
    private double rightJoystickY = 0;
    private double leftJoystickX = 0;
    private double leftJoystickY = 0;
    private boolean isCross = false;
    private final DriverOI driverOI;

    double turningValue = 0;
    boolean manualControl = true;
    PlacementPosition placementPosition = PlacementPosition.NONE;

    private RateLimiter lightsRateLimit = new RateLimiter(1.3);

    public OI(
            Drive drive,
            Shoulder shoulder,
            Elevator elevator,
            Wrist wrist,
            Intake intake,
            Lights lights) {
        super(reservations(drive, shoulder, elevator, wrist, intake, lights));

        loggerCategory = Category.OPERATOR_INTERFACE;

        this.drive = drive;
        this.shoulder = shoulder;
        this.elevator = elevator;
        this.wrist = wrist;
        this.intake = intake;
        this.lights = lights;

        leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
        boxopGamepad = RobotProvider.instance.getJoystick(InputConstants.BOXOP_GAMEPAD);

        driverOI = new DriverOI(drive, leftJoystick, rightJoystick);
    }

    public void run(Context context) {
        boolean elevatorManual = false;
        boolean wristManual = false;

        while (true) {
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();

            SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());

            // Add driver controls here.

            // Driver OI: take input from left, right joysticks.  control drive.
            driverOI.handleOI(context);

            if (leftJoystick.getButtonPressed(InputConstants.INTAKE_OUT)) {
                context.runSync(new IntakeOut(intake));
            } else if (leftJoystick.getButtonReleased(InputConstants.INTAKE_OUT)) {
                context.runSync(new IntakeStop(intake));
            }

            // Respond to boxop commands

            // first, check if the boxop is making a cone or cube selection
            if (boxopGamepad.getPOV() == InputConstants.POV_UP) {
                context.runSync(new GoForCones(intake));
                setLightsForGamePiece();
                SmartDashboard.putBoolean("Game Piece", true);
            } else if (boxopGamepad.getPOV() == InputConstants.POV_DOWN) {
                context.runSync(new GoForCubes(intake));
                setLightsForGamePiece();
                SmartDashboard.putBoolean("Game Piece", false);
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
                context.runSync(new IntakeIn(intake));
            } else if (boxopGamepad.getButtonReleased(InputConstants.BUTTON_INTAKE_IN)) {
                context.runSync(new IntakeIdle(intake));
            } else if (boxopGamepad.getButton(InputConstants.BUTTON_INTAKE_STOP)) {
                context.runSync(new IntakeStop(intake));
            }

            // look for button hold to extend intake/wrist/elevator superstructure,
            // release to retract
            if (boxopGamepad.getButtonPressed(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {
                switch (placementPosition) {
                    case NONE:
                        break;
                    case LOW_NODE:
                        context.startAsync(new ExtendWristvatorToLow(shoulder, elevator, wrist));
                        break;
                    case MID_NODE:
                        context.startAsync(new ExtendWristvatorToMid(shoulder, elevator, wrist));
                        break;
                    case HIGH_NODE:
                        context.startAsync(new ExtendWristvatorToHigh(shoulder, elevator, wrist));
                        break;
                    case HUMAN_PLAYER:
                        context.startAsync(
                                new ExtendToHumanWithIntake(
                                        intake.getGamePieceType(),
                                        shoulder,
                                        elevator,
                                        wrist,
                                        intake));
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
                    context.startAsync(
                            new RetractWristvatorIdleIntake(shoulder, elevator, wrist, intake));
                } else {
                    context.startAsync(new RetractWristvator(shoulder, elevator, wrist));
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
                    // Robot.elevator.nudgeNoPID(elevatorNudgeAxis);
                    if (elevatorNudgeAxis > 0) {
                        elevator.nudgeUp();
                    } else {
                        elevator.nudgeDown();
                    }
                } else if (false && elevatorManual) {
                    elevator.stopElevator();
                    elevatorManual = false;
                }

                // look for wrist nudges
                double wristNudgeAxis =
                        -1 * boxopGamepad.getAxis(InputConstants.AXIS_WRIST_MOVEMENT);
                if (Math.abs(wristNudgeAxis) > 0.05) {
                    // wristManual = true;
                    // Robot.wrist.nudgeNoPID(wristNudgeAxis);
                    if (wristNudgeAxis > 0) {
                        wrist.nudgeUp();
                    } else {
                        wrist.nudgeDown();
                    }
                } else if (false && wristManual) {
                    wrist.stopWrist();
                    wristManual = true;
                }
            }

            if (lightsRateLimit.next()) {
                if (DriverStation.getMatchTime() > 0 && DriverStation.getMatchTime() < 17) {
                    lights.rainbow();
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
        if (intake.getGamePieceType() == GamePieceType.CUBE) {
            lights.purple();
        } else {
            lights.yellow();
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
