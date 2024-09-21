package com.team766.robot.gatorade;

import static com.team766.framework3.RulePersistence.*;
import static com.team766.framework3.StatusBus.publishStatus;

import com.team766.framework3.Rule;
import com.team766.framework3.RuleEngine;
import com.team766.framework3.Status;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.robot.common.DriverOI;
import com.team766.robot.common.mechanisms.*;
import com.team766.robot.gatorade.constants.InputConstants;
import com.team766.robot.gatorade.mechanisms.*;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.procedures.*;
import java.util.Set;

/**
 * This class is the glue that binds the controls on the physical operator interface to the code
 * that allow control of the robot.
 */
public class OI extends RuleEngine {

    public record OIStatus(GamePieceType gamePieceType, PlacementPosition placementPosition)
            implements Status {}

    PlacementPosition placementPosition = PlacementPosition.NONE;

    GamePieceType gamePieceType = GamePieceType.CONE;

    public OI(SwerveDrive drive, Superstructure ss, Intake intake) {
        final JoystickReader leftJoystick =
                RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
        final JoystickReader rightJoystick =
                RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
        final JoystickReader boxopGamepad =
                RobotProvider.instance.getJoystick(InputConstants.BOXOP_GAMEPAD);

        // Driver OI: take input from left, right joysticks.  control drive.
        new DriverOI(this, leftJoystick, rightJoystick, drive);

        addRule(
                Rule.create(
                                "Intake Out",
                                () -> leftJoystick.getButton(InputConstants.BUTTON_INTAKE_OUT))
                        .withOnTriggeringProcedure(
                                ONCE_AND_HOLD,
                                Set.of(intake),
                                () ->
                                        intake.setRequest(
                                                new Intake.IntakeState(
                                                        gamePieceType, Intake.MotorState.OUT)))
                        .withFinishedTriggeringProcedure(
                                Set.of(intake),
                                () ->
                                        intake.setRequest(
                                                new Intake.IntakeState(
                                                        gamePieceType, Intake.MotorState.STOP))));

        // Respond to boxop commands

        // first, check if the boxop is making a cone or cube selection
        addRule(
                Rule.create("Select Cone", () -> boxopGamepad.getPOV() == InputConstants.POV_UP)
                        .withOnTriggeringProcedure(
                                ONCE,
                                Set.of(),
                                () -> {
                                    gamePieceType = GamePieceType.CONE;
                                    updateStatus();
                                }));
        addRule(
                Rule.create("Select Cube", () -> boxopGamepad.getPOV() == InputConstants.POV_DOWN)
                        .withOnTriggeringProcedure(
                                ONCE,
                                Set.of(),
                                () -> {
                                    gamePieceType = GamePieceType.CUBE;
                                    updateStatus();
                                }));

        // look for button presses to queue placement of intake/wrist/elevator superstructure
        addRule(
                Rule.create(
                                "Select none",
                                () -> boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_NONE))
                        .withOnTriggeringProcedure(
                                ONCE,
                                Set.of(),
                                () -> {
                                    placementPosition = PlacementPosition.NONE;
                                    updateStatus();
                                }));
        addRule(
                Rule.create(
                                "Select low",
                                () -> boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_LOW))
                        .withOnTriggeringProcedure(
                                ONCE,
                                Set.of(),
                                () -> {
                                    placementPosition = PlacementPosition.LOW_NODE;
                                    updateStatus();
                                }));
        addRule(
                Rule.create(
                                "Select mid",
                                () -> boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_MID))
                        .withOnTriggeringProcedure(
                                ONCE,
                                Set.of(),
                                () -> {
                                    placementPosition = PlacementPosition.MID_NODE;
                                    updateStatus();
                                }));
        addRule(
                Rule.create(
                                "Select high",
                                () -> boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HIGH))
                        .withOnTriggeringProcedure(
                                ONCE,
                                Set.of(),
                                () -> {
                                    placementPosition = PlacementPosition.HIGH_NODE;
                                    updateStatus();
                                }));
        addRule(
                Rule.create(
                                "Select human",
                                () ->
                                        boxopGamepad.getButton(
                                                InputConstants.BUTTON_PLACEMENT_HUMAN_PLAYER))
                        .withOnTriggeringProcedure(
                                ONCE,
                                Set.of(),
                                () -> {
                                    placementPosition = PlacementPosition.HUMAN_PLAYER;
                                    updateStatus();
                                }));

        // look for button hold to start intake, release to idle intake
        addRule(
                Rule.create(
                                "Intake In",
                                () -> boxopGamepad.getButton(InputConstants.BUTTON_INTAKE_IN))
                        .withOnTriggeringProcedure(
                                ONCE_AND_HOLD,
                                Set.of(intake),
                                () ->
                                        intake.setRequest(
                                                new Intake.IntakeState(
                                                        gamePieceType, Intake.MotorState.IN)))
                        .withFinishedTriggeringProcedure(
                                Set.of(intake),
                                () ->
                                        intake.setRequest(
                                                new Intake.IntakeState(
                                                        gamePieceType, Intake.MotorState.IDLE))));

        addRule(
                Rule.create(
                                "Intake Stop",
                                () -> boxopGamepad.getButton(InputConstants.BUTTON_INTAKE_STOP))
                        .withOnTriggeringProcedure(
                                ONCE,
                                Set.of(intake),
                                () ->
                                        intake.setRequest(
                                                new Intake.IntakeState(
                                                        gamePieceType, Intake.MotorState.STOP))));

        // look for button hold to extend intake/wrist/elevator superstructure,
        // release to retract
        addRule(
                Rule.create(
                                "Extend Wristvator",
                                () ->
                                        boxopGamepad.getButton(
                                                InputConstants.BUTTON_EXTEND_WRISTVATOR))
                        .withOnTriggeringProcedure(
                                ONCE,
                                Set.of(ss),
                                () -> {
                                    if (placementPosition != PlacementPosition.NONE) {
                                        ss.setRequest(
                                                Superstructure.MoveToPosition.Extended(
                                                        placementPosition, gamePieceType));
                                    }
                                })
                        .withFinishedTriggeringProcedure(
                                Set.of(ss, intake),
                                () -> {
                                    ss.setRequest(Superstructure.MoveToPosition.RETRACTED);
                                    if (placementPosition == PlacementPosition.HUMAN_PLAYER) {
                                        intake.setRequest(
                                                new Intake.IntakeState(
                                                        gamePieceType, Intake.MotorState.IDLE));
                                    }
                                }));

        // look for manual nudges
        // we only allow these if the extend elevator trigger is extended

        boxopGamepad.setAllAxisDeadzone(0.05);

        // look for elevator nudges
        addRule(
                Rule.create(
                                "Elevator nudge",
                                () ->
                                        boxopGamepad.getButton(
                                                        InputConstants.BUTTON_EXTEND_WRISTVATOR)
                                                && boxopGamepad.isAxisMoved(
                                                        InputConstants.AXIS_ELEVATOR_MOVEMENT))
                        .withOnTriggeringProcedure(
                                REPEATEDLY,
                                Set.of(ss),
                                () -> {
                                    final double elevatorNudgeAxis =
                                            -1
                                                    * boxopGamepad.getAxis(
                                                            InputConstants.AXIS_ELEVATOR_MOVEMENT);
                                    // elevator.setRequest(new
                                    // Elevator.NudgeNoPID(elevatorNudgeAxis)));
                                    if (elevatorNudgeAxis > 0) {
                                        ss.setRequest(Superstructure.makeNudgeElevatorUp());
                                    } else {
                                        ss.setRequest(Superstructure.makeNudgeElevatorDown());
                                    }
                                }));
        // look for wrist nudges
        addRule(
                Rule.create(
                                "Elevator nudge",
                                () ->
                                        boxopGamepad.getButton(
                                                        InputConstants.BUTTON_EXTEND_WRISTVATOR)
                                                && boxopGamepad.isAxisMoved(
                                                        InputConstants.AXIS_WRIST_MOVEMENT))
                        .withOnTriggeringProcedure(
                                REPEATEDLY,
                                Set.of(ss),
                                () -> {
                                    final double wristNudgeAxis =
                                            -1
                                                    * boxopGamepad.getAxis(
                                                            InputConstants.AXIS_WRIST_MOVEMENT);
                                    // wrist.setRequest(new Wrist.NudgeNoPID(wristNudgeAxis));
                                    if (wristNudgeAxis > 0) {
                                        ss.setRequest(Superstructure.makeNudgeWristUp());
                                    } else {
                                        ss.setRequest(Superstructure.makeNudgeWristDown());
                                    }
                                }));
    }

    private void updateStatus() {
        publishStatus(new OIStatus(gamePieceType, placementPosition));
    }
}
