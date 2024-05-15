package com.team766.robot.gatorade;

import com.team766.framework.OIBase;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Severity;
import com.team766.robot.common.DriverOI;
import com.team766.robot.common.mechanisms.*;
import com.team766.robot.gatorade.constants.InputConstants;
import com.team766.robot.gatorade.mechanisms.*;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.procedures.*;
import edu.wpi.first.wpilibj2.command.Commands;
import org.littletonrobotics.junction.AutoLogOutput;

/**
 * This class is the glue that binds the controls on the physical operator interface to the code
 * that allow control of the robot.
 */
public class OI extends OIBase {

    private final JoystickReader leftJoystick;
    private final JoystickReader rightJoystick;
    private final JoystickReader boxopGamepad;
    private final Drive drive;
    private final Shoulder shoulder;
    private final Elevator elevator;
    private final Wrist wrist;
    private final Intake intake;
    private final DriverOI driverOI;

    double turningValue = 0;
    boolean manualControl = true;
    PlacementPosition placementPosition = PlacementPosition.NONE;

    @AutoLogOutput(key = "Game Piece")
    GamePieceType gamePieceType = GamePieceType.CONE;

    public OI(Drive drive, Shoulder shoulder, Elevator elevator, Wrist wrist, Intake intake) {
        this.drive = drive;
        this.shoulder = shoulder;
        this.elevator = elevator;
        this.wrist = wrist;
        this.intake = intake;

        leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
        boxopGamepad = RobotProvider.instance.getJoystick(InputConstants.BOXOP_GAMEPAD);

        driverOI = new DriverOI(this, drive, leftJoystick, rightJoystick);
    }

    protected void dispatch() {
        // Add driver controls here.

        // Driver OI: take input from left, right joysticks.  control drive.
        driverOI.run();

        new Condition(leftJoystick.getButton(InputConstants.BUTTON_INTAKE_OUT)) {
            protected void ifTriggering() {
                runIfAvailable(() -> intake.setGoalBehavior(
                        new Intake.State(gamePieceType, Intake.MotorState.OUT)));
            }

            protected void ifFinishedTriggering() {
                runIfAvailable(() -> intake.setGoalBehavior(
                        new Intake.State(gamePieceType, Intake.MotorState.STOP)));
            }
        };

        // Respond to boxop commands

        // first, check if the boxop is making a cone or cube selection
        if (boxopGamepad.getPOV() == InputConstants.POV_UP) {
            gamePieceType = GamePieceType.CONE;
        } else if (boxopGamepad.getPOV() == InputConstants.POV_DOWN) {
            gamePieceType = GamePieceType.CUBE;
        }

        // look for button presses to queue placement of intake/wrist/elevator superstructure
        if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_NONE)) {
            placementPosition = PlacementPosition.NONE;
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_LOW)) {
            placementPosition = PlacementPosition.LOW_NODE;
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_MID)) {
            placementPosition = PlacementPosition.MID_NODE;
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HIGH)) {
            placementPosition = PlacementPosition.HIGH_NODE;
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HUMAN_PLAYER)) {
            placementPosition = PlacementPosition.HUMAN_PLAYER;
        }

        // look for button hold to start intake, release to idle intake
        new Condition(boxopGamepad.getButton(InputConstants.BUTTON_INTAKE_IN)) {
            protected void ifTriggering() {
                runIfAvailable(() -> intake.setGoalBehavior(
                        new Intake.State(gamePieceType, Intake.MotorState.IN)));
            }

            protected void ifFinishedTriggering() {
                runIfAvailable(() -> intake.setGoalBehavior(
                        new Intake.State(gamePieceType, Intake.MotorState.IDLE)));
            }
        };

        new Condition(boxopGamepad.getButton(InputConstants.BUTTON_INTAKE_STOP)) {
            protected void ifTriggering() {
                runIfAvailable(() -> intake.setGoalBehavior(
                        new Intake.State(gamePieceType, Intake.MotorState.STOP)));
            }
        };

        // look for button hold to extend intake/wrist/elevator superstructure,
        // release to retract
        new Condition(boxopGamepad.getButton(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {
            protected void ifNewlyTriggering() {
                switch (placementPosition) {
                    case NONE:
                        break;
                    case LOW_NODE:
                        runIfAvailable(() -> new ExtendWristvatorToLow(shoulder, elevator, wrist));
                        break;
                    case MID_NODE:
                        runIfAvailable(() -> new ExtendWristvatorToMid(shoulder, elevator, wrist));
                        break;
                    case HIGH_NODE:
                        runIfAvailable(() -> new ExtendWristvatorToHigh(shoulder, elevator, wrist));
                        break;
                    case HUMAN_PLAYER:
                        runIfAvailable(() -> new ExtendToHumanWithIntake(
                                gamePieceType, shoulder, elevator, wrist, intake));
                        break;
                }
                // warn, ignore
                log(
                        Severity.WARNING,
                        "Unexpected placement position: " + placementPosition.toString());
            }

            protected void ifFinishedTriggering() {
                if (placementPosition == PlacementPosition.HUMAN_PLAYER) {
                    runIfAvailable(() -> Commands.sequence(
                            new RetractWristvator(shoulder, elevator, wrist),
                            intake.setGoalBehavior(
                                    new Intake.State(gamePieceType, Intake.MotorState.IDLE))));
                } else {
                    runIfAvailable(() -> new RetractWristvator(shoulder, elevator, wrist));
                }
            }

            // look for manual nudges
            // we only allow these if the extend elevator trigger is extended
            protected void ifTriggering() {
                // look for elevator nudges
                final double elevatorNudgeAxis =
                        -1 * boxopGamepad.getAxis(InputConstants.AXIS_ELEVATOR_MOVEMENT);
                if (Math.abs(elevatorNudgeAxis) > 0.05) {
                    // runIfAvailable(() -> elevator.setGoalBehavior(
                    //     new Elevator.NudgeNoPID(elevatorNudgeAxis)));
                    if (elevatorNudgeAxis > 0) {
                        runIfAvailable(() -> elevator.setGoalBehavior(new Elevator.NudgeUp()));
                    } else {
                        runIfAvailable(() -> elevator.setGoalBehavior(new Elevator.NudgeDown()));
                    }
                }
                // look for wrist nudges
                final double wristNudgeAxis =
                        -1 * boxopGamepad.getAxis(InputConstants.AXIS_WRIST_MOVEMENT);
                if (Math.abs(wristNudgeAxis) > 0.05) {
                    // runIfAvailable(() -> wrist.setGoalBehavior(
                    //     new Wrist.NudgeNoPID(wristNudgeAxis)));
                    if (wristNudgeAxis > 0) {
                        runIfAvailable(() -> wrist.setGoalBehavior(new Wrist.NudgeUp()));
                    } else {
                        runIfAvailable(() -> wrist.setGoalBehavior(new Wrist.NudgeDown()));
                    }
                }
            }
        };

        byDefault(() -> elevator.setGoalBehavior(new Elevator.StopElevator()));
        byDefault(() -> wrist.setGoalBehavior(new Wrist.StopWrist()));
    }
}
