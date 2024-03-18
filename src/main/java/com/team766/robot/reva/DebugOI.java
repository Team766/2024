package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;

/**
 * Programmer-centric controls to test each of our (non-drive) mechanisms.
 * Useful for tuning and for testing, eg in the pit.
 *
 * Uses a DOIO KB16 macropad, as follows:
 *
 *
 *      ┌───┬───┬───┬───┐   12<──  12<──
 *      │ 1 │ 2 │ 3 │ 4 │   ( 3 )  ( 4 )
 *      ├───┼───┼───┼───┤    -─>8  -─>8
 *      │ 5 │ 6 │ 7 │ 8 │
 *      ├───┼───┼───┼───┤
 *      │ 9 │ 10| 11│ 12|     12<──
 *      ├───┼───┼───┼───┤      (   )
 *      │ 13│ 14│ 15│ 16│      -─>8
 *      └───┴───┴───┴───┘
 *
 * 1 + 8/12 = Control Shoulder + Nudge Up/Down
 * 2 + 8/12 = Control Shooter + Nudge Up/Down
 * 3 + 8/12 = Control Left Climber + Nudge Up/Down (BYPASSES SOFT LIMITS)
 * 4 + 8/12 = Control Right Climber + Nudge Up/Down (BYPASSES SOFT LIMITS)
 * 5        = Intake In
 * 6        = Intake Out
 * 16       = Resets climber relative encoders to 0.
 */
public class DebugOI extends OIFragment {
    private final JoystickReader macropad;

    private final Shoulder shoulder;
    private final Climber climber;
    private final Intake intake;
    private final Shooter shooter;
    private final OICondition controlShoulder;
    private final OICondition controlLeftClimber;
    private final OICondition controlRightClimber;
    private final OICondition controlShooter;
    private final OICondition intakeIn;
    private final OICondition intakeOut;

    public DebugOI(
            JoystickReader macropad,
            Shoulder shoulder,
            Climber climber,
            Intake intake,
            Shooter shooter) {
        this.macropad = macropad;
        this.shoulder = shoulder;
        this.climber = climber;
        this.intake = intake;
        this.shooter = shooter;

        controlShoulder =
                new OICondition(() -> macropad.getButton(InputConstants.CONTROL_SHOULDER));
        controlShooter = new OICondition(() -> macropad.getButton(InputConstants.CONTROL_SHOOTER));
        controlLeftClimber =
                new OICondition(() -> macropad.getButton(InputConstants.CONTROL_LEFT_CLIMBER));
        controlRightClimber =
                new OICondition(() -> macropad.getButton(InputConstants.CONTROL_RIGHT_CLIMBER));
        intakeIn = new OICondition(() -> macropad.getButton(InputConstants.INTAKE_IN));
        intakeOut = new OICondition(() -> macropad.getButton(InputConstants.INTAKE_OUT));
    }

    @Override
    protected void handleOI(Context context) {
        // fine-grained control of the shoulder
        // used for testing and tuning
        // press down the shoulder control button and nudge the angle up and down
        if (controlShoulder.isTriggering()) {
            if (controlShoulder.isNewlyTriggering()) {
                context.takeOwnership(shoulder);
            }

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                shoulder.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                shoulder.nudgeDown();
            }
        } else if (controlShoulder.isFinishedTriggering()) {
            context.releaseOwnership(shoulder);
        }

        // fine-grained control of the climber
        // used for testing and tuning
        // press down the climber control buttons and nudge the climber up and down
        // NOTE: this bypasses the soft limits - use with care
        if (controlLeftClimber.isTriggering()) {
            if (controlLeftClimber.isNewlyTriggering()) {
                context.takeOwnership(climber);
                climber.enableSoftLimits(false);
            }

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                climber.setLeftPower(0.25);
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                climber.setLeftPower(-0.25);
            } else if (macropad.getButtonReleased(InputConstants.NUDGE_UP)
                    || macropad.getButtonReleased(InputConstants.NUDGE_DOWN)) {
                climber.stopLeft();
            }
        } else if (controlLeftClimber.isFinishedTriggering()) {
            climber.stopLeft();
            climber.enableSoftLimits(true);
            context.releaseOwnership(climber);
        }

        if (controlRightClimber.isTriggering()) {
            if (controlRightClimber.isNewlyTriggering()) {
                context.takeOwnership(climber);
                climber.enableSoftLimits(false);
            }

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                climber.setRightPower(0.25);
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                climber.setRightPower(-0.25);
            } else if (macropad.getButtonReleased(InputConstants.NUDGE_UP)
                    || macropad.getButtonReleased(InputConstants.NUDGE_DOWN)) {
                climber.stopRight();
            }
        } else if (controlRightClimber.isFinishedTriggering()) {
            climber.stopRight();
            climber.enableSoftLimits(true);
            context.releaseOwnership(climber);
        }

        if (macropad.getButtonPressed(InputConstants.RESET_CLIMBER_ENCODERS)) {
            climber.resetLeftPosition();
            climber.resetRightPosition();
        }

        // simple one-button controls for intake
        // used for testing and tuning
        // allows for running intake at default intake/outtake speeds.
        if (intakeIn.isNewlyTriggering()) {
            context.takeOwnership(intake);
            intake.in();
        } else if (intakeOut.isNewlyTriggering()) {
            context.takeOwnership(intake);
            intake.out();
        } else if (intakeIn.isFinishedTriggering() || intakeOut.isFinishedTriggering()) {
            intake.stop();
            context.releaseOwnership(intake);
        }

        // fine-grained controls for shooter
        // used for testing and tuning
        // press down the intake control button and nudge ths shooter speed up and down
        if (controlShooter.isTriggering()) {
            if (controlShooter.isNewlyTriggering()) {
                context.takeOwnership(shooter);
                Robot.shooter.shoot();
            }

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                shooter.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                shooter.nudgeDown();
            }

        } else if (controlShooter.isFinishedTriggering()) {
            shooter.stop();
            context.releaseOwnership(shooter);
        }
    }
}
