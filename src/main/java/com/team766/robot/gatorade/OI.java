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

    public record Status(GamePieceType gamePieceType, PlacementPosition placementPosition) {}

    private final JoystickReader leftJoystick;
    private final JoystickReader rightJoystick;
    private final JoystickReader boxopGamepad;
    private final DriverOI driverOI;

    @AutoLogOutput
    PlacementPosition placementPosition = PlacementPosition.NONE;

    @AutoLogOutput(key = "Game Piece")
    GamePieceType gamePieceType = GamePieceType.CONE;

    public OI() {
        leftJoystick = RobotProvider.instance.getJoystick(this, InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(this, InputConstants.RIGHT_JOYSTICK);
        boxopGamepad = RobotProvider.instance.getJoystick(this, InputConstants.BOXOP_GAMEPAD);

        driverOI = new DriverOI(this, leftJoystick, rightJoystick);
    }

    private void updateStatus() {
        updateStatus(new Status(gamePieceType, placementPosition));
    }

    protected void dispatch() {
        // Add driver controls here.

        // Driver OI: take input from left, right joysticks.  control drive.
        driverOI.run();

        switch (leftJoystick.getButton(InputConstants.BUTTON_INTAKE_OUT)) {
            case IsTriggering -> ifAvailable((Intake intake) ->
                    intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.OUT)));
            case IsFinishedTriggering -> ifAvailable((Intake intake) ->
                    intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.STOP)));
            default -> {}
        }

        // Respond to boxop commands

        // first, check if the boxop is making a cone or cube selection
        if (boxopGamepad.getPOV() == InputConstants.POV_UP) {
            gamePieceType = GamePieceType.CONE;
            updateStatus();
        } else if (boxopGamepad.getPOV() == InputConstants.POV_DOWN) {
            gamePieceType = GamePieceType.CUBE;
            updateStatus();
        }

        // look for button presses to queue placement of intake/wrist/elevator superstructure
        if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_NONE).isTriggering()) {
            placementPosition = PlacementPosition.NONE;
            updateStatus();
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_LOW).isTriggering()) {
            placementPosition = PlacementPosition.LOW_NODE;
            updateStatus();
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_MID).isTriggering()) {
            placementPosition = PlacementPosition.MID_NODE;
            updateStatus();
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HIGH).isTriggering()) {
            placementPosition = PlacementPosition.HIGH_NODE;
            updateStatus();
        } else if (boxopGamepad
                .getButton(InputConstants.BUTTON_PLACEMENT_HUMAN_PLAYER)
                .isTriggering()) {
            placementPosition = PlacementPosition.HUMAN_PLAYER;
            updateStatus();
        }

        // look for button hold to start intake, release to idle intake
        switch (boxopGamepad.getButton(InputConstants.BUTTON_INTAKE_IN)) {
            case IsTriggering -> ifAvailable((Intake intake) ->
                    intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.IN)));
            case IsFinishedTriggering -> ifAvailable((Intake intake) ->
                    intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.IDLE)));
            default -> {}
        }

        if (boxopGamepad.getButton(InputConstants.BUTTON_INTAKE_STOP).isTriggering()) {
            ifAvailable((Intake intake) ->
                    intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.STOP)));
        }

        // look for button hold to extend intake/wrist/elevator superstructure,
        // release to retract
        switch (boxopGamepad.getButton(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {
            case IsNewlyTriggering -> {
                switch (placementPosition) {
                    case NONE:
                        break;
                    case LOW_NODE:
                        ifAvailable((Shoulder shoulder, Elevator elevator, Wrist wrist) ->
                                new ExtendWristvatorToLow(shoulder, elevator, wrist));
                        break;
                    case MID_NODE:
                        ifAvailable((Shoulder shoulder, Elevator elevator, Wrist wrist) ->
                                new ExtendWristvatorToMid(shoulder, elevator, wrist));
                        break;
                    case HIGH_NODE:
                        ifAvailable((Shoulder shoulder, Elevator elevator, Wrist wrist) ->
                                new ExtendWristvatorToHigh(shoulder, elevator, wrist));
                        break;
                    case HUMAN_PLAYER:
                        ifAvailable(
                                (Shoulder shoulder,
                                        Elevator elevator,
                                        Wrist wrist,
                                        Intake intake) -> new ExtendToHumanWithIntake(
                                        gamePieceType, shoulder, elevator, wrist, intake));
                        break;
                    default:
                        // warn, ignore
                        log(
                                Severity.WARNING,
                                "Unexpected placement position: " + placementPosition.toString());
                        break;
                }
            }
            case IsFinishedTriggering -> {
                if (placementPosition == PlacementPosition.HUMAN_PLAYER) {
                    ifAvailable(
                            (Shoulder shoulder, Elevator elevator, Wrist wrist, Intake intake) ->
                                    Commands.sequence(
                                            new RetractWristvator(shoulder, elevator, wrist),
                                            intake.setGoalBehavior(new Intake.Status(
                                                    gamePieceType, Intake.MotorState.IDLE))));
                } else {
                    ifAvailable((Shoulder shoulder, Elevator elevator, Wrist wrist) ->
                            new RetractWristvator(shoulder, elevator, wrist));
                }
            }

                // look for manual nudges
                // we only allow these if the extend elevator trigger is extended
            case IsTriggering -> {
                // look for elevator nudges
                final double elevatorNudgeAxis =
                        -1 * boxopGamepad.getAxis(InputConstants.AXIS_ELEVATOR_MOVEMENT);
                if (Math.abs(elevatorNudgeAxis) > 0.05) {
                    // tryUsing(() ->
                    //     reserve(elevator).setGoal(new Elevator.NudgeNoPID(elevatorNudgeAxis)));
                    if (elevatorNudgeAxis > 0) {
                        ifAvailable(
                                (Elevator elevator) -> elevator.setGoal(new Elevator.NudgeUp()));
                    } else {
                        ifAvailable(
                                (Elevator elevator) -> elevator.setGoal(new Elevator.NudgeDown()));
                    }
                }
                // look for wrist nudges
                final double wristNudgeAxis =
                        -1 * boxopGamepad.getAxis(InputConstants.AXIS_WRIST_MOVEMENT);
                if (Math.abs(wristNudgeAxis) > 0.05) {
                    // tryUsing(() -> reserve(wrist).setGoal(new Wrist.NudgeNoPID(wristNudgeAxis)));
                    if (wristNudgeAxis > 0) {
                        ifAvailable((Wrist wrist) -> wrist.setGoal(new Wrist.NudgeUp()));
                    } else {
                        ifAvailable((Wrist wrist) -> wrist.setGoal(new Wrist.NudgeDown()));
                    }
                }
            }
            default -> {}
        }

        byDefault((Elevator elevator) -> elevator.setGoal(new Elevator.StopElevator()));
        byDefault((Wrist wrist) -> wrist.setGoal(new Wrist.StopWrist()));
    }
}
