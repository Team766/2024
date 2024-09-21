package com.team766.robot.reva;

import static com.team766.framework3.Conditions.checkForStatusWith;
import static com.team766.framework3.RulePersistence.*;

import com.team766.framework3.Rule;
import com.team766.framework3.RuleEngine;
import com.team766.hal.JoystickReader;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.ArmAndClimber;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.procedures.IntakeUntilIn;
import java.util.Set;

public class BoxOpOI {
    public BoxOpOI(
            RuleEngine oi,
            JoystickReader gamepad,
            ArmAndClimber ss,
            Intake intake,
            Shooter shooter) {
        gamepad.setAxisDeadzone(InputConstants.XBOX_LS_Y, ControlConstants.JOYSTICK_DEADZONE);
        gamepad.setAxisDeadzone(InputConstants.XBOX_RS_Y, ControlConstants.JOYSTICK_DEADZONE);

        // climber

        oi.addRule(
                Rule.create(
                                "Climber Mode",
                                () ->
                                        gamepad.getButton(InputConstants.XBOX_A)
                                                && gamepad.getButton(InputConstants.XBOX_B))
                        .withOnTriggeringProcedure(
                                // move the shoulder out of the way
                                ONCE,
                                Set.of(ss),
                                () -> ss.setRequest(Shoulder.RotateToPosition.TOP))
                        .whenTriggering(
                                // if the sticks are being moving, move the corresponding climber(s)
                                Rule.create(
                                                "Move climbers",
                                                () ->
                                                        gamepad.isAxisMoved(
                                                                        InputConstants.XBOX_LS_Y)
                                                                || gamepad.isAxisMoved(
                                                                        InputConstants.XBOX_RS_Y))
                                        .withOnTriggeringProcedure(
                                                REPEATEDLY,
                                                Set.of(ss),
                                                () -> {
                                                    boolean overrideSoftLimits =
                                                            gamepad.getButton(InputConstants.XBOX_X)
                                                                    && gamepad.getButton(
                                                                            InputConstants.XBOX_Y);
                                                    ss.setRequest(
                                                            new Climber.MotorPowers(
                                                                    gamepad.getAxis(
                                                                            InputConstants
                                                                                    .XBOX_LS_Y),
                                                                    gamepad.getAxis(
                                                                            InputConstants
                                                                                    .XBOX_RS_Y),
                                                                    overrideSoftLimits));
                                                })
                                        .withFinishedTriggeringProcedure(
                                                Set.of(ss),
                                                () -> ss.setRequest(new Climber.Stop())))
                        .withFinishedTriggeringProcedure(
                                // restore the shoulder (and stop the climber)
                                Set.of(ss), () -> ss.setRequest(new Shoulder.RotateToPosition(85)))
                        .whenNotTriggering(
                                Rule.create(
                                                "Shoulder to Intake",
                                                () -> gamepad.getButton(InputConstants.XBOX_A))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                Set.of(ss),
                                                () ->
                                                        ss.setRequest(
                                                                Shoulder.RotateToPosition
                                                                        .INTAKE_FLOOR)),
                                Rule.create(
                                                "Shoulder to close shot",
                                                () -> gamepad.getButton(InputConstants.XBOX_B))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                Set.of(ss),
                                                () ->
                                                        ss.setRequest(
                                                                Shoulder.RotateToPosition
                                                                        .SHOOT_LOW)),
                                Rule.create(
                                                "Shoulder to amp shot",
                                                () -> gamepad.getButton(InputConstants.XBOX_X))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                Set.of(ss),
                                                () -> ss.setRequest(Shoulder.RotateToPosition.AMP)),
                                Rule.create(
                                                "Shoulder to assist shot",
                                                () -> gamepad.getButton(InputConstants.XBOX_Y))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                Set.of(ss),
                                                () ->
                                                        ss.setRequest(
                                                                Shoulder.RotateToPosition
                                                                        .SHOOTER_ASSIST))
                                        .whenTriggering(
                                                Rule.create(
                                                                "Spin shooter for assist shot",
                                                                () ->
                                                                        checkForStatusWith(
                                                                                Shooter
                                                                                        .ShooterStatus
                                                                                        .class,
                                                                                s ->
                                                                                        s
                                                                                                        .targetSpeed()
                                                                                                != 0.0))
                                                        .withOnTriggeringProcedure(
                                                                ONCE_AND_HOLD,
                                                                Set.of(shooter),
                                                                () ->
                                                                        shooter.setRequest(
                                                                                Shooter.ShootAtSpeed
                                                                                        .SHOOTER_ASSIST_SPEED))),
                                Rule.create("Nudge Shoulder Up", () -> gamepad.getPOV() == 0)
                                        .withOnTriggeringProcedure(
                                                REPEATEDLY,
                                                Set.of(ss),
                                                () -> ss.setRequest(Shoulder.makeNudgeUp())),
                                Rule.create("Nudge Shoulder Down", () -> gamepad.getPOV() == 180)
                                        .withOnTriggeringProcedure(
                                                REPEATEDLY,
                                                Set.of(ss),
                                                () -> ss.setRequest(Shoulder.makeNudgeDown()))));

        // shooter
        oi.addRule(
                Rule.create("Spin Shooter", () -> gamepad.getAxis(InputConstants.XBOX_RT) > 0)
                        .withOnTriggeringProcedure(
                                ONCE_AND_HOLD,
                                Set.of(shooter),
                                () -> shooter.setRequest(new Shooter.ShootAtSpeed(4800))));

        // intake
        oi.addRule(
                Rule.create("Intake Out", () -> gamepad.getButton(InputConstants.XBOX_RB))
                        .withOnTriggeringProcedure(
                                ONCE_AND_HOLD,
                                Set.of(intake),
                                () -> intake.setRequest(new Intake.Out())));
        oi.addRule(
                Rule.create("Intake Until In", () -> gamepad.getButton(InputConstants.XBOX_LB))
                        .withOnTriggeringProcedure(ONCE_AND_HOLD, () -> new IntakeUntilIn(intake)));

        // // rumble
        // // TODO(MF3): Add the ability to reserve joysticks
        // oi.addRule(Rule.create(
        //                 "Rumble when holding note",
        //                 () -> checkStatus(Intake.IntakeStatus.class, s -> s.hasNoteInIntake()))
        //         .withOnTriggeringProcedure(ONCE_AND_HOLD, Set.of(), () -> ((GenericHID) gamepad)
        //                 .setRumble(RumbleType.kBothRumble, 0.5))
        //         .withFinishedTriggeringProcedure(Set.of(), () -> ((GenericHID) gamepad)
        //                 .setRumble(RumbleType.kBothRumble, 0.0)));
    }
}
