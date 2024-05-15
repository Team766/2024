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

        leftJoystick = RobotProvider.instance.getJoystick(this, InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(this, InputConstants.RIGHT_JOYSTICK);
        boxopGamepad = RobotProvider.instance.getJoystick(this, InputConstants.BOXOP_GAMEPAD);

        driverOI = new DriverOI(this, drive, leftJoystick, rightJoystick);
    }

    protected void dispatch() {
        // Add driver controls here.

        // Driver OI: take input from left, right joysticks.  control drive.
        driverOI.run();

        leftJoystick
                .getButton(InputConstants.BUTTON_INTAKE_OUT)
                .whileTriggering(() -> intake.setGoalBehavior(
                        new Intake.State(gamePieceType, Intake.MotorState.OUT)))
                .ifFinishedTriggering(() -> intake.setGoalBehavior(
                        new Intake.State(gamePieceType, Intake.MotorState.STOP)));

        // Respond to boxop commands

        // first, check if the boxop is making a cone or cube selection
        if (boxopGamepad.getPOV() == InputConstants.POV_UP) {
            gamePieceType = GamePieceType.CONE;
        } else if (boxopGamepad.getPOV() == InputConstants.POV_DOWN) {
            gamePieceType = GamePieceType.CUBE;
        }

        // look for button presses to queue placement of intake/wrist/elevator superstructure
        if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_NONE).isTriggering()) {
            placementPosition = PlacementPosition.NONE;
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_LOW).isTriggering()) {
            placementPosition = PlacementPosition.LOW_NODE;
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_MID).isTriggering()) {
            placementPosition = PlacementPosition.MID_NODE;
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HIGH).isTriggering()) {
            placementPosition = PlacementPosition.HIGH_NODE;
        } else if (boxopGamepad
                .getButton(InputConstants.BUTTON_PLACEMENT_HUMAN_PLAYER)
                .isTriggering()) {
            placementPosition = PlacementPosition.HUMAN_PLAYER;
        }

        // look for button hold to start intake, release to idle intake
        boxopGamepad
                .getButton(InputConstants.BUTTON_INTAKE_IN)
                .whileTriggering(() -> intake.setGoalBehavior(
                        new Intake.State(gamePieceType, Intake.MotorState.IN)))
                .ifFinishedTriggering(() -> intake.setGoalBehavior(
                        new Intake.State(gamePieceType, Intake.MotorState.IDLE)));

        boxopGamepad
                .getButton(InputConstants.BUTTON_INTAKE_STOP)
                .whileTriggering(() -> intake.setGoalBehavior(
                        new Intake.State(gamePieceType, Intake.MotorState.STOP)));

        // look for button hold to extend intake/wrist/elevator superstructure,
        // release to retract
        boxopGamepad
                .getButton(InputConstants.BUTTON_EXTEND_WRISTVATOR)
                .ifNewlyTriggering(() -> {
                    switch (placementPosition) {
                        case NONE:
                            return null;
                        case LOW_NODE:
                            return new ExtendWristvatorToLow(shoulder, elevator, wrist);
                        case MID_NODE:
                            return new ExtendWristvatorToMid(shoulder, elevator, wrist);
                        case HIGH_NODE:
                            return new ExtendWristvatorToHigh(shoulder, elevator, wrist);
                        case HUMAN_PLAYER:
                            return new ExtendToHumanWithIntake(
                                    gamePieceType, shoulder, elevator, wrist, intake);
                    }
                    // warn, ignore
                    log(
                            Severity.WARNING,
                            "Unexpected placement position: " + placementPosition.toString());
                    return null;
                })
                .ifFinishedTriggering(() -> {
                    if (placementPosition == PlacementPosition.HUMAN_PLAYER) {
                        return Commands.sequence(
                                new RetractWristvator(shoulder, elevator, wrist),
                                intake.setGoalBehavior(
                                        new Intake.State(gamePieceType, Intake.MotorState.IDLE)));
                    } else {
                        return new RetractWristvator(shoulder, elevator, wrist);
                    }
                });

        /* TODO: this is fully procedural style. Do we want this?
        Validation would be based on line coverage. */
        // look for manual nudges
        // we only allow these if the extend elevator trigger is extended
        boxopGamepad.getButton(InputConstants.BUTTON_EXTEND_WRISTVATOR).whileTriggering(() -> {
            // look for elevator nudges
            final double elevatorNudgeAxis =
                    -1 * boxopGamepad.getAxis(InputConstants.AXIS_ELEVATOR_MOVEMENT);
            if (Math.abs(elevatorNudgeAxis) > 0.05) {
                // tryScheduling(
                //     elevator.setGoalBehavior(new
                // Elevator.NudgeNoPID(elevatorNudgeAxis)));
                if (elevatorNudgeAxis > 0) {
                    tryScheduling(elevator.setGoalBehavior(new Elevator.NudgeUp()));
                } else {
                    tryScheduling(elevator.setGoalBehavior(new Elevator.NudgeDown()));
                }
            }
            // look for wrist nudges
            final double wristNudgeAxis =
                    -1 * boxopGamepad.getAxis(InputConstants.AXIS_WRIST_MOVEMENT);
            if (Math.abs(wristNudgeAxis) > 0.05) {
                // tryScheduling(wrist.setGoalBehavior(new
                // Wrist.NudgeNoPID(wristNudgeAxis)));
                if (wristNudgeAxis > 0) {
                    tryScheduling(wrist.setGoalBehavior(new Wrist.NudgeUp()));
                } else {
                    tryScheduling(wrist.setGoalBehavior(new Wrist.NudgeDown()));
                }
            }
        });

        byDefault(elevator.setGoalBehavior(new Elevator.StopElevator()));
        byDefault(wrist.setGoalBehavior(new Wrist.StopWrist()));
    }
}
