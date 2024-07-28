package com.team766.robot.gatorade;

import static com.team766.framework.resources.Guarded.guard;

import com.team766.framework.OIBase;
import com.team766.framework.resources.Guarded;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.robot.common.DriverOI;
import com.team766.robot.common.mechanisms.*;
import com.team766.robot.gatorade.constants.InputConstants;
import com.team766.robot.gatorade.mechanisms.*;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.procedures.*;
import java.util.Optional;
import org.littletonrobotics.junction.AutoLogOutput;

/**
 * This class is the glue that binds the controls on the physical operator interface to the code
 * that allow control of the robot.
 */
public class OI extends OIBase {

    public record Status(
            GamePieceType gamePieceType, Optional<PlacementPosition> placementPosition) {}

    private final JoystickReader leftJoystick;
    private final JoystickReader rightJoystick;
    private final JoystickReader boxopGamepad;
    private final DriverOI driverOI;
    private final Guarded<Superstructure> ss;
    private final Guarded<Intake> intake;

    @AutoLogOutput
    Optional<PlacementPosition> placementPosition = Optional.empty();

    @AutoLogOutput(key = "Game Piece")
    GamePieceType gamePieceType = GamePieceType.CONE;

    public OI(Drive drive, Superstructure ss, Intake intake) {
        leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
        boxopGamepad = RobotProvider.instance.getJoystick(InputConstants.BOXOP_GAMEPAD);
        this.ss = guard(ss);
        this.intake = guard(intake);

        driverOI = new DriverOI(this, leftJoystick, rightJoystick, drive);
    }

    private void updateStatus() {
        updateStatus(new Status(gamePieceType, placementPosition));
    }

    protected void dispatch() {
        // Add driver controls here.

        // Driver OI: take input from left, right joysticks.  control drive.
        driverOI.run();

        if (leftJoystick.getButton(InputConstants.BUTTON_INTAKE_OUT)) {
            whileAvailable(
                    intake,
                    (Intake intake) -> intake.setGoal(
                            new Intake.Status(gamePieceType, Intake.MotorState.OUT)));
        } else {
            byDefault(
                    intake,
                    (Intake intake) -> intake.setGoal(
                            new Intake.Status(gamePieceType, Intake.MotorState.STOP)));
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
        if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_NONE)) {
            placementPosition = Optional.empty();
            updateStatus();
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_LOW)) {
            placementPosition = Optional.of(PlacementPosition.LOW_NODE);
            updateStatus();
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_MID)) {
            placementPosition = Optional.of(PlacementPosition.MID_NODE);
            updateStatus();
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HIGH)) {
            placementPosition = Optional.of(PlacementPosition.HIGH_NODE);
            updateStatus();
        } else if (boxopGamepad.getButton(InputConstants.BUTTON_PLACEMENT_HUMAN_PLAYER)) {
            placementPosition = Optional.of(PlacementPosition.HUMAN_PLAYER);
            updateStatus();
        }

        // look for button hold to start intake, release to idle intake
        if (boxopGamepad.getButton(InputConstants.BUTTON_INTAKE_IN)) {
            whileAvailable(
                    intake,
                    (Intake intake) ->
                            intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.IN)));
        } else {
            byDefault(
                    intake,
                    (Intake intake) -> intake.setGoal(
                            new Intake.Status(gamePieceType, Intake.MotorState.IDLE)));
        }

        if (boxopGamepad.getButton(InputConstants.BUTTON_INTAKE_STOP)) {
            whileAvailable(
                    intake,
                    (Intake intake) -> intake.setGoal(
                            new Intake.Status(gamePieceType, Intake.MotorState.STOP)));
        }

        // look for button hold to extend intake/wrist/elevator superstructure,
        // release to retract
        if (boxopGamepad.getButton(InputConstants.BUTTON_EXTEND_WRISTVATOR)) {
            onceAvailable(ss, (Superstructure ss) -> {
                if (placementPosition.isPresent()) {
                    ss.setGoal(Superstructure.MoveToPosition.Extended(
                            placementPosition.get(), gamePieceType));
                }
            });

            // look for manual nudges
            // we only allow these if the extend elevator trigger is extended

            // look for elevator nudges
            final double elevatorNudgeAxis =
                    -1 * boxopGamepad.getAxis(InputConstants.AXIS_ELEVATOR_MOVEMENT);
            if (Math.abs(elevatorNudgeAxis) > 0.05) {
                // ifAvailable((Elevator elevator) ->
                //     elevator.setGoal(new Elevator.NudgeNoPID(elevatorNudgeAxis)));
                if (elevatorNudgeAxis > 0) {
                    whileAvailable(
                            ss,
                            (Superstructure ss) -> ss.setGoal(Superstructure.NUDGE_ELEVATOR_UP));
                } else {
                    whileAvailable(
                            ss,
                            (Superstructure ss) -> ss.setGoal(Superstructure.NUDGE_ELEVATOR_DOWN));
                }
            }
            // look for wrist nudges
            final double wristNudgeAxis =
                    -1 * boxopGamepad.getAxis(InputConstants.AXIS_WRIST_MOVEMENT);
            if (Math.abs(wristNudgeAxis) > 0.05) {
                // ifAvailable((Wrist wrist) ->
                //     wrist.setGoal(new Wrist.NudgeNoPID(wristNudgeAxis)));
                if (wristNudgeAxis > 0) {
                    whileAvailable(
                            ss, (Superstructure ss) -> ss.setGoal(Superstructure.NUDGE_WRIST_UP));
                } else {
                    whileAvailable(
                            ss, (Superstructure ss) -> ss.setGoal(Superstructure.NUDGE_WRIST_DOWN));
                }
            }
        } else {
            byDefault(ss, intake, (Superstructure ss, Intake intake) -> {
                ss.setGoal(Superstructure.MoveToPosition.RETRACTED);
                if (placementPosition.orElse(null) == PlacementPosition.HUMAN_PLAYER) {
                    intake.setGoal(new Intake.Status(gamePieceType, Intake.MotorState.IDLE));
                }
            });
        }
    }
}
