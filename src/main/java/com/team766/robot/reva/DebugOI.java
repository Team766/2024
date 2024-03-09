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
 * 2 + 8/12 = Control Climber + Nudge Up/Down
 * 3 + 8/12 = Control Intake + Nudge Up/Down
 * 4 + 8/12 = Control Shooter + Nudge Up/Down
 * 13       = Intake In (default speed)
 * 14       = Intake Out (default speed)
 * 15       = Shoot (default speed)
 */
public class DebugOI extends OIFragment {
    private final JoystickReader macropad;

    private final Shoulder shoulder;
    private final Climber climber;
    private final Intake intake;
    private final Shooter shooter;

    public DebugOI(
            JoystickReader macropad,
            Shoulder shoulder,
            Climber climber,
            Intake intake,
            Shooter shooter) {
        super("DebugOI");
        this.macropad = macropad;
        this.shoulder = shoulder;
        this.climber = climber;
        this.intake = intake;
        this.shooter = shooter;

        addRule(
                () -> macropad.getButton(InputConstants.CONTROL_SHOULDER),
                (context) -> controlShoulder(context),
                null);
        addRule(
                () -> macropad.getButton(InputConstants.CONTROL_CLIMBER),
                (context) -> controlClimber(context),
                null);
        addRule(
                () -> macropad.getButtonPressed(InputConstants.INTAKE_IN),
                (context) -> intakeIn(context),
                (context) -> intakeStop(context));
        addRule(
                () -> macropad.getButtonPressed(InputConstants.INTAKE_OUT),
                (context) -> intakeOut(context),
                (context) -> intakeStop(context));
        addRule(
                () -> macropad.getButtonPressed(InputConstants.CONTROL_SHOOTER),
                (context) -> controlShooter(context),
                (context) -> controlShooterDone(context));
    }

    private void controlShoulder(Context context) {
        // Shoulder
        context.takeOwnership(shoulder);

        if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
            shoulder.nudgeUp();
        } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
            shoulder.nudgeDown();
        } else if (macropad.getButtonPressed(InputConstants.MACROPAD_RESET_SHOULDER)) {
            shoulder.reset();
        }
        context.releaseOwnership(shoulder);
    }

    private void controlClimber(Context context) {
        // Climber
        context.takeOwnership(climber);
        climber.goNoPID();
        if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
            climber.nudgeUp();
        } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
            climber.nudgeDown();
        }
        context.releaseOwnership(climber);
    }

    private void controlClimberDone(Context context) {
        context.takeOwnership(climber);
        climber.stop();
        context.releaseOwnership(climber);
    }

    private void intakeIn(Context context) {
        context.takeOwnership(intake);
        intake.in();
        context.releaseOwnership(intake);
    }

    private void intakeOut(Context context) {
        context.takeOwnership(intake);
        intake.out();
        context.releaseOwnership(intake);
    }

    private void intakeStop(Context context) {
        context.takeOwnership(intake);
        intake.stop();
        context.releaseOwnership(intake);
    }

    private void controlShooter(Context context) {
        context.takeOwnership(shooter);
        Robot.shooter.shoot();

        if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
            shooter.nudgeUp();
        } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
            shooter.nudgeDown();
        }
        context.releaseOwnership(shooter);
    }

    private void controlShooterDone(Context context) {
        context.takeOwnership(shooter);
        shooter.stop();
        context.releaseOwnership(shooter);
    }
}
